package pds.discovery.xrd;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.opensaml.xml.parse.ParserPool;
import org.openxrd.discovery.DiscoveryException;
import org.openxrd.discovery.HttpDiscoveryMethod;
import org.openxrd.discovery.impl.AbstractHttpDiscoveryMethod;
import org.openxrd.xrd.core.Alias;
import org.openxrd.xrd.core.Link;
import org.openxrd.xrd.core.Property;
import org.openxrd.xrd.core.XRD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LRDDDiscoveryMethod implements HttpDiscoveryMethod {

	private static final Logger log = LoggerFactory.getLogger(LRDDDiscoveryMethod.class);

	private HttpDiscoveryMethod parentDiscovery;

	public LRDDDiscoveryMethod(HttpDiscoveryMethod parentDiscovery) {

		this.parentDiscovery = parentDiscovery;
	}

	@Override
	public HttpClient getHttpClient() {

		return this.parentDiscovery.getHttpClient();
	}

	@Override
	public void setHttpClient(HttpClient newHttpClient) {

		this.parentDiscovery.setHttpClient(newHttpClient);
	}

	public ParserPool getParserPool() {

		return this.parentDiscovery.getParserPool();
	}


	public void setParserPool(ParserPool newParserPool) {

		this.parentDiscovery.setParserPool(newParserPool);
	}

	public HttpDiscoveryMethod getParent() {

		return this.parentDiscovery;
	}

	public void setParentDiscovery(HttpDiscoveryMethod parentDiscovery) {

		this.parentDiscovery = parentDiscovery;
	}

	@Override
	public XRD discoverXRD(URI uri) throws DiscoveryException {

		URI hostUri;

		if (uri.toString().startsWith("acct:") && uri.toString().contains("@")) {

			hostUri = URI.create("http://" + uri.toString().substring(uri.toString().indexOf("@") + 1));
		} else {

			hostUri = uri;
		}

		XRD xrd = this.parentDiscovery.discoverXRD(hostUri);
		List<XRD> lrddXrds = new ArrayList<XRD> ();

		for (Iterator<Link> links = xrd.getLinks().iterator(); links.hasNext(); ) {

			Link link = links.next();

			if ("lrdd".equals(link.getRel())) {

				String lrddUrl;

				if (link.getHref() != null)
					lrddUrl = link.getHref();
				else if (link.getTemplate() != null)
					lrddUrl = link.getTemplate().replace("{uri}", uri.toString());
				else
					continue;

				log.debug("LRDD <Link> found: " + lrddUrl);

				AbstractHttpDiscoveryMethod lrddDiscoveryMethod = new AbstractHttpDiscoveryMethod() {

					@Override
					public URI getXRDLocation(URI uri) throws DiscoveryException {

						return uri;
					}
				};
				lrddDiscoveryMethod.setHttpClient(this.getHttpClient());
				lrddDiscoveryMethod.setParserPool(this.getParserPool());

				lrddXrds.add(lrddDiscoveryMethod.discoverXRD(URI.create(lrddUrl)));
			}
		}

		// add the <Alias>es, <Property>s and <Link>s from the LRDD XRD to the original XRD

		for (XRD lrddXrd : lrddXrds) {

			log.debug("Adding " + lrddXrd.getAliases().size() + " <Alias>es from LRDD <XRD>");

			for (Alias lrddAlias : lrddXrd.getAliases()) {

				lrddAlias.detach();
				xrd.getAliases().add(lrddAlias);
			}

			log.debug("Adding " + lrddXrd.getProperties().size() + " <Property>s from LRDD <XRD>");

			for (Property lrddProperty : lrddXrd.getProperties()) {

				lrddProperty.detach();
				xrd.getProperties().add(lrddProperty);
			}

			log.debug("Adding " + lrddXrd.getLinks().size() + " <Link>s from LRDD <XRD>");

			for (Link lrddLink : lrddXrd.getLinks()) {

				lrddLink.detach();
				xrd.getLinks().add(lrddLink);
			}
		}

		// done

		return xrd;
	}
}
