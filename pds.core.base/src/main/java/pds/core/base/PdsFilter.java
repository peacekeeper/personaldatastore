package pds.core.base;

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
import org.eclipse.higgins.xdi4j.messaging.server.EndpointRegistry;
import org.eclipse.higgins.xdi4j.messaging.server.EndpointServlet;
import org.eclipse.higgins.xdi4j.messaging.server.MessagingTarget;
import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractMessagingTarget;
import org.eclipse.higgins.xdi4j.messaging.server.impl.CompoundMessagingTarget;
import org.eclipse.higgins.xdi4j.messaging.server.impl.graph.GraphMessagingTarget;

import pds.core.base.messagingtargets.ContextResourceMessagingTarget;
import pds.core.base.messagingtargets.PdsResourceMessagingTarget;

/**
 * A servlet filter that instantiates PDS instances as needed and mounts them
 * as XDI4j messaging targets.
 * 
 * It uses one or more PdsInstanceFactory's for instantiating PDS instances, e.g. just a single instance,
 * or one instance per user, etc.
 * 
 * It also uses a PdsGraphFactory for instantiating one of the XDI4j backend Graph implementations
 * 
 * @author Markus
 */
public class PdsFilter implements Filter {

	private static Log log = LogFactory.getLog(PdsFilter.class.getName());

	private PdsInstanceFactory[] pdsInstanceFactories;
	private PdsGraphFactory pdsGraphFactory;
	private EndpointServlet endpointServlet;

