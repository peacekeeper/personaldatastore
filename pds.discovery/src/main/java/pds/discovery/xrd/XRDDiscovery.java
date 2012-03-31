package pds.discovery.xrd;

import java.net.URI;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class can:
 * 
 * - Discover an <XRD> from an http(s):// or acct: URI.
 * - Select a <Link> with a given rel and type from an <XRD>.
 */
public class XRDDiscovery {

	private static final Logger log = LoggerFactory.getLogger(XRDDiscovery.class);

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

		if (uri == null) throw new NullPointerException("No URI provided.");

		log.debug("Trying to discover <XRD> from " + uri);

		XRD xrd;

		if (uri.toString().startsWith("http://") || uri.toString().startsWith("https://")) {

			xrd = lrddDiscoveryManager.discover(uri);
		} else if (uri.toString().startsWith("acct:") && uri.toString().contains("@")) {

			xrd = webfingerDiscoveryMethod.discoverXRD(uri);
		} else {

			throw new IllegalArgumentException("Can only discover via LRDD and Webfinger.");
		}

		if (xrd == null) return null;

		// done

		log.debug("<XRD> from " + uri + " with " + xrd.getLinks().size() + " links");

		return xrd;
	}

	public static URI selectLinkHref(XRD xrd, String rel, String type) {

		if (xrd == null || rel == null) throw new NullPointerException("No <XRD> or rel provided.");

		log.debug("Looking for <Link> with rel=" + rel + " and type=" + type);

		for (Link link : xrd.getLinks()) {

			String linkRel = link.getRel();
			String linkType = link.getType();
			
			log.debug("Trying to match <Link> with rel=" + linkRel + " and type="  + linkType);

			if (rel.equals(linkRel) &&
					(type == null || type.equals(linkType))) {

				URI href = URI.create(link.getHref());
				
				log.debug("Match! URI: " + href.toString());

				return href;
			}
		}

		log.debug("No matching <Link>");

		return null;
	}
}
