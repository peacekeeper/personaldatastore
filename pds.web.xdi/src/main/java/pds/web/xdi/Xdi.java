package pds.web.xdi;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.higgins.xdi4j.messaging.client.http.XDIHttpClient;
import org.eclipse.higgins.xdi4j.seps.XDIService;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.openxri.XRI;
import org.openxri.resolve.Resolver;
import org.openxri.resolve.ResolverFlags;
import org.openxri.resolve.ResolverState;
import org.openxri.xml.Service;
import org.openxri.xml.XRD;

import pds.web.logger.Logger;
import pds.web.xdi.events.XdiListener;
import pds.web.xdi.events.XdiTransactionFailureEvent;
import pds.web.xdi.events.XdiTransactionSuccessEvent;
import pds.web.xdi.objects.XdiContext;

/**
 * PDS-based implementation of the Store interface.
 */
public class Xdi {

	private static final Log log = LogFactory.getLog(Xdi.class.getName());

	private final Resolver resolver;
	private final Logger logger;

	private final List<XdiListener> xdiListeners;

	public Xdi(Resolver resolver, Logger logger) {

		this.resolver = resolver;
		this.logger = logger;
		
		this.xdiListeners = new ArrayList<XdiListener> ();
	}

	/*
	 * Context methods
	 */

	public XdiContext resolveContext(String iname, String password) throws XdiException {

		log.trace("resolveContext()");

		String uri = null;
		String inumber = null;

		try {

			ResolverFlags resolverFlags = new ResolverFlags();

			XRD xrd = this.resolver.resolveAuthToXRD(new XRI(iname), resolverFlags, new ResolverState());
			inumber = xrd.getCanonicalID().getValue();
		} catch (Exception ex) {

			throw new RuntimeException("The I-Name or its I-Number could not be found: " + ex.getMessage());
		}

		this.logger.info("The I-Name " + iname + " has been resolved to the I-Number " + inumber, null);

		try {

			ResolverFlags resolverFlags = new ResolverFlags();
			resolverFlags.setNoDefaultT(true);

			XRD xrd = this.resolver.resolveSEPToXRD(new XRI(inumber), XDIService.SERVICE_TYPE, null, resolverFlags, new ResolverState());
			if (! xrd.getStatus().getCode().equals("100")) throw new RuntimeException("Resultion failed: " + xrd.getStatus().getCode());

			List<?> services = xrd.getSelectedServices().getList();
			
			for (Object service : services) {

				if (((Service) service).getNumURIs() > 0) uri = ((Service) service).getURIAt(0).getUriString();
			}

			if (uri == null) throw new RuntimeException("No XDI endpoint URI.");
			if (! uri.endsWith("/")) uri += "/";
		} catch (Exception ex) {

			throw new RuntimeException("The I-Name's XDI endpoint could not be found: " + ex.getMessage());
		}

		this.logger.info("The I-Number " + inumber + " has been resolved to the URI " + uri, null);

		XdiContext context = new XdiContext(
				this, 
				new XDIHttpClient(uri), 
				iname, 
				new XRI3Segment(inumber), 
				password);

		// done

		log.trace("Done.");
		return context;
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

	public void fireXdiTransactionSuccessEvent(XdiTransactionSuccessEvent transactionSuccessEvent) {

		for (XdiListener storeListener : this.xdiListeners) {

			storeListener.onXdiTransactionSuccess(transactionSuccessEvent);
		}
	}

	public void fireXdiTransactionFailureEvent(XdiTransactionFailureEvent transactionFailureEvent) {

		for (XdiListener storeListener : this.xdiListeners) {

			storeListener.onXdiTransactionFailure(transactionFailureEvent);
		}
	}
}