	public void init(FilterConfig filterConfig) throws ServletException {

		log.info("Initializing...");

		if (this.pdsInstanceFactories == null || this.pdsInstanceFactories.length < 0) throw new ServletException("Please configure at least one PdsInstanceFactory!");
		if (this.pdsGraphFactory == null) throw new ServletException("Please configure a PdsGraphFactory!");
		if (this.endpointServlet == null) throw new ServletException("Please configure the EndpointServlet!");

		for (PdsInstanceFactory pdsInstanceFactory : this.pdsInstanceFactories) {

			try {

				pdsInstanceFactory.init(filterConfig);
			} catch (PdsException ex) {

				throw new ServletException("Cannot initialize PDS connection factory " + pdsInstanceFactory.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
			}
		}

		try {

			this.pdsGraphFactory.init(filterConfig);
		} catch (PdsException ex) {

			throw new ServletException("Cannot initialize PDS graph factory: " + ex.getMessage(), ex);
		}

		log.info("Initializing complete.");
	}

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		String requestUri = ((HttpServletRequest) request).getRequestURI();
		String contextPath = ((HttpServletRequest) request).getContextPath(); 
		String path = requestUri.substring(contextPath.length() + 1);

		while (path.startsWith("/")) path = path.substring(1);

		// try all our PDS instance factories 

		for (PdsInstanceFactory pdsInstanceFactory : this.pdsInstanceFactories) {

			// find out what target this request applies to (if any)

			log.debug("With " + pdsInstanceFactory.getClass().getSimpleName() + ": Looking up target at path \"" + path + "\"");

			String target = pdsInstanceFactory.getTarget(path);
			if (target == null) {

				log.debug("With " + pdsInstanceFactory.getClass().getSimpleName() + ": No target at path \"" + path + "\"");
				continue;
			}

			log.debug("With " + pdsInstanceFactory.getClass().getSimpleName() + ": Target \"" + target + "\" at path \"" + path + "\"");

			// check if we already have that target

			EndpointRegistry endpointRegistry = this.endpointServlet.getEndpointRegistry();

			MessagingTarget messagingTarget = endpointRegistry.findMessagingTargetByPath(target, true);
			if (messagingTarget != null) {

				log.debug("Already have messaging target for \"" + target + "\"");
				chain.doFilter(request, response);
				return;
			}

			// look up the appropriate PDS instance for our target	

			log.debug("With " + pdsInstanceFactory.getClass().getSimpleName() + ": Looking up PDS instance at \"" + target + "\"");

			PdsInstance pdsInstance = null;

			try {

				pdsInstance = pdsInstanceFactory.getPdsInstance(target);
			} catch (Exception ex) {

				log.error("With " + pdsInstanceFactory.getClass().getSimpleName() + ": Cannot look up PDS instance at \"" + target + "\": " + ex.getMessage(), ex);
				((HttpServletResponse) response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
				return;
			}

			if (pdsInstance == null) {

				log.debug("With " + pdsInstanceFactory.getClass().getSimpleName() + ": No PDS instance at \"" + target + "\"");
				continue;
			}

			log.debug("With " + pdsInstanceFactory.getClass().getSimpleName() + ": PDS instance " + pdsInstance.getClass().getSimpleName() + " at \"" + target + "\"");

			// create and register messaging target for the PDS instance

			try {

				log.debug("Creating messaging target for PDS instance " + pdsInstance.getClass().getSimpleName());

				messagingTarget = this.createMessagingTarget(endpointRegistry, pdsInstance);
				endpointRegistry.registerMessagingTarget(target, messagingTarget);

				log.info("Successfully registered messaging target at \"" + target + "\"");
			} catch (Exception ex) {

				log.error("Cannot create and register messaging target at \"" + target + "\": " + ex.getMessage(), ex);
				((HttpServletResponse) response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
				return;
			}

			// done

			break;
		}

		chain.doFilter(request, response);
	}

	private MessagingTarget createMessagingTarget(EndpointRegistry endpointRegistry, PdsInstance pdsInstance) throws Exception {

		// create CompoundMessagingTarget

		CompoundMessagingTarget compoundMessagingTarget = new CompoundMessagingTarget();
		compoundMessagingTarget.setMode(CompoundMessagingTarget.MODE_WRITE_FIRST_HANDLED);

		// create and add ContextResourceMessagingTarget

		ContextResourceMessagingTarget contextResourceMessagingTarget = new ContextResourceMessagingTarget();
		contextResourceMessagingTarget.setPdsInstance(pdsInstance);
		contextResourceMessagingTarget.init(endpointRegistry);

		compoundMessagingTarget.getMessagingTargets().add(contextResourceMessagingTarget);

		// create and add PdsResourceMessagingTarget

		PdsResourceMessagingTarget pdsResourceMessagingTarget = new PdsResourceMessagingTarget();
		pdsResourceMessagingTarget.setPdsInstance(pdsInstance);
		pdsResourceMessagingTarget.init(endpointRegistry);

		compoundMessagingTarget.getMessagingTargets().add(pdsResourceMessagingTarget);

		// add PdsInstanceMessagingTargets

		for (AbstractMessagingTarget pdsInstanceMessagingTarget : pdsInstance.getMessagingTargets()) {

			pdsInstanceMessagingTarget.init(endpointRegistry);

			compoundMessagingTarget.getMessagingTargets().add(pdsInstanceMessagingTarget);
		}

		// get graph

		Graph graph = this.pdsGraphFactory.getPdsInstanceGraph(pdsInstance);

		// create and add GraphMessagingTarget

		GraphMessagingTarget graphMessagingTarget = new GraphMessagingTarget();
		graphMessagingTarget.setGraph(graph);
		graphMessagingTarget.init(endpointRegistry);

		compoundMessagingTarget.getMessagingTargets().add(graphMessagingTarget);

		// init and done

		compoundMessagingTarget.init(endpointRegistry);

		return compoundMessagingTarget;
	}

	public PdsInstanceFactory[] getPdsInstanceFactories() {

		return this.pdsInstanceFactories;
	}

	public void setPdsInstanceFactories(PdsInstanceFactory[] pdsInstanceFactories) {

		this.pdsInstanceFactories = pdsInstanceFactories;
	}

	public PdsGraphFactory getPdsGraphFactory() {

		return this.pdsGraphFactory;
	}

	public void setPdsGraphFactory(PdsGraphFactory pdsGraphFactory) {

		this.pdsGraphFactory = pdsGraphFactory;
	}

	public EndpointServlet getEndpointServlet() {

		return this.endpointServlet;
	}

	public void setEndpointServlet(EndpointServlet endpointServlet) {

		this.endpointServlet = endpointServlet;
	}
}
