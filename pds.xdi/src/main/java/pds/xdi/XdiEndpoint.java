package pds.xdi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.xdi.events.XdiGraphAddEvent;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphGetEvent;
import pds.xdi.events.XdiGraphListener;
import pds.xdi.events.XdiGraphModEvent;
import pds.xdi.events.XdiTransactionEvent;
import pds.xdi.events.XdiTransactionFailureEvent;
import pds.xdi.events.XdiTransactionSuccessEvent;
import xdi2.client.XDIClient;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.ContextNode;
import xdi2.core.Statement;
import xdi2.core.util.XRIUtil;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.error.ErrorMessageResult;

public class XdiEndpoint {

	private static final Logger log = LoggerFactory.getLogger(XdiEndpoint.class.getName());

	private final XdiClient xdi;
	private final XDIClient xdiClient;
	private final String identifier;
	private final XRI3Segment canonical;
	private final String secretToken;

	private final Map<XRI3Segment, List<XdiGraphListener> > xdiGetGraphListeners;
	private final Map<XRI3Segment, List<XdiGraphListener> > xdiAddGraphListeners;
	private final Map<XRI3Segment, List<XdiGraphListener> > xdiModGraphListeners;
	private final Map<XRI3Segment, List<XdiGraphListener> > xdiDelGraphListeners;

	XdiEndpoint(XdiClient xdi, XDIClient xdiClient, String identifier, XRI3Segment canonical, String secretToken) { 

		this.xdi = xdi;
		this.xdiClient = xdiClient;
		this.identifier = identifier;
		this.canonical = canonical;
		this.secretToken = secretToken;

		this.xdiGetGraphListeners = new HashMap<XRI3Segment, List<XdiGraphListener> > ();
		this.xdiAddGraphListeners = new HashMap<XRI3Segment, List<XdiGraphListener> > ();
		this.xdiModGraphListeners = new HashMap<XRI3Segment, List<XdiGraphListener> > ();
		this.xdiDelGraphListeners = new HashMap<XRI3Segment, List<XdiGraphListener> > ();
	}

	public String getEndpoint() {

		if (this.xdiClient instanceof XDIHttpClient) {

			return ((XDIHttpClient) this.xdiClient).getUrl().toString();
		}

		return null;
	}

	public String getIdentifier() {

		return this.identifier;
	}

	public XRI3Segment getCanonical() {

		return this.canonical;
	}

	public String getSecretToken() {

		return this.secretToken;
	}

	public void checkSecretToken() throws XdiException {

		// $get

		/* TODO		XRI3 operationAddress = new XRI3("" + this.canonical + "/$password");
		Operation operation = this.prepareOperation(XDIMessagingConstants.XRI_GET, operationAddress);
		MessageResult messageResult = this.send(operation);

		if (! Boolean.TRUE.equals(messageResult.getBoolean())) {

			throw new XdiException("Incorrect password.");
		}*/
	}

	public XdiTransactionEvent directXdi(MessageEnvelope messageEnvelope) throws XdiException {

		// do XDI transaction

		Date beginTimestamp = new Date();
		XdiTransactionEvent transactionEvent;

		try {

			MessageResult ret = XdiEndpoint.this.xdiClient.send(messageEnvelope, null);

			transactionEvent = new XdiTransactionSuccessEvent(this, messageEnvelope, beginTimestamp, new Date(), ret);
			this.xdi.fireXdiTransactionEvent(transactionEvent);
		} catch (Exception ex) {

			if (! (ex instanceof XdiException)) ex = new XdiException("Problem during XDI Transaction: " + ex.getMessage(), ex);
			transactionEvent = new XdiTransactionFailureEvent(this, messageEnvelope, beginTimestamp, new Date(), ex);
			this.xdi.fireXdiTransactionEvent(transactionEvent);
		}

		return transactionEvent;
	}

	/* 
	 * Messaging methods
	 */

