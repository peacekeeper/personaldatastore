package pds.solution.plug;

import java.util.Date;
import java.util.Properties;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.Admin;
import pds.p2p.api.Orion;
import pds.p2p.api.Polaris;
import pds.p2p.api.Sirius;
import pds.p2p.api.Vega;
import pds.p2p.api.annotation.DanubeApi;
import pds.solution.plug.admin.AdminImpl;
import pds.solution.plug.servlets.ManualScriptServlet;
import pds.solution.plug.servlets.MyJsonRpcServlet;
import pds.solution.plug.servlets.PacketServlet;


public class JettyServer {

	private static final Logger log = LoggerFactory.getLogger(JettyServer.class);

	private static final String PROPERTIES_KEY_SERVERPORT = "server.port";
	private static final String PROPERTIES_DEFAULT_SERVERPORT = "9090";

	private static Properties properties;

	private static Server server;
	private static Context context;

	private static LoopScriptThread loopScriptThread;

	public static Admin adminObject;
	public static Orion orionObject;
	public static Vega vegaObject;
	public static Sirius siriusObject;
	public static Polaris polarisObject;

	public static void main(String[] args) throws Throwable {

		init(args);
		server(args);
		shutdown();
	}

	private static void init(String[] args) throws Throwable {

		log.info("init()");

		// init properties

		properties = new Properties();
		properties.load(JettyServer.class.getResourceAsStream("/application.properties"));

		// init LoopScriptThread

		loopScriptThread = new LoopScriptThread();

		// init Jetty

		int port = Integer.parseInt(properties.getProperty(PROPERTIES_KEY_SERVERPORT, PROPERTIES_DEFAULT_SERVERPORT));

		server = new Server(port);
		context = new Context(server, "/");
		
		server.

		// init API

		adminObject = new AdminImpl(new Date(), server, context, loopScriptThread);

		orionObject = pds.p2p.api.orion.OrionFactory.getOrion();
		if (pds.p2p.api.orion.OrionFactory.getException() != null) throw pds.p2p.api.orion.OrionFactory.getException();

		vegaObject = pds.p2p.api.vega.VegaFactory.getVega(orionObject);
		if (pds.p2p.api.vega.VegaFactory.getException() != null) throw pds.p2p.api.vega.VegaFactory.getException();

		siriusObject = pds.p2p.api.sirius.SiriusFactory.getSirius(vegaObject);
		if (pds.p2p.api.sirius.SiriusFactory.getException() != null) throw pds.p2p.api.sirius.SiriusFactory.getException();

		polarisObject = pds.p2p.api.polaris.PolarisFactory.getPolaris(orionObject);
		if (pds.p2p.api.polaris.PolarisFactory.getException() != null) throw pds.p2p.api.polaris.PolarisFactory.getException();

		adminObject.init();
		orionObject.init();
		vegaObject.init();
		siriusObject.init();
		polarisObject.init();
	}

	private static void shutdown() throws Exception {

		log.info("shutdown()");

		// shutdown LoopScriptThread

		loopScriptThread.stopRunning();
		loopScriptThread.join();

		// shutdown API

		adminObject.shutdown();
		polarisObject.shutdown();
		siriusObject.shutdown();
		vegaObject.shutdown();
		orionObject.shutdown();

		adminObject = null;
		polarisObject = null;
		siriusObject = null;
		vegaObject = null;
		orionObject = null;
	}

	private static void server(String[] args) throws Throwable {

		log.info("server()");

		// start LoopScriptThread

		log.info("Starting LoopScriptThread...");

		loopScriptThread.start();

		// start Jetty

		log.info("Starting Jetty...");

		context.addServlet(new ServletHolder(new ManualScriptServlet()), "/script");
		context.addServlet(new ServletHolder(new PacketServlet()), "/packet");
		context.addServlet(new ServletHolder(new MyJsonRpcServlet(adminObject)), "/" + Admin.class.getAnnotation(DanubeApi.class).name());
		context.addServlet(new ServletHolder(new MyJsonRpcServlet(orionObject)), "/" + Orion.class.getAnnotation(DanubeApi.class).name());
		context.addServlet(new ServletHolder(new MyJsonRpcServlet(vegaObject)), "/" + Vega.class.getAnnotation(DanubeApi.class).name());
		context.addServlet(new ServletHolder(new MyJsonRpcServlet(siriusObject)), "/" + Sirius.class.getAnnotation(DanubeApi.class).name());
		context.addServlet(new ServletHolder(new MyJsonRpcServlet(polarisObject)), "/" + Polaris.class.getAnnotation(DanubeApi.class).name());

		server.setGracefulShutdown(3000);
		server.setStopAtShutdown(true);
		server.start();
		server.join();
	};
}
