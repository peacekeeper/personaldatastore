package pds.web.xdi;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.addressing.Addressing;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.MessageEnvelope;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.messaging.client.XDIClient;
import org.eclipse.higgins.xdi4j.messaging.client.http.XDIHttpClient;
import org.eclipse.higgins.xdi4j.messaging.error.ErrorMessageResult;
import org.eclipse.higgins.xdi4j.util.CopyUtil;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.discovery.Discovery;
import pds.web.logger.Logger;
import pds.web.xdi.events.XdiTransactionFailureEvent;
import pds.web.xdi.events.XdiTransactionListener;
import pds.web.xdi.events.XdiTransactionSuccessEvent;

/**
 * PDS-based implementation of the Store interface.
 */
public class Xdi {

	private static final Log log = LogFactory.getLog(Xdi.class.getName());

	private final Discovery discovery;
	private final Logger logger;

	private final List<XdiTransactionListener> xdiListeners;

	public Xdi(Discovery discovery, Logger logger) {

		this.discovery = discovery;
		this.logger = logger;

		this.xdiListeners = new ArrayList<XdiTransactionListener> ();
	}

	/*
	 * Context methods
	 */

	public XdiContext resolveContextByIname(String iname, String password) throws XdiException {

		log.trace("resolveContextByIname()");

		// resolve I-Name

		String inumber = null;

		try {

			inumber = this.discovery.resolveXriToInumber(iname);
		} catch (Exception ex) {

			throw new XdiException("Problem while resolving the I-Name: " + ex.getMessage());
		}

		if (inumber == null) throw new XdiException("The I-Name or its I-Number could not be found.");
		this.logger.info("The I-Name " + iname + " has been resolved to the I-Number " + inumber + ".", null);

		// resolve I-Number

		String endpoint = null;

		try {

			endpoint = this.discovery.resolveXriToEndpoint(inumber);
		} catch (Exception ex) {

			throw new XdiException("Problem while resolving the I-Number: " + ex.getMessage());
		}

		if (endpoint == null) throw new XdiException("The XDI endpoint could not be found.");
		this.logger.info("The I-Number " + inumber + " has been resolved to the XDI endpoint " + endpoint + ".", null);

		// instantiate context

		XdiContext context = new XdiContext(
				this, 
				new XDIHttpClient(endpoint), 
				iname, 
				new XRI3Segment(inumber), 
				password);

		// check password

		if (password != null) context.checkPassword();

		// done

		return context;
	}

	public XdiContext resolveContextByInumber(String inumber, String password) throws XdiException {

		log.trace("resolveContextByInumber()");

		// resolve I-Number

		String endpoint = null;

		try {

			endpoint = this.discovery.resolveXriToEndpoint(inumber);
		} catch (Exception ex) {

			throw new XdiException("Problem while resolving the I-Number: " + ex.getMessage());
		}

		if (endpoint == null) throw new XdiException("The XDI endpoint could not be found.");
		this.logger.info("The I-Number " + inumber + " has been resolved to the XDI endpoint " + endpoint + ".", null);

		// instantiate context

		XdiContext context = new XdiContext(
				this, 
				new XDIHttpClient(endpoint), 
				inumber, 
				new XRI3Segment(inumber), 
				password);

		// check password

		if (password != null) context.checkPassword();

		// done

		return context;
	}

	public XdiContext resolveContextByEndpoint(String endpoint, String password) throws XdiException {

		log.trace("resolveContextByEndpoint()");

		// resolve endpoint

		String inumber = null;
		XDIHttpClient xdiClient = new XDIHttpClient(endpoint);

		try {

			XRI3 operationAddress = new XRI3("$/$is($xdi$v$1)");
			MessageEnvelope messageEnvelope = MessageEnvelope.newInstance();
			Message message = messageEnvelope.newMessage(MessagingConstants.XRI_SELF);
			Operation operation = message.createGetOperation();
			Graph operationGraph = operation.createOperationGraph(null);
			CopyUtil.copyStatement(Addressing.convertAddressToStatement(operationAddress), operationGraph, null);
			MessageResult messageResult = this.send(xdiClient, messageEnvelope);

			inumber = Addressing.findReferenceXri(messageResult.getGraph(), operationAddress).toString();
		} catch (Exception ex) {

			throw new RuntimeException("Problem while resolving the endpoint: " + ex.getMessage());
		}

		if (inumber == null) throw new XdiException("The I-Number could not be found.");
		this.logger.info("The XDI endpoint " + endpoint + " has been resolved to the I-Number " + inumber + ".", null);

		// instantiate context

		XdiContext context = new XdiContext(
				this, 
				xdiClient, 
				inumber, 
				new XRI3Segment(inumber), 
				password);

		// check password

		if (password != null) context.checkPassword();

		// done

		return context;
	}

	/*
	 * Sending methods
	 */

	public MessageResult send(XDIClient xdiClient, Operation operation) throws XdiException {

		return this.send(xdiClient, operation.getMessage());
	}

	public MessageResult send(XDIClient xdiClient, Message message) throws XdiException {

		return this.send(xdiClient, message.getMessageEnvelope());
	}

	public MessageResult send(XDIClient xdiClient, MessageEnvelope messageEnvelope) throws XdiException {

		// send the message envelope

		Date beginTimestamp = new Date();
		MessageResult messageResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);

			if (ErrorMessageResult.isValid(messageResult.getGraph())) {

				messageResult = ErrorMessageResult.fromGraph(messageResult.getGraph());
				throw new XdiException("Problem from XDI Server: " + ((ErrorMessageResult) messageResult).getErrorString());
			}

			this.fireXdiTransactionSuccessEvent(new XdiTransactionSuccessEvent(this, messageEnvelope, beginTimestamp, new Date(), messageResult));
		} catch (Exception ex) {

			if (! (ex instanceof XdiException)) ex = new XdiException("Problem during XDI Transaction: " + ex.getMessage(), ex);
			this.fireXdiTransactionFailureEvent(new XdiTransactionFailureEvent(this, messageEnvelope, beginTimestamp, new Date(), ex));
			throw (XdiException) ex;
		}

		// done

		return messageResult;
	}

	/*
	 * Listener methods
	 */

	public void addXdiListener(XdiTransactionListener xdiListener) {

		if (this.xdiListeners.contains(xdiListener)) return;
		this.xdiListeners.add(xdiListener);
	}

	public void removeXdiListener(XdiTransactionListener xdiListener) {

		this.xdiListeners.remove(xdiListener);
	}

	public void fireXdiTransactionSuccessEvent(XdiTransactionSuccessEvent transactionSuccessEvent) {

		for (XdiTransactionListener xdiListener : this.xdiListeners) {

			xdiListener.onXdiTransactionSuccess(transactionSuccessEvent);
		}
	}

	public void fireXdiTransactionFailureEvent(XdiTransactionFailureEvent transactionFailureEvent) {

		for (XdiTransactionListener xdiListener : this.xdiListeners) {

			xdiListener.onXdiTransactionFailure(transactionFailureEvent);
		}
	}
}