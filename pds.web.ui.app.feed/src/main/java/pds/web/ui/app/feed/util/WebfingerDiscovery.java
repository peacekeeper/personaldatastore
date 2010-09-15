package pds.web.ui.app.feed.util;

import java.net.URI;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.parse.BasicParserPool;
import org.openxrd.DefaultBootstrap;
import org.openxrd.discovery.DiscoveryException;
import org.openxrd.discovery.DiscoveryManager;
import org.openxrd.discovery.impl.AbstractHttpDiscoveryMethod;
import org.openxrd.discovery.impl.BasicDiscoveryManager;
import org.openxrd.discovery.impl.HostMetaDiscoveryMethod;
import org.openxrd.discovery.impl.HtmlLinkDiscoveryMethod;
import org.openxrd.discovery.impl.HttpHeaderDiscoveryMethod;
import org.openxrd.xrd.core.Link;
import org.openxrd.xrd.core.XRD;

public class WebfingerDiscovery {

	private static final Log log = LogFactory.getLog(WebfingerDiscovery.class);

	private static DiscoveryManager lrddDiscoveryManager;
	private static HostMetaDiscoveryMethod webfingerDiscoveryMethod;
	private static final HttpClient httpClient = new DefaultHttpClient();

	static {

		try {

			DefaultBootstrap.bootstrap();
		} catch (ConfigurationException ex) {

			throw new RuntimeException(ex);
		}

		// LRDD discovery

		lrddDiscoveryManager = new BasicDiscoveryManager();

		HostMetaDiscoveryMethod hostMeta = new HostMetaDiscoveryMethod();
		hostMeta.setHttpClient(httpClient);
		hostMeta.setParserPool(new BasicParserPool());
		lrddDiscoveryManager.getDiscoveryMethods().add(hostMeta);

		HttpHeaderDiscoveryMethod header = new HttpHeaderDiscoveryMethod();
		header.setHttpClient(httpClient);
		header.setParserPool(new BasicParserPool());
		lrddDiscoveryManager.getDiscoveryMethods().add(header);

		HtmlLinkDiscoveryMethod link = new HtmlLinkDiscoveryMethod();
		link.setHttpClient(httpClient);
		link.setParserPool(new BasicParserPool());
		lrddDiscoveryManager.getDiscoveryMethods().add(link);

		// Webfinger discovery

		webfingerDiscoveryMethod = new HostMetaDiscoveryMethod();
		webfingerDiscoveryMethod.setHttpClient(httpClient);
		webfingerDiscoveryMethod.setParserPool(new BasicParserPool());

		hostMeta.setHttpClient(httpClient);
		hostMeta.setParserPool(new BasicParserPool());
	}

	public static XRD discoverXRD(URI uri) throws Exception {

		XRD xrd;

		log.debug("Trying to discover <XRD> from " + uri);

		if (uri.toString().startsWith("http://") || uri.toString().startsWith("https://")) {

			xrd = lrddDiscoveryManager.discover(uri);
		} else if (uri.toString().startsWith("acct:") && uri.toString().contains("@")) {

			URI hostUri = URI.create("http://" + uri.toString().substring(uri.toString().indexOf("@") + 1));
			log.debug("Trying to discover <XRD> from " + hostUri);
			xrd = webfingerDiscoveryMethod.discoverXRD(hostUri);
		} else {

			throw new IllegalArgumentException("Can only discover via LRDD and Webfinger.");
		}

		if (xrd == null) {

			throw new RuntimeException("No XRD document.");
		}

		log.debug("<XRD> from " + uri + " with " + xrd.getLinks().size() + " links");

		// LRDD?

		Link lrddLink = discoverLink(xrd, "lrdd", null);
		String template = lrddLink == null ? null : lrddLink.getTemplate();
		log.debug("LRDD template (raw): " + template);

		if (template != null) {

			template = template.replace("{uri}", URLEncoder.encode(uri.toString(), "UTF-8"));
			log.debug("LRDD template (replaced): " + template);

			LRDDDiscoveryMethod lrdd = new LRDDDiscoveryMethod();
			lrdd.setHttpClient(httpClient);
			lrdd.setParserPool(new BasicParserPool());

			xrd = lrdd.discoverXRD(URI.create(template));

			log.debug("LRDD <XRD> from " + template + " with " + xrd.getLinks().size() + " links");
		}

		// done

		return xrd;
	}

	public static Link discoverLink(XRD xrd, String rel, String type) {

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

	private static class LRDDDiscoveryMethod extends AbstractHttpDiscoveryMethod {

		private LRDDDiscoveryMethod() {

		}

		@Override
		public URI getXRDLocation(URI uri) throws DiscoveryException {

			return uri;
		}
	}
}
