package pds.p2p.node;

import java.lang.reflect.Method;
import java.util.Date;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.Admin;
import pds.p2p.api.annotation.DanubeApi;

public class AdminImpl implements Admin {

	private static final Logger log = LoggerFactory.getLogger(AdminImpl.class);

	private Date startTime;
	private Server server;
	private Context context;

	public AdminImpl(Date startTime, Server server, Context context) {

		this.startTime = startTime;
		this.server = server;
		this.context = context;
	}

	public void init() throws Exception {

	}

	public void shutdown() {

	}

	public String hello() {

		return "Hello World";
	}

	public String uptime() {

		return "" + (new Date().getTime() - this.startTime.getTime());
	}

	public String helpApis() throws Exception {

		StringBuffer buffer = new StringBuffer();

		// list JSON RPC servlets

		for (ServletMapping servletMapping : this.context.getServletHandler().getServletMappings()) {

			String servletName = servletMapping.getServletName();

			ServletHolder servletHolder = this.context.getServletHandler().getServlet(servletName);

			JsonRpcServlet servlet = null;
			if (servletHolder != null) servlet = (JsonRpcServlet) servletHolder.getServlet();

			if (servlet == null) continue;

			// find interface

			Object object = servlet.getJsonRpcObject();
			Class<?> clazz = object.getClass();
			Class<?> interfaze = null;

			for (Class<?> clazzInterfaze : clazz.getInterfaces()) {

				System.err.println(clazzInterfaze.getCanonicalName());

				if (clazzInterfaze.getAnnotation(DanubeApi.class) != null) {

					interfaze = clazzInterfaze;
					break;
				}
			}

			if (interfaze == null) return null;

			// print help

			DanubeApi apiInterface = interfaze.getAnnotation(DanubeApi.class);

			buffer.append(apiInterface.name() + " - " + apiInterface.description() + "\n");
		}

		return buffer.toString();
	}

	public String helpApi(String apiName) throws Exception {

		// find JSON RPC servlet at this path

		String pathSpec = "/" + apiName;

		JsonRpcServlet servlet = null;

		for (ServletMapping servletMapping : this.context.getServletHandler().getServletMappings()) {

			for (String servletPathSpec : servletMapping.getPathSpecs()) {

				if (servletPathSpec.equals(pathSpec)) {

					String servletName = servletMapping.getServletName();

					ServletHolder servletHolder = this.context.getServletHandler().getServlet(servletName);
					if (servletHolder != null) servlet = (JsonRpcServlet) servletHolder.getServlet();
					if (servlet != null) break;
				}
			}
		}

		if (servlet == null) return null;

		// find interface

		Object object = servlet.getJsonRpcObject();
		Class<?> clazz = object.getClass();
		Class<?> interfaze = null;

		for (Class<?> clazzInterfaze : clazz.getInterfaces()) {

			System.err.println(clazzInterfaze.getCanonicalName());

			if (clazzInterfaze.getAnnotation(DanubeApi.class) != null) {

				interfaze = clazzInterfaze;
				break;
			}
		}

		if (interfaze == null) return null;

		// print help

		StringBuffer buffer = new StringBuffer();

		for (Method method : interfaze.getMethods()) {

			buffer.append(apiName + "." + method.getName() + "(");

			Class<?> parameterTypes[] = method.getParameterTypes();
			for (int i=0; i<parameterTypes.length; i++) {

				if (i > 0) buffer.append(",");
				buffer.append(parameterTypes[i].getSimpleName().toLowerCase());
			}

			buffer.append(")\n");
		}

		return buffer.toString();
	}

	public void stop() throws Exception {

		new Thread() {

			public void run() {

				try {

					log.info("Preparing to shut down the server...");
					Thread.sleep(1000);
					log.info("Shutting down the server...");
					server.stop();
					log.info("Server has stopped.");
				} catch (Exception ex) {

					log.error("Error when stopping server: " + ex.getMessage(), ex);
				}
			}
		}.start();
	}
}
