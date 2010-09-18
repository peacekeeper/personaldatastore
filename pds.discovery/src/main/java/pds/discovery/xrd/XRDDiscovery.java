package pds.discovery.xrd;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.parse.BasicParserPool;
import org.openxrd.DefaultBootstrap;
import org.openxrd.discovery.DiscoveryManager;
import org.openxrd.discovery.impl.BasicDiscoveryManager;
import org.openxrd.discovery.impl.HostMetaDiscoveryMethod;
import org.openxrd.discovery.impl.HtmlLinkDiscoveryMethod;
import org.openxrd.discovery.impl.HttpHeaderDiscoveryMethod;
import org.openxrd.xrd.core.Link;
import org.openxrd.xrd.core.XRD;

public class XRDDiscovery {

	private static final Log log = LogFactory.getLog(XRDDiscovery.class);

	private static DiscoveryManager lrddDiscoveryManager;
	private static LRDDDiscoveryMethod webfingerDiscoveryMethod;
	private static final HttpClient httpClient = new DefaultHttpClient();

	static {

		try {

			DefaultBootstrap.bootstrap();
		} catch (ConfigurationException ex) {

			throw new RuntimeException(ex);
		}

		// LRDD discovery

		lrddDiscoveryManager = new BasicDiscoveryManager();

		LRDDDiscoveryMethod hostMeta = new LRDDDiscoveryMethod(new HostMetaDiscoveryMethod());
		hostMeta.setHttpClient(httpClient);
		hostMeta.setParserPool(new BasicParserPool());
		lrddDiscoveryManager.getDiscoveryMethods().add(hostMeta);

		LRDDDiscoveryMethod header = new LRDDDiscoveryMethod(new HttpHeaderDiscoveryMethod());
		header.setHttpClient(httpClient);
		header.setParserPool(new BasicParserPool());
		lrddDiscoveryManager.getDiscoveryMethods().add(header);

		LRDDDiscoveryMethod link = new LRDDDiscoveryMethod(new HtmlLinkDiscoveryMethod());
		link.setHttpClient(httpClient);
		link.setParserPool(new BasicParserPool());
		lrddDiscoveryManager.getDiscoveryMethods().add(link);

		// Webfinger discovery

		webfingerDiscoveryMethod = new LRDDDiscoveryMethod(new HostMetaDiscoveryMethod());
		webfingerDiscoveryMethod.setHttpClient(httpClient);
		webfingerDiscoveryMethod.setParserPool(new BasicParserPool());
	}

	public static XRD discoverXRD(URI uri) throws Exception {

		XRD xrd;

		log.debug("Trying to discover <XRD> from " + uri);

		if (uri.toString().startsWith("http://") || uri.toString().startsWith("https://")) {

			xrd = lrddDiscoveryManager.discover(uri);
		} else if (uri.toString().startsWith("acct:") && uri.toString().contains("@")) {

			xrd = webfingerDiscoveryMethod.discoverXRD(uri);
		} else {

			throw new IllegalArgumentException("Can only discover via LRDD and Webfinger.");
		}

		if (xrd == null) {

			throw new RuntimeException("No XRD document.");
		}

		log.debug("<XRD> from " + uri + " with " + xrd.getLinks().size() + " links");

		// done

		return xrd;
	}

	public static Link selectLink(XRD xrd, String rel, String type) {

		if (xrd == null || rel == null) return null;

		log.debug("Looking for <Link> with rel=" + rel + " and type=" + type);

		for (Link link : xrd.getLinks()) {

			log.debug("Trying to match <Link> with rel=" + link.getRel() + " and type="  + link.getType());

			if (link.getRel().equals(rel) &&
					(type == null || link.getType().equals(type))) {

				return link;
			}
		}

		log.debug("No matching <Link>");

		return null;
	}
}