	public Message prepareMessage() {

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(this.canonical, true);

		if (this.secretToken != null) {

			ContextNode secretTokenContextNode = message.getContextNode().createContextNodes(new XRI3Segment("$($secret)$!($token)"));
			secretTokenContextNode.createLiteral(this.secretToken);
		}

		return message;
	}

	public Message prepareOperation(XRI3Segment operationXri, XRI3Segment targetXri) {

		Message message = this.prepareMessage();

		message.createOperation(operationXri, targetXri);

		return message;
	}

	public Message prepareOperation(XRI3Segment operationXri, Statement targetStatement) {

		Message message = this.prepareMessage();

		message.createOperation(operationXri, targetStatement);

		return message;
	}

	public Message prepareOperations(XRI3Segment operationXri, XRI3Segment[] targetXris) {

		Message message = this.prepareMessage();

		for (XRI3Segment targetXri : targetXris) {

			message.createOperation(operationXri, targetXri);
		}

		return message;
	}

	public Message prepareOperations(XRI3Segment operationXri, Iterator<Statement> targetStatements) {

		Message message = this.prepareMessage();

		while (targetStatements.hasNext()) {
			
			Statement targetStatement = targetStatements.next();

			message.createOperation(operationXri, targetStatement);
		}

		return message;
	}

	/*
	 * Sending methods
	 */

	public MessageResult send(Operation operation) throws XdiException {

		return this.send(operation.getMessage());
	}

	public MessageResult send(Message message) throws XdiException {

		return this.send(message.getMessageEnvelope());
	}

	public MessageResult send(MessageEnvelope messageEnvelope) throws XdiException {

		// send the message envelope

		MessageResult messageResult = this.xdi.send(this.xdiClient, messageEnvelope);

		// check modified addresses

		for (Iterator<Operation> operations = messageEnvelope.getOperations(); operations.hasNext(); ) {

			Operation operation = operations.next();

			if (operation.isWriteOperation()) {

				XRI3Segment operationXri = operation.getOperationXri();
				XRI3Segment targetXri = operation.getTarget();

				this.fireXdiGraphEvent(operationXri, targetXri);
			}
		}

		// check for eerrors

		if (ErrorMessageResult.isValid(messageResult.getGraph())) {

			messageResult = ErrorMessageResult.fromGraph(messageResult.getGraph());
			throw new XdiException("Problem from XDI Server: " + ((ErrorMessageResult) messageResult).getErrorString());
		}

		// done

		return messageResult;
	}

	/*
	 * Listener methods
	 */

	private void addXdiGraphListener(Map<XRI3Segment, List<XdiGraphListener>> xdiGraphListeners, XRI3Segment address, XdiGraphListener xdiGraphListener) {

		List<XdiGraphListener > addressXdiObjectListeners = xdiGraphListeners.get(address);
		if (addressXdiObjectListeners == null) {

			addressXdiObjectListeners = new ArrayList<XdiGraphListener > ();
			xdiGraphListeners.put(address, addressXdiObjectListeners);
		}

		addressXdiObjectListeners.add(xdiGraphListener);
	}

	public void addXdiGraphListener(XdiGraphListener xdiGraphListener) {

		for (XRI3Segment address : xdiGraphListener.xdiGetAddresses()) this.addXdiGraphListener(this.xdiGetGraphListeners, address, xdiGraphListener);
		for (XRI3Segment address : xdiGraphListener.xdiAddAddresses()) this.addXdiGraphListener(this.xdiAddGraphListeners, address, xdiGraphListener);
		for (XRI3Segment address : xdiGraphListener.xdiModAddresses()) this.addXdiGraphListener(this.xdiModGraphListeners, address, xdiGraphListener);
		for (XRI3Segment address : xdiGraphListener.xdiDelAddresses()) this.addXdiGraphListener(this.xdiDelGraphListeners, address, xdiGraphListener);
	}

