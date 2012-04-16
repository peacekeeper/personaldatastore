package pds.p2p.api.node.client;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

/**
 * A JSON-RPC client that uses the HTTP protocol.
 */
public class MyJsonRpcHttpClient extends JsonRpcHttpClient {

	private static final int DEFAULT_CONNECTION_TIMEOUT = 60*1000;
	private static final int DEFAULT_READ_TIMEOUT = 60*1000;

	private static Logger log = LoggerFactory.getLogger(MyJsonRpcHttpClient.class);

	public MyJsonRpcHttpClient(ObjectMapper mapper, URL serviceUrl, Map<String, String> headers) {

		super(mapper, serviceUrl, headers);

		this.setConnectionTimeoutMillis(DEFAULT_CONNECTION_TIMEOUT);
		this.setReadTimeoutMillis(DEFAULT_READ_TIMEOUT);
	}

	public MyJsonRpcHttpClient(URL serviceUrl, Map<String, String> headers) {

		super(serviceUrl, headers);

		this.setConnectionTimeoutMillis(DEFAULT_CONNECTION_TIMEOUT);
		this.setReadTimeoutMillis(DEFAULT_READ_TIMEOUT);
	}

	public MyJsonRpcHttpClient(URL serviceUrl) {

		super(serviceUrl);

		this.setConnectionTimeoutMillis(DEFAULT_CONNECTION_TIMEOUT);
		this.setReadTimeoutMillis(DEFAULT_READ_TIMEOUT);
	}

	/**
	 * Invokes the given method with the given arguments and returns
	 * an object of the given type, or null if void.
	 * @param methodName the name of the method to invoke
	 * @param arguments the arguments to the method
	 * @param returnType the return type
	 * @param extraHeaders extra headers to add to the request
	 * @return the return value
	 * @throws Throwable on error
	 */
	public Object invoke(String methodName, Object[] arguments, Type returnType, Map<String, String> extraHeaders) throws Throwable {

		try {

			return this.internalInvoke(methodName, arguments, returnType, extraHeaders);
		} catch (Throwable ex) {

			log.warn("Problem while invoking '" + methodName + "' on " + this.getServiceUrl() + ": " + ex.getMessage(), ex);
			throw ex;
		}
	}

	private Object internalInvoke(String methodName, Object[] arguments, Type returnType, Map<String, String> extraHeaders) throws Throwable {

		// HTTP parameters

		URL serviceUrl = this.getServiceUrl();
		Proxy connectionProxy = this.getConnectionProxy();
		int connectionTimeoutMillis = this.getConnectionTimeoutMillis();
		int readTimeoutMillis = this.getReadTimeoutMillis();
		Map<String, String> headers = this.getHeaders();

		// prepare request

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		super.invoke(methodName, arguments, buffer);
		buffer.close();

		// create URLConnection

		log.debug("JSON-RPC: " + methodName + " to " + serviceUrl);

		HttpURLConnection con = (HttpURLConnection)serviceUrl.openConnection(connectionProxy);
		con.setConnectTimeout(connectionTimeoutMillis);
		con.setReadTimeout(readTimeoutMillis);
		con.setAllowUserInteraction(false);
		con.setDefaultUseCaches(false);
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);
		con.setInstanceFollowRedirects(true);
		con.setRequestMethod("POST");

		// add headers

		for (Entry<String, String> entry : headers.entrySet()) {
			con.setRequestProperty(entry.getKey(), entry.getValue());
		}
		for (Entry<String, String> entry : extraHeaders.entrySet()) {
			con.setRequestProperty(entry.getKey(), entry.getValue());
		}
		con.setRequestProperty("Content-Type", "application/json-rpc");
		con.setRequestProperty("Content-Length", Integer.toString(buffer.size()));

		// open the connection

		con.connect();

		// send request and read response

		con.getOutputStream().write(buffer.toByteArray());
		con.getOutputStream().flush();
		con.getOutputStream().close();

		Object response = super.readResponse(returnType, con.getInputStream());
		con.getInputStream().close();

		// done

		con.disconnect();

		return response;
	}
}