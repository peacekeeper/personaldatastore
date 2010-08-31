package pds.web.xdi;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.addressing.Addressing;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.MessageEnvelope;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.messaging.client.http.XDIHttpClient;
import org.eclipse.higgins.xdi4j.messaging.error.ErrorMessageResult;
import org.eclipse.higgins.xdi4j.util.CopyUtil;
import org.eclipse.higgins.xdi4j.util.iterators.IteratorArrayMaker;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.xdi.events.XdiGraphAddEvent;
import pds.web.xdi.events.XdiGraphDelEvent;
import pds.web.xdi.events.XdiGraphEvent;
import pds.web.xdi.events.XdiGraphGetEvent;
import pds.web.xdi.events.XdiGraphListener;
import pds.web.xdi.events.XdiGraphModEvent;
import pds.web.xdi.events.XdiTransactionEvent;
import pds.web.xdi.events.XdiTransactionFailureEvent;
import pds.web.xdi.events.XdiTransactionSuccessEvent;

public class XdiContext {

	private static final Log log = LogFactory.getLog(XdiContext.class.getName());

	private final Xdi xdi;
	private final XDIHttpClient xdiClient;
	private final String identifier;
	private final XRI3Segment canonical;
	private final String password;

	private final Map<XRI3, List<XdiGraphListener> > xdiGetGraphListeners;
	private final Map<XRI3, List<XdiGraphListener> > xdiAddGraphListeners;
	private final Map<XRI3, List<XdiGraphListener> > xdiModGraphListeners;
	private final Map<XRI3, List<XdiGraphListener> > xdiDelGraphListeners;

	public XdiContext(Xdi xdi, XDIHttpClient xdiClient, String identifier, XRI3Segment canonical, String password) { 

		this.xdi = xdi;
		this.xdiClient = xdiClient;
		this.identifier = identifier;
		this.canonical = canonical;
		this.password = password;

		this.xdiGetGraphListeners = new HashMap<XRI3, List<XdiGraphListener> > ();
		this.xdiAddGraphListeners = new HashMap<XRI3, List<XdiGraphListener> > ();
		this.xdiModGraphListeners = new HashMap<XRI3, List<XdiGraphListener> > ();
		this.xdiDelGraphListeners = new HashMap<XRI3, List<XdiGraphListener> > ();
	}

	public String getEndpoint() {

		return this.xdiClient.getUrl().toString();
	}

	public String getIdentifier() {

		return this.identifier;
	}

	public String getCanonical() {

		return this.canonical.toString();
	}

	public String getPassword() {

		return this.password;
	}

	public void checkPassword() throws XdiException {

		// $get

		XRI3 operationAddress = new XRI3("" + this.canonical + "/$password");
		Operation operation = this.prepareOperation(MessagingConstants.XRI_GETEXISTS, operationAddress);
		MessageResult messageResult = this.send(operation);

		if (! Boolean.TRUE.equals(messageResult.getBoolean())) {

			throw new XdiException("Incorrect password.");
		}
	}

	public XdiTransactionEvent directXdi(MessageEnvelope messageEnvelope) throws XdiException {

		// do XDI transaction

		Date beginTimestamp = new Date();
		XdiTransactionEvent transactionEvent;

		try {

			MessageResult ret = XdiContext.this.xdiClient.send(messageEnvelope, null);

			if (ErrorMessageResult.isValid(ret.getGraph())) {

				ret = ErrorMessageResult.fromGraph(ret.getGraph());
				throw new XdiException("Problem from XDI Server: " + ((ErrorMessageResult) ret).getErrorString());
			}

			transactionEvent = new XdiTransactionSuccessEvent(this, messageEnvelope, beginTimestamp, new Date(), ret);
			this.xdi.fireXdiTransactionSuccessEvent((XdiTransactionSuccessEvent) transactionEvent);
		} catch (Exception ex) {

			if (! (ex instanceof XdiException)) ex = new XdiException("Problem during XDI Transaction: " + ex.getMessage(), ex);
			transactionEvent = new XdiTransactionFailureEvent(this, messageEnvelope, beginTimestamp, new Date(), ex);
			this.xdi.fireXdiTransactionFailureEvent((XdiTransactionFailureEvent) transactionEvent);
		}

		return transactionEvent;
	}

	/* 
	 * Messaging methods
	 */

	public Message prepareMessage() {

		MessageEnvelope messageEnvelope = MessageEnvelope.newInstance();
		Message message = messageEnvelope.newMessage(this.canonical);
		if (this.password != null) messageEnvelope.getGraph().createStatement(this.canonical, new XRI3Segment("$password"), this.password);

		return message;
	}

	public Operation prepareOperation(XRI3Segment operationXri) {

		MessageEnvelope messageEnvelope = MessageEnvelope.newInstance();
		Message message = messageEnvelope.newMessage(this.canonical);
		if (this.password != null) messageEnvelope.getGraph().createStatement(this.canonical, new XRI3Segment("$password"), this.password);
		Operation operation = message.createOperation(operationXri);

		return operation;
	}

