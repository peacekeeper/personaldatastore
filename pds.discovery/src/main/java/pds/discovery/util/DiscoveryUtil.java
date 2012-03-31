package pds.discovery.util;

import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscoveryUtil {

	private static final Logger log = LoggerFactory.getLogger(DiscoveryUtil.class);

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
