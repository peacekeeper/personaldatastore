package pds.solution.plug;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyServer {

	private static final Logger log = LoggerFactory.getLogger(JettyServer.class);

	private static Server server;

	public static void main(String[] args) throws Throwable {

		init();
		server();
		shutdown();
	}

	private static void init() throws Throwable {

		log.info("init()");

		// port 8080

		SelectChannelConnector connector8080 = new SelectChannelConnector();
		connector8080.setName("8080");
		connector8080.setPort(8080);

		WebAppContext webapp8080 = new WebAppContext();
		webapp8080.setContextPath("/");
		webapp8080.setWar("./webapps/pds.p2p.node.webshell-0.1-SNAPSHOT-skinny.war");
		webapp8080.setConnectorNames(new String[] { "8080" });
		webapp8080.setExtractWAR(true);

		// port 9090

		SelectChannelConnector connector9090 = new SelectChannelConnector();
		connector9090.setName("9090");
		connector9090.setPort(9090);

		WebAppContext webapp9090 = new WebAppContext();
		webapp9090.setContextPath("/");
		webapp9090.setWar("./webapps/pds.p2p.node-0.1-SNAPSHOT-skinny.war");
		webapp9090.setConnectorNames(new String[] { "9090" });
		webapp9090.setExtractWAR(true);

		// port 10100

		SelectChannelConnector connector10100 = new SelectChannelConnector();
		connector10100.setName("10100");
		connector10100.setPort(10100);

		WebAppContext webapp10100 = new WebAppContext();
		webapp10100.setContextPath("/");
		webapp10100.setWar("./webapps/pds.core-0.1-SNAPSHOT-skinny.war");
		webapp10100.setConnectorNames(new String[] { "10100" });
		webapp10100.setExtractWAR(true);

		// init Jetty

		HandlerList handlerList = new HandlerList();
		handlerList.addHandler(webapp8080);
		handlerList.addHandler(webapp9090);
		handlerList.addHandler(webapp10100);
		
		server = new Server();
		
		server.setConnectors(new Connector[] { connector8080, connector9090, connector10100 });
		server.setHandler(handlerList);
	}

	private static void shutdown() throws Exception {

		log.info("shutdown()");

	}

	private static void server() throws Throwable {

		log.info("server()");

		// start Jetty

		log.info("Starting Jetty...");

		server.setGracefulShutdown(3000);
		server.setStopAtShutdown(true);
		server.start();
		server.join();
	};
}
