package pds.p2p.node.shell;

import java.net.URL;
import java.util.Properties;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.shell.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.Orion;
import pds.p2p.api.Polaris;
import pds.p2p.api.Sirius;
import pds.p2p.api.Vega;
import pds.p2p.api.annotations.ApiInterface;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;

public class DanubeShell {

	private static Logger log = LoggerFactory.getLogger(DanubeShell.class);

	private static Properties properties;

	private static Admin adminObject;
	private static Orion orionObject;
	private static Vega vegaObject;
	private static Sirius siriusObject;
	private static Polaris polarisObject;

	public static void main(String[] args) throws Throwable {

		init(args);
		shell(args);
		shutdown();
	}

	private static void init(String[] args) throws Throwable {

		log.info("init()");

		// Properties
		
		properties = new Properties();
		properties.load(DanubeShell.class.getResourceAsStream("/application.properties"));

		// JSON-RPC

		String serviceUrl = properties.getProperty("server.url", "http://localhost:9090/");
		if (! serviceUrl.endsWith("/")) serviceUrl += "/";

		adminObject = ProxyUtil.createProxy(DanubeShell.class.getClassLoader(), Admin.class, new JsonRpcHttpClient(new URL(serviceUrl + "admin")));
		orionObject = ProxyUtil.createProxy(DanubeShell.class.getClassLoader(), Orion.class, new JsonRpcHttpClient(new URL(serviceUrl + "orion")));
		vegaObject = ProxyUtil.createProxy(DanubeShell.class.getClassLoader(), Vega.class, new JsonRpcHttpClient(new URL(serviceUrl + "vega")));
		siriusObject = ProxyUtil.createProxy(DanubeShell.class.getClassLoader(), Sirius.class, new JsonRpcHttpClient(new URL(serviceUrl + "sirius")));
		polarisObject = ProxyUtil.createProxy(DanubeShell.class.getClassLoader(), Polaris.class, new JsonRpcHttpClient(new URL(serviceUrl + "polaris")));
	}

	private static void shutdown() {

		log.info("shutdown()");

		adminObject = null;
		polarisObject = null;
		siriusObject = null;
		vegaObject = null;
		orionObject = null;
	}

	private static void shell(String[] args) throws Throwable {

		log.info("shell()");

		// Rhino / JavaScript Shell

		org.mozilla.javascript.tools.shell.Main.global = new Global () {

			private static final long serialVersionUID = -4244134739915734959L;

			public void init(Context cx) {

				super.init(cx);

				log.info("Adding JavaScript objects...");

				this.defineProperty(Admin.class.getAnnotation(ApiInterface.class).name(), Context.javaToJS(adminObject, this), ScriptableObject.DONTENUM);
				this.defineProperty(Orion.class.getAnnotation(ApiInterface.class).name(), Context.javaToJS(orionObject, this), ScriptableObject.DONTENUM);
				this.defineProperty(Vega.class.getAnnotation(ApiInterface.class).name(), Context.javaToJS(vegaObject, this), ScriptableObject.DONTENUM);
				this.defineProperty(Sirius.class.getAnnotation(ApiInterface.class).name(), Context.javaToJS(siriusObject, this), ScriptableObject.DONTENUM);
				this.defineProperty(Polaris.class.getAnnotation(ApiInterface.class).name(), Context.javaToJS(polarisObject, this), ScriptableObject.DONTENUM);
			}
		};

		log.info("Running JavaScript shell...");
		org.mozilla.javascript.tools.shell.Main.main(args);
		log.info("JavaScript shell exited...");
	}
}
