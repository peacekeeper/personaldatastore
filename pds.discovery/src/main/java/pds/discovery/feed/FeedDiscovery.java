package pds.discovery.feed;

import java.io.StringReader;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import pds.discovery.util.DiscoveryUtil;

/**
 * This class can:
 * 
 * - Discover an ATOM feed's PuSH hub from a feed URL.
 * - Discover an ATOM feed's PuSH hub from a feed document.
 * - Discover an ATOM feed URL from a feed document.
 */
public class FeedDiscovery {

	private static final Log log = LogFactory.getLog(FeedDiscovery.class);
	
	private FeedDiscovery() { }

	public static URI discoverHub(URI uri) throws Exception {

		if (uri == null) throw new NullPointerException("No URI provided.");

		DocumentBuilderFactory Factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = Factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(DiscoveryUtil.getContents(uri))));

		return discoverHub(doc);
	}

	public static URI discoverHub(Document doc) {

		String hub = null;

		XPathFactory factory = XPathFactory.newInstance();
		XPath xPath = factory.newXPath();
		XPathExpression xPathExpression;

		try {

			xPathExpression = xPath.compile("/feed/link[@rel='hub']/@href");
			hub = (String) xPathExpression.evaluate(doc);

			if (hub == null || hub.equals("")) {

				xPathExpression = xPath.compile("//link[@rel='hub']/@href");
				hub = (String) xPathExpression.evaluate(doc);			
			}	

			if (hub == null || hub.equals("")) return null;
			log.debug("Discovered hub: " + hub);

			return URI.create(hub);
		} catch (XPathExpressionException ex) {

			throw new RuntimeException(ex);
		}
	}

	public static String discoverTopic(Document doc) {

		String topic = null;

		XPathFactory factory = XPathFactory.newInstance();
		XPath xPath = factory.newXPath();
		XPathExpression xPathExpression;

		try {

			xPathExpression = xPath.compile("/feed/link[@rel='self']/@href");
			topic = (String) xPathExpression.evaluate(doc);

			if (topic == null || topic.equals("")) {

				xPathExpression = xPath.compile("//link[@rel='self']/@href");
				topic = (String) xPathExpression.evaluate(doc);			
			}

			if (topic == null || topic.equals("")) return null;
			log.debug("Discovered topic: " + topic);

			return topic;
		} catch (XPathExpressionException ex) {

			throw new RuntimeException(ex);
		}
	}
}
