package pds.web.ui.app.feed.subscribe;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

public class Subscriber {

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

	/**
	 * Sends a subscription request to a hub.
	 */
	public static String subscribe(String hub, String hubcallback, String hubtopic, String hubleaseseconds, String hubsecret) throws IOException {

		String hubverifytoken = makeVerifyToken();

		HttpPost httppost = new HttpPost(hub);	
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("hub.mode", "subscribe"));
		nvps.add(new BasicNameValuePair("hub.callback", hubcallback));
		nvps.add(new BasicNameValuePair("hub.topic", hubtopic));
		nvps.add(new BasicNameValuePair("hub.verify", "async"));
		nvps.add(new BasicNameValuePair("hub.verify", "sync"));
		if (hubleaseseconds != null) nvps.add(new BasicNameValuePair("hub.lease_seconds", hubleaseseconds));
		if (hubsecret != null) nvps.add(new BasicNameValuePair("hub.hub.secret", hubsecret));
		nvps.add(new BasicNameValuePair("hub.verify_token", hubverifytoken));

		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		httppost.setHeader("Content-type", "application/x-www-form-urlencoded");

		post(httppost);
		
		// done
		
		return hubverifytoken;
	}

	/**
	 * Sends an unsubscription request to a hub.
	 */
	public static String unsubscribe(String hub, String hubcallback, String hubtopic, String hubsecret) throws Exception {

		String hubverifytoken = makeVerifyToken();

		HttpPost httppost = new HttpPost(hub);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("hub.mode", "unsubscribe"));
		nvps.add(new BasicNameValuePair("hub.callback", hubcallback));
		nvps.add(new BasicNameValuePair("hub.topic", hubtopic));
		nvps.add(new BasicNameValuePair("hub.verify", "async"));
		nvps.add(new BasicNameValuePair("hub.verify", "sync"));
		if (hubsecret != null) nvps.add(new BasicNameValuePair("hub.hub_secret", hubsecret));
		nvps.add(new BasicNameValuePair("hub.verify_token", hubverifytoken));

		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		httppost.setHeader("Content-type", "application/x-www-form-urlencoded");

		post(httppost);

		// done
		
		return hubverifytoken;
	}

	/*
	 * Helper methods
	 */
	
	private static void post(HttpPost httppost) throws IOException {

		HttpContext context = new BasicHttpContext();

		HttpResponse httpresponse = httpClient.execute(httppost, context);

		if (httpresponse == null) throw new IOException("No HTTP Response from Hub.");
		if (httpresponse.getStatusLine().getStatusCode() != 200) throw new IOException("Got HTTP Error from Hub: " + httpresponse.getStatusLine().getStatusCode());

		HttpEntity entity = httpresponse.getEntity();
		if (entity != null) entity.consumeContent();
	}

	private static Random random = new Random();

	private static String makeVerifyToken() {

		return new BigInteger(130, random).toString(32);
	}
}
