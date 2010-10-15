package pds.discovery.util;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class DiscoveryUtil {

	private static final Log log = LogFactory.getLog(DiscoveryUtil.class);

	private static final HttpClient httpClient = new DefaultHttpClient();

	private DiscoveryUtil() { }

	public static String getContents(URI uri) throws Exception {

		if (uri == null) throw new NullPointerException("No URI provided.");

		HttpGet httpget = new HttpGet(uri);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();

		log.debug("Retrieving from: " + uri);
		String responseBody = httpClient.execute(httpget, responseHandler);
		log.debug("Response size: " + responseBody.length() + " characters.");

		return responseBody;
	}
}
