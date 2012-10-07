package pds.xdi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.xdi.events.XdiListener;
import pds.xdi.events.XdiResolutionEndpointEvent;
import pds.xdi.events.XdiResolutionEvent;
import pds.xdi.events.XdiResolutionInameEvent;
import pds.xdi.events.XdiResolutionInumberEvent;
import pds.xdi.events.XdiTransactionEvent;
import pds.xdi.events.XdiTransactionFailureEvent;
import pds.xdi.events.XdiTransactionSuccessEvent;
import xdi2.client.XDIClient;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.resolution.XDIResolutionResult;
import xdi2.resolution.XDIResolver;

/**
 * Support for resolving and opening XDI endpoints.
 */
public class XdiClient {

	private static final Logger log = LoggerFactory.getLogger(XdiClient.class.getName());

	private XDIResolver xdiResolver;
	private Cache inumberEndpointCache;
	private Cache xdiEndpointCache;

	private final List<XdiListener> xdiListeners;

	public XdiClient(XDIResolver xdiResolver) {

		this.xdiResolver = xdiResolver;

		CacheManager cacheManager = CacheManager.create(XdiClient.class.getResourceAsStream("ehcache.xml"));
		this.inumberEndpointCache = cacheManager.getCache("inumberEndpointCache");
		if (this.inumberEndpointCache == null) throw new NullPointerException("No inumberEndpointCache.");
		this.xdiEndpointCache = cacheManager.getCache("xdiEndpointCache");
		if (this.xdiEndpointCache == null) throw new NullPointerException("No xdiEndpointCache.");

		this.xdiListeners = new ArrayList<XdiListener> ();
	}

	/*
	 * Endpoint methods
	 */

	public XdiEndpoint resolveEndpointByIname(String iname, String secretToken) throws XdiException {

		log.trace("resolveEndpointByIname()");

		// resolve I-Name

		String inumber = null;

		try {

			XDIResolutionResult xdiResolutionResult = this.xdiResolver.resolve(iname);	// TODO: add cache

			inumber = xdiResolutionResult.getInumber();
		} catch (Exception ex) {

			throw new XdiException("Problem while resolving the I-Name: " + ex.getMessage());
		}

		if (inumber == null) throw new XdiException("The I-Name or its I-Number could not be found.");
		this.fireXdiResolutionEvent(new XdiResolutionInameEvent(this, iname, inumber));

		// resolve I-Number

		String endpointUrl = null;

		try {

			XDIResolutionResult xdiResolutionResult = this.xdiResolver.resolve(iname);	// TODO: add cache

			endpointUrl = xdiResolutionResult.getUri();
		} catch (Exception ex) {

			throw new XdiException("Problem while resolving the I-Number: " + ex.getMessage());
		}

		if (endpointUrl == null) throw new XdiException("The XDI endpoint could not be found.");
		this.fireXdiResolutionEvent(new XdiResolutionInumberEvent(this, inumber, endpointUrl));

		// instantiate endpoint

		XdiEndpoint endpoint = new XdiEndpoint(
				this, 
				new XDIHttpClient(endpointUrl), 
				iname, 
				new XRI3Segment(inumber), 
				secretToken);

		// check secret token

		if (secretToken != null) endpoint.checkSecretToken();

		// done

		return endpoint;
	}

	public XdiEndpoint resolveEndpointByEndpointUrl(String endpointUrl, String secretToken) throws XdiException {

		log.trace("resolveEndpointByEndpointUrl()");

		// resolve endpoint url

		String inumber = null;
		XDIHttpClient xdiClient = new XDIHttpClient(endpointUrl);

		try {

			MessageEnvelope messageEnvelope = new MessageEnvelope();
			Message message = messageEnvelope.getMessage(XDIMessagingConstants.XRI_S_ANONYMOUS, true);
			message.createGetOperation(new XRI3Segment("(()/$is$is/($))"));
			MessageResult messageResult = this.send(xdiClient, messageEnvelope);

			inumber = messageResult.getGraph().findRelation(new XRI3Segment("()"), new XRI3Segment("$is$is")).toString();
		} catch (Exception ex) {

			throw new RuntimeException("Problem while resolving the endpoint: " + ex.getMessage());
		}

		if (inumber == null) throw new XdiException("The I-Number could not be found.");
		this.fireXdiResolutionEvent(new XdiResolutionEndpointEvent(this, endpointUrl, inumber));

		// instantiate endpoint

		XdiEndpoint endpoint = new XdiEndpoint(
				this, 
				xdiClient, 
				inumber, 
				new XRI3Segment(inumber), 
				secretToken);

		// check secret token

		if (secretToken != null) endpoint.checkSecretToken();

		// done

		return endpoint;
	}

	public XdiEndpoint resolveEndpointManually(String endpointUrl, String identifier, XRI3Segment canonical, String secretToken) throws XdiException {

		log.trace("resolveEndpointManually()");

		// instantiate endpoint

		XdiEndpoint endpoint = new XdiEndpoint(
				this, 
				new XDIHttpClient(endpointUrl), 
				identifier, 
				canonical, 
				secretToken);

		// check secret token

		if (secretToken != null) endpoint.checkSecretToken();

		// done

		return endpoint;
	}

	public XDIResolver getXdiResolver() {

		return this.xdiResolver;
	}

	public void setXdiResolver(XDIResolver xdiResolver) {

		this.xdiResolver = xdiResolver;
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

	public MessageResult send(XDIClient xdiClient, Operation operation) throws Xdi2ClientException {

		return this.send(xdiClient, operation.getMessage());
	}

	public MessageResult send(XDIClient xdiClient, Message message) throws Xdi2ClientException {

		return this.send(xdiClient, message.getMessageEnvelope());
	}

	public MessageResult send(XDIClient xdiClient, MessageEnvelope messageEnvelope) throws Xdi2ClientException {

		// send the message envelope

		MessageResult messageResult = null;
		Date beginTimestamp = new Date();

		try {

			messageResult = xdiClient.send(messageEnvelope, null);

			this.fireXdiTransactionEvent(new XdiTransactionSuccessEvent(this, messageEnvelope, messageResult, beginTimestamp, new Date()));
		} catch (Xdi2ClientException ex) {

			messageResult = ((Xdi2ClientException) ex).getErrorMessageResult();

			this.fireXdiTransactionEvent(new XdiTransactionFailureEvent(this, messageEnvelope, messageResult, beginTimestamp, new Date(), ex));
			throw ex;
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