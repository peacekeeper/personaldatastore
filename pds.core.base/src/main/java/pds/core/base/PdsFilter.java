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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.core.base.messagingtargets.PdsMessagingTarget;
import xdi2.core.Graph;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.impl.AbstractMessagingTarget;
import xdi2.messaging.target.impl.CompoundMessagingTarget;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.server.EndpointRegistry;
import xdi2.server.EndpointServlet;

/**
 * A servlet filter that instantiates PDS instances as needed and mounts them
 * as XDI2 messaging targets.
 * 
 * It uses one or more PdsInstanceFactory's for instantiating PDS instances, e.g. just a single instance,
 * or one instance per user, etc.
 * 
 * It also uses a PdsGraphFactory for instantiating one of the XDI2 backend Graph implementations
 * 
 * @author Markus
 */
public class PdsFilter implements Filter {

	private static Logger log = LoggerFactory.getLogger(PdsFilter.class.getName());

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

			// find out what PDS path this request applies to (if any)

			log.debug("With " + pdsInstanceFactory.getClass().getSimpleName() + ": Looking up PDS path at \"" + path + "\"");

			String pdsPath = pdsInstanceFactory.getPdsPath(path);
			if (pdsPath == null) {

				log.debug("With " + pdsInstanceFactory.getClass().getSimpleName() + ": No PDS path at \"" + path + "\"");
				continue;
			}

			log.debug("With " + pdsInstanceFactory.getClass().getSimpleName() + ": PDS path \"" + pdsPath + "\" at \"" + path + "\"");

			// check if we already have a messaging target

			EndpointRegistry endpointRegistry = this.endpointServlet.getEndpointRegistry();

			MessagingTarget messagingTarget = endpointRegistry.getMessagingTarget(pdsPath);
			if (messagingTarget != null) {

				log.debug("Already have messaging target for \"" + pdsPath + "\"");
				chain.doFilter(request, response);
				return;
			}

			// look up the appropriate PDS instance for our PDS path	

			log.debug("With " + pdsInstanceFactory.getClass().getSimpleName() + ": Looking up PDS instance at \"" + pdsPath + "\"");

			PdsInstance pdsInstance = null;

			try {

				pdsInstance = pdsInstanceFactory.getPdsInstance(pdsPath);
			} catch (Exception ex) {

				log.error("With " + pdsInstanceFactory.getClass().getSimpleName() + ": Cannot look up PDS instance at \"" + pdsPath + "\": " + ex.getMessage(), ex);
				((HttpServletResponse) response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
				return;
			}

			if (pdsInstance == null) {

				log.debug("With " + pdsInstanceFactory.getClass().getSimpleName() + ": No PDS instance at \"" + pdsPath + "\"");
				continue;
			}

			log.debug("With " + pdsInstanceFactory.getClass().getSimpleName() + ": PDS instance " + pdsInstance.getClass().getSimpleName() + " at \"" + pdsPath + "\"");

			// create and register messaging target for the PDS instance

			try {

				log.debug("Creating messaging target for PDS instance " + pdsInstance.getClass().getSimpleName());

				messagingTarget = this.createMessagingTarget(endpointRegistry, pdsInstance);

				String[] allMountTargets = pdsInstanceFactory.getAllPdsPaths(pdsInstance);
				for (String mountTarget : allMountTargets) {

					endpointRegistry.registerMessagingTarget(mountTarget, messagingTarget);
				}

				log.info("Successfully registered messaging target at \"" + pdsPath + "\"");
			} catch (Exception ex) {

				log.error("Cannot create and register messaging target at \"" + pdsPath + "\": " + ex.getMessage(), ex);
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

		// create and add PdsMessagingTarget

		PdsMessagingTarget pdsMessagingTarget = new PdsMessagingTarget();
		pdsMessagingTarget.setPdsInstance(pdsInstance);
		pdsMessagingTarget.init();

		compoundMessagingTarget.getMessagingTargets().add(pdsMessagingTarget);

		// add additional MessagingTargets from the PdsInstance

		for (AbstractMessagingTarget additionalMessagingTarget : pdsInstance.getAdditionalMessagingTargets()) {

			additionalMessagingTarget.init();

			compoundMessagingTarget.getMessagingTargets().add(additionalMessagingTarget);
		}

		// get graph

		Graph graph = this.pdsGraphFactory.getPdsInstanceGraph(pdsInstance);

		// create and add GraphMessagingTarget

		GraphMessagingTarget graphMessagingTarget = new GraphMessagingTarget();
		graphMessagingTarget.setGraph(graph);
		graphMessagingTarget.init();

		compoundMessagingTarget.getMessagingTargets().add(graphMessagingTarget);

		// init and done

		compoundMessagingTarget.init();

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
