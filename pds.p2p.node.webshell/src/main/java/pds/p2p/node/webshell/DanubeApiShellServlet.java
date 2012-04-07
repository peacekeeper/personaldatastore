package pds.p2p.node.webshell;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.Admin;
import pds.p2p.api.Orion;
import pds.p2p.api.Polaris;
import pds.p2p.api.Sirius;
import pds.p2p.api.Vega;
import pds.p2p.api.annotation.DanubeApi;
import pds.p2p.api.node.client.DanubeApiClient;

public class DanubeApiShellServlet extends HttpServlet {

	private static final long serialVersionUID = 7365330254187946790L;

	private static Logger log = LoggerFactory.getLogger(DanubeApiShellServlet.class);

	private WrapFactory wrapFactory;

	@Override
	public void init(ServletConfig config) throws ServletException {

		super.init(config);

		this.wrapFactory = new MyWrapFactory();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// read parameters

		String command = request.getParameter("command");

		// prepare Rhino context and scope

		Context context = Context.enter();
		context.setWrapFactory(this.wrapFactory);

		ScriptableObject scope = this.getScope(request, context);

		// execute command

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

	private ScriptableObject getScope(HttpServletRequest request, Context context) {

		ScriptableObject scope = (ScriptableObject) request.getSession().getAttribute("scope");
		if (scope != null) return scope;
		scope = context.initStandardObjects();

		log.info("Adding JavaScript objects...");

		scope.defineProperty(Admin.class.getAnnotation(DanubeApi.class).name(), Context.javaToJS(DanubeApiClient.adminObject, scope), ScriptableObject.DONTENUM);
		scope.defineProperty(Orion.class.getAnnotation(DanubeApi.class).name(), Context.javaToJS(DanubeApiClient.orionObject, scope), ScriptableObject.DONTENUM);
		scope.defineProperty(Vega.class.getAnnotation(DanubeApi.class).name(), Context.javaToJS(DanubeApiClient.vegaObject, scope), ScriptableObject.DONTENUM);
		scope.defineProperty(Sirius.class.getAnnotation(DanubeApi.class).name(), Context.javaToJS(DanubeApiClient.siriusObject, scope), ScriptableObject.DONTENUM);
		scope.defineProperty(Polaris.class.getAnnotation(DanubeApi.class).name(), Context.javaToJS(DanubeApiClient.polarisObject, scope), ScriptableObject.DONTENUM);

		request.getSession().setAttribute("scope", scope);

		return scope;
	}
}
