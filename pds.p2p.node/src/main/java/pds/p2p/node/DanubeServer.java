package pds.p2p.node;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.Orion;
import pds.p2p.api.Polaris;
import pds.p2p.api.Sirius;
import pds.p2p.api.Vega;

import com.googlecode.jsonrpc4j.JsonRpcServer;

public class DanubeServer {

	private static Logger log = LoggerFactory.getLogger(DanubeServer.class);

	private static Server server;

	private static Admin adminObject;
	private static Orion orionObject;
	private static Vega vegaObject;
	private static Sirius siriusObject;
	private static Polaris polarisObject;

	private static void init() throws Throwable {

		log.info("init()");

		adminObject = new AdminImpl(new Date(), server);

		orionObject = pds.p2p.api.orion.OrionFactory.getOrion();
		orionObject.init();
		if (pds.p2p.api.orion.OrionFactory.getException() != null) throw pds.p2p.api.orion.OrionFactory.getException();

		vegaObject = pds.p2p.api.vega.VegaFactory.getVega(orionObject);
		vegaObject.init();
		if (pds.p2p.api.vega.VegaFactory.getException() != null) throw pds.p2p.api.vega.VegaFactory.getException();

		siriusObject = pds.p2p.api.sirius.SiriusFactory.getSirius(vegaObject);
		siriusObject.init();
		if (pds.p2p.api.sirius.SiriusFactory.getException() != null) throw pds.p2p.api.sirius.SiriusFactory.getException();

		polarisObject = pds.p2p.api.polaris.PolarisFactory.getPolaris(orionObject);
		polarisObject.init();
		if (pds.p2p.api.polaris.PolarisFactory.getException() != null) throw pds.p2p.api.polaris.PolarisFactory.getException();
	}

	public static void main(String[] args) throws Throwable {

		server = new Server(8080);

		init();

		Context context = new Context(server, "/");
		context.addServlet(new ServletHolder(new JsonRpcServlet(adminObject)), "/admin");
		context.addServlet(new ServletHolder(new JsonRpcServlet(orionObject)), "/orion");
		context.addServlet(new ServletHolder(new JsonRpcServlet(vegaObject)), "/vega");
		context.addServlet(new ServletHolder(new JsonRpcServlet(siriusObject)), "/sirius");
		context.addServlet(new ServletHolder(new JsonRpcServlet(polarisObject)), "/polaris");

		server.setGracefulShutdown(1000);
		server.setStopAtShutdown(true);
		server.start();
		server.join();
	}

	private static class JsonRpcServlet extends HttpServlet {

		private static final long serialVersionUID = 7453275488406497744L;

		private Object jsonRpcObject;
		private JsonRpcServer jsonRpcServer;

		private JsonRpcServlet(Object jsonRpcObject) {

			this.jsonRpcObject = jsonRpcObject;
		}

		@Override
		public void init(ServletConfig config) throws ServletException {

			super.init(config);

			this.jsonRpcServer = new JsonRpcServer(jsonRpcObject, jsonRpcObject.getClass());
		}

		@Override
		protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

			log.debug(this.jsonRpcObject.getClass() + ": service()");

			this.jsonRpcServer.handle(request, response);
		}
	};
}
