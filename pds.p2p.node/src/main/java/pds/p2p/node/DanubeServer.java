package pds.p2p.node;

import java.util.Date;
import java.util.Properties;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.Orion;
import pds.p2p.api.Polaris;
import pds.p2p.api.Sirius;
import pds.p2p.api.Vega;
import pds.p2p.api.annotations.ApiInterface;


public class DanubeServer {

	private static final Logger log = LoggerFactory.getLogger(DanubeServer.class);

	private static Properties properties;

	private static Server server;
	private static Context context;

	private static Admin adminObject;
	private static Orion orionObject;
	private static Vega vegaObject;
	private static Sirius siriusObject;
	private static Polaris polarisObject;

	public static void main(String[] args) throws Throwable {

		init(args);
		server(args);
		shutdown();
	}

	private static void init(String[] args) throws Throwable {

		log.info("init()");

		// Properties

		properties = new Properties();
		properties.load(DanubeServer.class.getResourceAsStream("/application.properties"));

		// Server and Context

		int port = Integer.parseInt(properties.getProperty("server.port", "9090"));
		server = new Server(port);

		context = new Context(server, "/");

		// JSON-RPC

		adminObject = new AdminImpl(new Date(), server, context);

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

	private static void shutdown() {

		log.info("shutdown()");

		adminObject = null;

		polarisObject.shutdown();
		polarisObject = null;

		siriusObject.shutdown();
		siriusObject = null;

		vegaObject.shutdown();
		vegaObject = null;

		orionObject.shutdown();
		orionObject = null;
	}

	private static void server(String[] args) throws Throwable {

		log.info("server()");

		context.addServlet(new ServletHolder(new JsonRpcServlet(adminObject)), "/" + Admin.class.getAnnotation(ApiInterface.class).name());
		context.addServlet(new ServletHolder(new JsonRpcServlet(orionObject)), "/" + Orion.class.getAnnotation(ApiInterface.class).name());
		context.addServlet(new ServletHolder(new JsonRpcServlet(vegaObject)), "/" + Vega.class.getAnnotation(ApiInterface.class).name());
		context.addServlet(new ServletHolder(new JsonRpcServlet(siriusObject)), "/" + Sirius.class.getAnnotation(ApiInterface.class).name());
		context.addServlet(new ServletHolder(new JsonRpcServlet(polarisObject)), "/" + Polaris.class.getAnnotation(ApiInterface.class).name());

		log.info("Starting server...");

		server.setGracefulShutdown(3000);
		server.setStopAtShutdown(true);
		server.start();
		server.join();
	};
}