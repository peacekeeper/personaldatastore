package pds.core;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.impl.keyvalue.bdb.BDBGraphFactory;
import org.eclipse.higgins.xdi4j.messaging.server.EndpointRegistry;
import org.eclipse.higgins.xdi4j.messaging.server.EndpointServlet;
import org.eclipse.higgins.xdi4j.messaging.server.MessagingTarget;
import org.eclipse.higgins.xdi4j.messaging.server.impl.CompoundMessagingTarget;
import org.eclipse.higgins.xdi4j.messaging.server.impl.graph.GraphMessagingTarget;
import org.eclipse.higgins.xdi4j.messaging.server.interceptor.impl.RoutingMessageInterceptor;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.messagingtargets.pds.ContextResourceMessagingTarget;
import pds.core.messagingtargets.pds.PdsResourceMessagingTarget;

public class PdsFilter implements Filter {

	private static Log log = LogFactory.getLog(PdsFilter.class.getName());

	private String servletContextName;
	private PdsConnectionFactory pdsConnectionFactory;
	private EndpointServlet endpointServlet;

	public void init(FilterConfig filterConfig) throws ServletException {

		this.servletContextName = filterConfig.getServletContext().getServletContextName();

		try {

			this.pdsConnectionFactory.init(filterConfig);
		} catch (PdsConnectionException ex) {

			throw new ServletException("Cannot initialize PDS connection factory: " + ex.getMessage(), ex);
		}
	}

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		String requestUri = ((HttpServletRequest) request).getRequestURI();
		String contextPath = ((HttpServletRequest) request).getContextPath(); 
		String path = requestUri.substring(contextPath.length() + 1);
		String target = path;
		if (target.indexOf("/") != -1) target = target.substring(0, path.indexOf("/"));
		if (! target.equals("")) target += "/";

		// check if we already have that target

		EndpointRegistry endpointRegistry = this.endpointServlet.getEndpointRegistry();

		MessagingTarget messagingTarget = endpointRegistry.findMessagingTargetByPath(target, true);

		if (messagingTarget == null) {

			log.info("Creating messaging target for /" + target);

			try {

				if (target.equals("")) {

					/*					// create CompoundMessagingTarget

					CompoundMessagingTarget compoundMessagingTarget = new CompoundMessagingTarget();
					compoundMessagingTarget.setMode(CompoundMessagingTarget.MODE_FIRST_HANDLED);

					// create and add IbrokerOperationMessagingTarget

					IbrokerOperationMessagingTarget ibrokerOperationMessagingTarget = new IbrokerOperationMessagingTarget();
					ibrokerOperationMessagingTarget.init(endpointRegistry);

					compoundMessagingTarget.getMessagingTargets().add(ibrokerOperationMessagingTarget);

					// create and add IbrokerOperationMessagingTarget

					IbrokerResourceMessagingTarget ibrokerResourceMessagingTarget = new IbrokerResourceMessagingTarget();
					ibrokerResourceMessagingTarget.init(endpointRegistry);

					compoundMessagingTarget.getMessagingTargets().add(ibrokerResourceMessagingTarget);

					// finish and register CompoundMessagingTarget

					compoundMessagingTarget.init(endpointRegistry);

					endpointRegistry.registerMessagingTarget(target, compoundMessagingTarget);*/
				} else {

					// retrieve the PDS connection

					String identifier = target;
					if (identifier.endsWith("/")) identifier = identifier.substring(0, identifier.length() - 1);

					PdsConnection pdsConnection = this.pdsConnectionFactory.getPdsConnection(identifier);

					if (pdsConnection == null) {

						throw new Exception("PDS not found.");
					}

					// create CompoundMessagingTarget

					CompoundMessagingTarget compoundMessagingTarget = new CompoundMessagingTarget();
					compoundMessagingTarget.setMode(CompoundMessagingTarget.MODE_WRITE_FIRST_HANDLED);

					// create and add ContextResourceMessagingTarget

					ContextResourceMessagingTarget contextResourceMessagingTarget = new ContextResourceMessagingTarget();
					contextResourceMessagingTarget.setPdsConnection(pdsConnection);
					contextResourceMessagingTarget.init(endpointRegistry);

					compoundMessagingTarget.getMessagingTargets().add(contextResourceMessagingTarget);

					// create and add IbrokerResourceMessagingTarget

					PdsResourceMessagingTarget xriResourceMessagingTarget = new PdsResourceMessagingTarget();
					xriResourceMessagingTarget.setPdsConnection(pdsConnection);
					xriResourceMessagingTarget.init(endpointRegistry);

					compoundMessagingTarget.getMessagingTargets().add(xriResourceMessagingTarget);

					// open graph

					XRI3Segment canonical = pdsConnection.getCanonical();

					String databasePath = "./pds.core-" + this.servletContextName + "/";
					String databaseName = (canonical != null) ? canonical.toString() : identifier;

					BDBGraphFactory graphFactory = new BDBGraphFactory();
					graphFactory.setDatabasePath(databasePath);
					graphFactory.setDatabaseName(databaseName);

					Graph graph;

					try {

						graph = graphFactory.openGraph();
					} catch (IOException ex) {

						throw new PdsConnectionException("Cannot open graph: " + ex.getMessage(), ex);
					}

					// create and add GraphMessagingTarget

					GraphMessagingTarget graphMessagingTarget = new GraphMessagingTarget();
					graphMessagingTarget.setGraph(graph);
					graphMessagingTarget.getMessageInterceptors().add(new RoutingMessageInterceptor());
					graphMessagingTarget.init(endpointRegistry);

					compoundMessagingTarget.getMessagingTargets().add(graphMessagingTarget);

					// finish and register CompoundMessagingTarget

					compoundMessagingTarget.init(endpointRegistry);

					endpointRegistry.registerMessagingTarget(target, compoundMessagingTarget);
				}
			} catch (Exception ex) {

				log.error("Cannot create messaging target for /" + target + ": " + ex.getMessage(), ex);
				((HttpServletResponse) response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
				return;
			}
		} else {

			log.info("Already have messaging target for /" + target);
		}

		chain.doFilter(request, response);
	}

	public PdsConnectionFactory getPdsConnectionFactory() {

		return this.pdsConnectionFactory;
	}

	public void setPdsConnectionFactory(PdsConnectionFactory pdsConnectionFactory) {

		this.pdsConnectionFactory = pdsConnectionFactory;
	}

	public EndpointServlet getEndpointServlet() {

		return this.endpointServlet;
	}

	public void setEndpointServlet(EndpointServlet endpointServlet) {

		this.endpointServlet = endpointServlet;
	}
}