	public void removeXdiGraphListener(Map<XRI3Segment, List<XdiGraphListener> > xdiGraphListeners, XRI3Segment address, XdiGraphListener xdiGraphListener) {

		List<XdiGraphListener > addressXdiObjectListeners = xdiGraphListeners.get(address);
		if (addressXdiObjectListeners == null) return;

		if (addressXdiObjectListeners.contains(xdiGraphListener)) {

			addressXdiObjectListeners.remove(xdiGraphListener);
		}

		if (addressXdiObjectListeners.isEmpty()) {

			xdiGraphListeners.remove(address);
		}
	}

	public void removeXdiGraphListener(XdiGraphListener xdiGraphListener) {

		for (XRI3Segment address : xdiGraphListener.xdiGetAddresses()) this.removeXdiGraphListener(this.xdiGetGraphListeners, address, xdiGraphListener);
		for (XRI3Segment address : xdiGraphListener.xdiAddAddresses()) this.removeXdiGraphListener(this.xdiAddGraphListeners, address, xdiGraphListener);
		for (XRI3Segment address : xdiGraphListener.xdiModAddresses()) this.removeXdiGraphListener(this.xdiModGraphListeners, address, xdiGraphListener);
		for (XRI3Segment address : xdiGraphListener.xdiDelAddresses()) this.removeXdiGraphListener(this.xdiDelGraphListeners, address, xdiGraphListener);
	}

	public void fireXdiGraphEvent(XRI3Segment operationXri, XRI3Segment targetXri) {

		Set<XdiGraphListener> needFireXdiGraphListeners = new HashSet<XdiGraphListener> ();

		boolean bubbled = false;

		while (targetXri != null) {

			XRI3Segment listenersAddress = bubbled ? new XRI3Segment(targetXri.toString() + "/$$") : targetXri;

			log.debug("Looking for listeners on address " + listenersAddress + " (" + operationXri + ")");

			List<XdiGraphListener> xdiGraphListeners;
			xdiGraphListeners = this.xdiGraphListenersForOperationXri(operationXri).get(listenersAddress);

			if (xdiGraphListeners != null) {

				log.debug("Found " + xdiGraphListeners.size() + " listeners on address " + listenersAddress + " (" + operationXri + ")");

				needFireXdiGraphListeners.addAll(xdiGraphListeners);
				break;
			}

			targetXri = XRIUtil.parentXri(targetXri);
			bubbled = true;
		}

		XdiGraphEvent xdiGraphEvent = this.xdiGraphEventForOperationXri(operationXri);

		for (XdiGraphListener needFireXdiGraphListener : needFireXdiGraphListeners) {

			log.debug("Notifying " + needFireXdiGraphListener.getClass().getSimpleName());
			needFireXdiGraphListener.onXdiGraphEvent(xdiGraphEvent);
		}
	}

	private XdiGraphEvent xdiGraphEventForOperationXri(XRI3Segment operationXri) {

		if (XDIMessagingConstants.XRI_S_GET.equals(operationXri)) return new XdiGraphGetEvent(this);
		if (XDIMessagingConstants.XRI_S_ADD.equals(operationXri)) return new XdiGraphAddEvent(this);
		if (XDIMessagingConstants.XRI_S_MOD.equals(operationXri)) return new XdiGraphModEvent(this);
		if (XDIMessagingConstants.XRI_S_DEL.equals(operationXri)) return new XdiGraphDelEvent(this);

		return null;
	}

	private Map<XRI3Segment, List<XdiGraphListener> > xdiGraphListenersForOperationXri(XRI3Segment operationXri) {

		if (XDIMessagingConstants.XRI_S_GET.equals(operationXri)) return this.xdiGetGraphListeners;
		if (XDIMessagingConstants.XRI_S_ADD.equals(operationXri)) return this.xdiAddGraphListeners;
		if (XDIMessagingConstants.XRI_S_MOD.equals(operationXri)) return this.xdiModGraphListeners;
		if (XDIMessagingConstants.XRI_S_DEL.equals(operationXri)) return this.xdiDelGraphListeners;

		return new HashMap<XRI3Segment, List<XdiGraphListener> > ();
	}
}