package pds.xdi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.openxri.resolve.Resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.xdi.events.XdiListener;
import pds.xdi.events.XdiResolutionEndpointEvent;
import pds.xdi.events.XdiResolutionEvent;
import pds.xdi.events.XdiTransactionEvent;
import pds.xdi.events.XdiTransactionFailureEvent;
import pds.xdi.events.XdiTransactionSuccessEvent;
import xdi2.client.XDIClient;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.ContextNode;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.error.ErrorMessageResult;
import xdi2.messaging.util.XDIMessagingConstants;

/**
 * Support for resolving and opening XDI contexts.
 */
public class Xdi {

	private static final Logger log = LoggerFactory.getLogger(Xdi.class.getName());

	private Resolver resolver;
	private Cache inumberEndpointCache;
	private Cache xdiEndpointCache;

	private final List<XdiListener> xdiListeners;

	public Xdi(Resolver resolver) {

		this.resolver = resolver;
		
		CacheManager cacheManager = new CacheManager(Xdi.class.getResourceAsStream("ehcache.xml"));
		this.inumberEndpointCache = cacheManager.getCache("inumberEndpointCache");
		if (this.inumberEndpointCache == null) throw new NullPointerException("No inumberEndpointCache.");
		this.xdiEndpointCache = cacheManager.getCache("xdiEndpointCache");
		if (this.xdiEndpointCache == null) throw new NullPointerException("No xdiEndpointCache.");

		this.xdiListeners = new ArrayList<XdiListener> ();
	}

	/*
	 * Context methods
	 */

/* TODO	public XdiContext resolveContextByIname(String iname, String password) throws XdiException {

		log.trace("resolveContextByIname()");

		// resolve I-Name

		String inumber = null;

		try {

			inumber = Discovery.discoverInumber(new XRI3Segment(iname), this.resolver, this.inumberEndpointCache);
		} catch (Exception ex) {

			throw new XdiException("Problem while resolving the I-Name: " + ex.getMessage());
		}

		if (inumber == null) throw new XdiException("The I-Name or its I-Number could not be found.");
		this.fireXdiResolutionEvent(new XdiResolutionInameEvent(this, iname, inumber));

		// resolve I-Number

		String endpoint = null;

		try {

			endpoint = Discovery.discoverEndpoint(new XRI3Segment(inumber), this.resolver, this.xdiEndpointCache);
		} catch (Exception ex) {

			throw new XdiException("Problem while resolving the I-Number: " + ex.getMessage());
		}

		if (endpoint == null) throw new XdiException("The XDI endpoint could not be found.");
		this.fireXdiResolutionEvent(new XdiResolutionInumberEvent(this, inumber, endpoint));

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
	} */

/* TODO	public XdiContext resolveContextByInumber(String inumber, String password) throws XdiException {

		log.trace("resolveContextByInumber()");

		// resolve I-Number

		String endpoint = null;

		try {

			endpoint = Discovery.discoverEndpoint(new XRI3Segment(inumber), this.resolver, this.xdiEndpointCache);
		} catch (Exception ex) {

			throw new XdiException("Problem while resolving the I-Number: " + ex.getMessage());
		}

		if (endpoint == null) throw new XdiException("The XDI endpoint could not be found.");
		this.fireXdiResolutionEvent(new XdiResolutionInumberEvent(this, inumber, endpoint));

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
	}*/

	public XdiContext resolveContextByEndpoint(String endpoint, String password) throws XdiException {

		log.trace("resolveContextByEndpoint()");

		// resolve endpoint

		String inumber = null;
		XDIHttpClient xdiClient = new XDIHttpClient(endpoint);

		try {

			MessageEnvelope messageEnvelope = MessageEnvelope.newInstance();
			ContextNode contextNode = messageEnvelope.getGraph().addStatement("()/$is($xdi$v$1)/($)").getSubject();
			Message message = messageEnvelope.getMessageContainer(XDIMessagingConstants.XRI_S_SELF, true).createMessage();
			message.createGetOperation(contextNode);
			MessageResult messageResult = this.send(xdiClient, messageEnvelope);

			inumber = messageResult.getGraph().findRelation(new XRI3Segment("()"), new XRI3Segment("$is($xdi$v$1)")).toString();
		} catch (Exception ex) {

			throw new RuntimeException("Problem while resolving the endpoint: " + ex.getMessage());
		}

		if (inumber == null) throw new XdiException("The I-Number could not be found.");
		this.fireXdiResolutionEvent(new XdiResolutionEndpointEvent(this, endpoint, inumber));

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

	public XdiContext resolveContextManually(String endpoint, String identifier, XRI3Segment canonical, String password) throws XdiException {

		log.trace("resolveContextManually()");

		// instantiate context

		XdiContext context = new XdiContext(
				this, 
				new XDIHttpClient(endpoint), 
				identifier, 
				canonical, 
				password);

		// check password

		if (password != null) context.checkPassword();

		// done

		return context;
	}

	public Resolver getResolver() {

		return this.resolver;
	}

	public void setResolver(Resolver resolver) {

		this.resolver = resolver;
	}

	public Cache getInumberEndpointCache() {

		return this.inumberEndpointCache;
	}

	public void setInumberEndpointCache(Cache inumberEndpointCache) {

		this.inumberEndpointCache = inumberEndpointCache;
	}

	public Cache getXdiEndpointCache() {

		return this.xdiEndpointCache;
	}

	public void setXdiEndpointCache(Cache xdiEndpointCache) {

		this.xdiEndpointCache = xdiEndpointCache;
	}

	/*
	 * Sending methods
	 */

	public MessageResult send(XDIClient xdiClient, Operation operation) throws XdiException {

		return this.send(xdiClient, operation.getMessage());
	}

	public MessageResult send(XDIClient xdiClient, Message message) throws XdiException {

		return this.send(xdiClient, message.getMessageContainer().getMessageEnvelope());
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

			this.fireXdiTransactionEvent(new XdiTransactionSuccessEvent(this, messageEnvelope, beginTimestamp, new Date(), messageResult));
		} catch (Exception ex) {

			if (! (ex instanceof XdiException)) ex = new XdiException("Problem during XDI Transaction: " + ex.getMessage(), ex);
			this.fireXdiTransactionEvent(new XdiTransactionFailureEvent(this, messageEnvelope, beginTimestamp, new Date(), ex));
			throw (XdiException) ex;
		}

		// done

		return messageResult;
	}

	/*
	 * Listener methods
	 */

	public void addXdiListener(XdiListener xdiListener) {

		if (this.xdiListeners.contains(xdiListener)) return;
		this.xdiListeners.add(xdiListener);
	}

	public void removeXdiListener(XdiListener xdiListener) {

		this.xdiListeners.remove(xdiListener);
	}

	public void fireXdiTransactionEvent(XdiTransactionEvent xdiTransactionEvent) {

		for (XdiListener xdiListener : this.xdiListeners) xdiListener.onXdiTransaction(xdiTransactionEvent);
	}

	public void fireXdiResolutionEvent(XdiResolutionEvent xdiResolutionEvent) {

		for (XdiListener xdiListener : this.xdiListeners) xdiListener.onXdiResolution(xdiResolutionEvent);
	}
}