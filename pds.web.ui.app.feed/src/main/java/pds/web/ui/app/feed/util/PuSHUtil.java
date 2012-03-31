package pds.web.ui.app.feed.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

public class PuSHUtil {

	private static final Logger log = LoggerFactory.getLogger(PuSHUtil.class);

	private static final DefaultHttpClient httpClient;

	static {

		HttpParams params = new BasicHttpParams();

		ConnManagerParams.setMaxTotalConnections(params, 200);
		ConnPerRouteBean connPerRoute = new ConnPerRouteBean(20);
		connPerRoute.setDefaultMaxPerRoute(50);
		ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);

		httpClient = new DefaultHttpClient(cm, params);

		httpClient.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {

			public long getKeepAliveDuration(HttpResponse response, HttpContext context) {

				HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));

				while (it.hasNext()) {

					HeaderElement he = it.nextElement();
					String param = he.getName();
					String value = he.getValue();

					if (value != null && param.equalsIgnoreCase("timeout")) {

						try {

							return Long.parseLong(value) * 1000;
						} catch (NumberFormatException ignore) { }
					}
				}

				return 30 * 1000;
			}
		});
	}

	private PuSHUtil() { }

	/**
	 * Sends a subscription request to a hub.
	 * @throws URISyntaxException 
	 */
	public static void subscribe(URI hub, URI hubcallback, URI hubtopic, String hubleaseseconds, String hubsecret, String hubverifytoken) throws IOException {

		// check parameters

		if (hubcallback == null) throw new NullPointerException();

		// POST

		HttpPost httppost = new HttpPost(hub);	
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("hub.mode", "subscribe"));
		nvps.add(new BasicNameValuePair("hub.callback", hubcallback.toString()));
		nvps.add(new BasicNameValuePair("hub.topic", hubtopic.toString()));
		nvps.add(new BasicNameValuePair("hub.verify", "async"));
		nvps.add(new BasicNameValuePair("hub.verify", "sync"));
		if (hubleaseseconds != null) nvps.add(new BasicNameValuePair("hub.lease_seconds", hubleaseseconds));
		if (hubsecret != null) nvps.add(new BasicNameValuePair("hub.hub.secret", hubsecret));
		nvps.add(new BasicNameValuePair("hub.verify_token", hubverifytoken));

		post(httppost, nvps);
	}

	/**
	 * Sends an unsubscription request to a hub.
	 */
	public static void unsubscribe(URI hub, URI hubcallback, URI hubtopic, String hubsecret, String hubverifytoken) throws IOException {

		// check parameters

		if (hubcallback == null) throw new NullPointerException();

		// POST

		HttpPost httppost = new HttpPost(hub);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("hub.mode", "unsubscribe"));
		nvps.add(new BasicNameValuePair("hub.callback", hubcallback.toString()));
		nvps.add(new BasicNameValuePair("hub.topic", hubtopic.toString()));
		nvps.add(new BasicNameValuePair("hub.verify", "async"));
		nvps.add(new BasicNameValuePair("hub.verify", "sync"));
		if (hubsecret != null) nvps.add(new BasicNameValuePair("hub.hub_secret", hubsecret));
		nvps.add(new BasicNameValuePair("hub.verify_token", hubverifytoken));

		post(httppost, nvps);
	}

	/**
	 * Sends a publish request to a hub.
	 */
	public static void publish(String hub, String hubtopic) throws Exception {

		// check parameters

		new URL(hub);
		new URL(hubtopic);

		// POST

		HttpPost httppost = new HttpPost(hub);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("hub.mode", "publish"));
		nvps.add(new BasicNameValuePair("hub.url", hubtopic));

		post(httppost, nvps);
	}

	/*
	 * Helper methods
	 */

	private static void post(HttpPost httppost, List<NameValuePair> nvps) throws IOException {

		httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		httppost.setHeader("Content-type", "application/x-www-form-urlencoded");

		String line = new BufferedReader(new InputStreamReader(httppost.getEntity().getContent())).readLine();

		HttpContext context = new BasicHttpContext();

		HttpResponse httpresponse = httpClient.execute(httppost, context);

		if (httpresponse == null) throw new IOException("No HTTP Response from Hub.");
		log.debug("POSTed " + line + " to " + httppost.getURI() + " --> " + httpresponse.getStatusLine().getStatusCode());
		if (httpresponse.getStatusLine().getStatusCode() >= 300) throw new IOException("Got HTTP Error from Hub: " + httpresponse.getStatusLine().getStatusCode() + " (" + httpresponse.getStatusLine().getReasonPhrase() + ")");

		HttpEntity entity = httpresponse.getEntity();
		if (entity != null) entity.consumeContent();
	}

	private static Random random = new Random();

	public static String makeVerifyToken() {

		return new BigInteger(130, random).toString(32);
	}
}
