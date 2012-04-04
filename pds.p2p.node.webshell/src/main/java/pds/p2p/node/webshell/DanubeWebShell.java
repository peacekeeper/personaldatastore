package pds.p2p.node.webshell;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.Orion;
import pds.p2p.api.Polaris;
import pds.p2p.api.Sirius;
import pds.p2p.api.Vega;
import pds.p2p.api.annotations.ApiInterface;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;

public class DanubeWebShell extends HttpServlet {

	private static final long serialVersionUID = 7365330254187946790L;

	private static Logger log = LoggerFactory.getLogger(DanubeWebShell.class);

	private static Properties properties;

	private static Admin adminObject;
	private static Orion orionObject;
	private static Vega vegaObject;
	private static Sirius siriusObject;
	private static Polaris polarisObject;

	static {

		try {

			log.info("init()");

			// Properties

			properties = new Properties();
			properties.load(DanubeWebShell.class.getResourceAsStream("/application.properties"));

			// JSON-RPC

			String serviceUrl = properties.getProperty("server.url", "http://localhost:9090/");
			if (! serviceUrl.endsWith("/")) serviceUrl += "/";

			adminObject = ProxyUtil.createProxy(DanubeWebShell.class.getClassLoader(), Admin.class, new JsonRpcHttpClient(new URL(serviceUrl + "admin")));
			orionObject = ProxyUtil.createProxy(DanubeWebShell.class.getClassLoader(), Orion.class, new JsonRpcHttpClient(new URL(serviceUrl + "orion")));
			vegaObject = ProxyUtil.createProxy(DanubeWebShell.class.getClassLoader(), Vega.class, new JsonRpcHttpClient(new URL(serviceUrl + "vega")));
			siriusObject = ProxyUtil.createProxy(DanubeWebShell.class.getClassLoader(), Sirius.class, new JsonRpcHttpClient(new URL(serviceUrl + "sirius")));
			polarisObject = ProxyUtil.createProxy(DanubeWebShell.class.getClassLoader(), Polaris.class, new JsonRpcHttpClient(new URL(serviceUrl + "polaris")));
		} catch (Throwable ex) {

			throw new RuntimeException(ex);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// prepare Rhino context and scope

		Context context = Context.enter();
		ScriptableObject scope = (ScriptableObject) request.getSession().getAttribute("scope");

		if (scope == null) {

			scope = context.initStandardObjects();

			log.info("Adding JavaScript objects...");

			scope.defineProperty(Admin.class.getAnnotation(ApiInterface.class).name(), Context.javaToJS(adminObject, scope), ScriptableObject.DONTENUM);
			scope.defineProperty(Orion.class.getAnnotation(ApiInterface.class).name(), Context.javaToJS(orionObject, scope), ScriptableObject.DONTENUM);
			scope.defineProperty(Vega.class.getAnnotation(ApiInterface.class).name(), Context.javaToJS(vegaObject, scope), ScriptableObject.DONTENUM);
			scope.defineProperty(Sirius.class.getAnnotation(ApiInterface.class).name(), Context.javaToJS(siriusObject, scope), ScriptableObject.DONTENUM);
			scope.defineProperty(Polaris.class.getAnnotation(ApiInterface.class).name(), Context.javaToJS(polarisObject, scope), ScriptableObject.DONTENUM);

			request.getSession().setAttribute("scope", scope);
		}

		// execute command

		String command = request.getParameter("command");
		String result;

		try {

			result = Context.toString(context.evaluateString(scope, command, "line", 1, null));
		} catch (RhinoException ex) {

			result = "JavaScript error: " + ex.getMessage();
		} catch (Exception ex) {

			log.warn("Internal error: " + ex.getMessage(), ex);
			result = "Internal error: " + ex.getMessage();
		}

		response.setContentType("text/plain");
		response.getWriter().print(result);

		// done

		Context.exit();
	}
}
