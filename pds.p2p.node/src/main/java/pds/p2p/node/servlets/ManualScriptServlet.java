package pds.p2p.node.servlets;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.node.MyWrapFactory;
import pds.p2p.node.ScriptRegistry;

public class ManualScriptServlet extends HttpServlet {

	private static final long serialVersionUID = -1368464795652451872L;

	private static Logger log = LoggerFactory.getLogger(ManualScriptServlet.class);

	private ScriptRegistry scriptRegistry;

	public ManualScriptServlet() {

		super();

		this.scriptRegistry = new ScriptRegistry(new File(".", "scripts-manual/"));
	}

	@Override
	public void init(ServletConfig config) throws ServletException {

		super.init(config);

		// init script registry

		Context context = Context.enter();
		context.setWrapFactory(MyWrapFactory.getInstance());

		this.scriptRegistry.init(context);

		Context.exit();
	}

	@Override
	public void destroy() {

		super.destroy();

		// shut down script registry

		Context context = Context.enter();
		context.setWrapFactory(MyWrapFactory.getInstance());
		
		this.scriptRegistry.shutdown(context);
		
		Context.exit();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// read parameters
		
		String scriptId = request.getParameter("scriptId");
		
		if (scriptId == null) {

			log.warn("Missing 'scriptId' parameter");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing 'scriptId' parameter");
			return;
		}

		// run script

		log.info("Running scripts..");

		Context context = Context.enter();
		context.setWrapFactory(MyWrapFactory.getInstance());
		
		String result;
		
		try {

			result = this.scriptRegistry.runScript(context, scriptId);
		} catch (Exception ex) {

			log.warn("Problem while running script '" + scriptId + "': " + ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Problem while running script '" + scriptId + "': " + ex.getMessage());
			return;
		}

		// done
		
		response.getOutputStream().print(result);
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}
}
