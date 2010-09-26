package pds.web.ui.app.feed.util;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class PuSHDiscovery {

	private static final Log log = LogFactory.getLog(PuSHDiscovery.class);
	
	private PuSHDiscovery() { }

	public static String getHub(String feedurl) throws Exception {

		DocumentBuilderFactory Factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = Factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(getContents(feedurl))));

		return getHub(doc);
	}

	public static String getHub(Document doc) {

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

			return hub;
		} catch (XPathExpressionException ex) {

			throw new RuntimeException(ex);
		}
	}

	public static String getTopic(Document doc) {

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

	public static String getContents(String feed) throws Exception {

		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(feed);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseBody = httpclient.execute(httpget, responseHandler);
		log.debug("Feed content: " + responseBody);

		httpclient.getConnectionManager().shutdown();

		return responseBody;
	}
}