	public Operation prepareOperation(XRI3Segment operationXri, XRI3 operationAddress) {

		Operation operation = this.prepareOperation(operationXri);
		Graph operationGraph = operation.createOperationGraph(null);
		CopyUtil.copyStatement(Addressing.convertAddressToStatement(operationAddress), operationGraph, null);

		return operation;
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

				XRI3[] operationAddresses = new IteratorArrayMaker<XRI3> (Addressing.getAddresses(operation.getOperationGraph(), operation.getOperationGraph())).array(new XRI3[0]);
				XRI3Segment operationXri = operation.getOperationXri();

				this.fireXdiGraphEvent(operationAddresses, operationXri);
			}
		}

		// done

		return messageResult;
	}

	/*
	 * Listener methods
	 */

	private void addXdiGraphListener(Map<XRI3, List<XdiGraphListener> > xdiGraphListeners, XRI3 address, XdiGraphListener xdiGraphListener) {

		List<XdiGraphListener > addressXdiObjectListeners = xdiGraphListeners.get(address);
		if (addressXdiObjectListeners == null) {

			addressXdiObjectListeners = new ArrayList<XdiGraphListener > ();
			xdiGraphListeners.put(address, addressXdiObjectListeners);
		}

		addressXdiObjectListeners.add(xdiGraphListener);
	}

	public void addXdiGraphListener(XdiGraphListener xdiGraphListener) {

		for (XRI3 address : xdiGraphListener.xdiGetAddresses()) this.addXdiGraphListener(this.xdiGetGraphListeners, address, xdiGraphListener);
		for (XRI3 address : xdiGraphListener.xdiAddAddresses()) this.addXdiGraphListener(this.xdiAddGraphListeners, address, xdiGraphListener);
		for (XRI3 address : xdiGraphListener.xdiModAddresses()) this.addXdiGraphListener(this.xdiModGraphListeners, address, xdiGraphListener);
		for (XRI3 address : xdiGraphListener.xdiDelAddresses()) this.addXdiGraphListener(this.xdiDelGraphListeners, address, xdiGraphListener);
	}

	public void removeXdiGraphListener(Map<XRI3, List<XdiGraphListener> > xdiGraphListeners, XRI3 address, XdiGraphListener xdiGraphListener) {

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

		for (XRI3 address : xdiGraphListener.xdiGetAddresses()) this.removeXdiGraphListener(this.xdiGetGraphListeners, address, xdiGraphListener);
		for (XRI3 address : xdiGraphListener.xdiAddAddresses()) this.removeXdiGraphListener(this.xdiAddGraphListeners, address, xdiGraphListener);
		for (XRI3 address : xdiGraphListener.xdiModAddresses()) this.removeXdiGraphListener(this.xdiModGraphListeners, address, xdiGraphListener);
		for (XRI3 address : xdiGraphListener.xdiDelAddresses()) this.removeXdiGraphListener(this.xdiDelGraphListeners, address, xdiGraphListener);
	}

	public void fireXdiGraphEvent(XRI3[] operationAddresses, XRI3Segment operationXri) {

		Set<XdiGraphListener> needFireXdiGraphListeners = new HashSet<XdiGraphListener> ();

		for (XRI3 operationAddress : operationAddresses) {

			boolean bubbled = false;

			while (operationAddress != null) {

				XRI3 listenersAddress = bubbled ? new XRI3(operationAddress.toString() + "/$$") : operationAddress;

				log.debug("Looking for listeners on address " + listenersAddress + " (" + operationXri + ")");

				List<XdiGraphListener> xdiGraphListeners;
				xdiGraphListeners = this.xdiGraphListenersForOperationXri(operationXri).get(listenersAddress);

				if (xdiGraphListeners != null) {

					log.debug("Found " + xdiGraphListeners.size() + " listeners on address " + listenersAddress + " (" + operationXri + ")");

					needFireXdiGraphListeners.addAll(xdiGraphListeners);
					break;
				}

				operationAddress = XdiUtil.extractParentXri(operationAddress);
				bubbled = true;
			}
		}

		XdiGraphEvent xdiGraphEvent = this.xdiGraphEventForOperationXri(operationXri);

		for (XdiGraphListener needFireXdiGraphListener : needFireXdiGraphListeners) {

			log.debug("Notifying " + needFireXdiGraphListener.getClass().getSimpleName());
			needFireXdiGraphListener.onXdiGraphEvent(xdiGraphEvent);
		}
	}

	private XdiGraphEvent xdiGraphEventForOperationXri(XRI3Segment operationXri) {

		if (MessagingConstants.XRI_GET.equals(operationXri)) return new XdiGraphGetEvent(this);
		if (MessagingConstants.XRI_ADD.equals(operationXri)) return new XdiGraphAddEvent(this);
		if (MessagingConstants.XRI_MOD.equals(operationXri)) return new XdiGraphModEvent(this);
		if (MessagingConstants.XRI_DEL.equals(operationXri)) return new XdiGraphDelEvent(this);

		return null;
	}

	private Map<XRI3, List<XdiGraphListener> > xdiGraphListenersForOperationXri(XRI3Segment operationXri) {

		if (MessagingConstants.XRI_GET.equals(operationXri)) return this.xdiGetGraphListeners;
		if (MessagingConstants.XRI_ADD.equals(operationXri)) return this.xdiAddGraphListeners;
		if (MessagingConstants.XRI_MOD.equals(operationXri)) return this.xdiModGraphListeners;
		if (MessagingConstants.XRI_DEL.equals(operationXri)) return this.xdiDelGraphListeners;

		return null;
	}
}