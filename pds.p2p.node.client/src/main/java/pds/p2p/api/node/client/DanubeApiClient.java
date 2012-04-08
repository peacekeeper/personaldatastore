package pds.p2p.api.node.client;

import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.Admin;
import pds.p2p.api.Orion;
import pds.p2p.api.Polaris;
import pds.p2p.api.Sirius;
import pds.p2p.api.Vega;
import pds.p2p.api.annotation.DanubeApi;

import com.googlecode.jsonrpc4j.ProxyUtil;

public class DanubeApiClient {

	private static Logger log = LoggerFactory.getLogger(DanubeApiClient.class);

	private static Properties properties;

	public static Admin adminObject;
	public static Orion orionObject;
	public static Vega vegaObject;
	public static Sirius siriusObject;
	public static Polaris polarisObject;

	public static void init() throws Exception {

		log.info("init()");

		// Properties

		properties = new Properties();
		properties.load(DanubeApiClient.class.getResourceAsStream("/application.properties"));

		// JSON-RPC

		String serviceUrl = properties.getProperty("server.url", "http://localhost:9090/");
		if (! serviceUrl.endsWith("/")) serviceUrl += "/";

		adminObject = ProxyUtil.createProxy(DanubeApiClient.class.getClassLoader(), Admin.class, new MyJsonRpcHttpClient(new URL(serviceUrl + "jsonrpc-" + Admin.class.getAnnotation(DanubeApi.class).name())));
		orionObject = ProxyUtil.createProxy(DanubeApiClient.class.getClassLoader(), Orion.class, new MyJsonRpcHttpClient(new URL(serviceUrl + "jsonrpc-" + Orion.class.getAnnotation(DanubeApi.class).name())));
		vegaObject = ProxyUtil.createProxy(DanubeApiClient.class.getClassLoader(), Vega.class, new MyJsonRpcHttpClient(new URL(serviceUrl + "jsonrpc-" + Vega.class.getAnnotation(DanubeApi.class).name())));
		siriusObject = ProxyUtil.createProxy(DanubeApiClient.class.getClassLoader(), Sirius.class, new MyJsonRpcHttpClient(new URL(serviceUrl + "jsonrpc-" + Sirius.class.getAnnotation(DanubeApi.class).name())));
		polarisObject = ProxyUtil.createProxy(DanubeApiClient.class.getClassLoader(), Polaris.class, new MyJsonRpcHttpClient(new URL(serviceUrl + "jsonrpc-" + Polaris.class.getAnnotation(DanubeApi.class).name())));
	}

	public static void shutdown() {

		log.info("shutdown()");

		adminObject = null;
		orionObject = null;
		vegaObject = null;
		siriusObject = null;
		polarisObject = null;
	}
}
