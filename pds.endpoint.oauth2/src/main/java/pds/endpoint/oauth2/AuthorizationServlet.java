package pds.endpoint.oauth2;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.smartam.leeloo.as.request.OAuthAuthzRequest;
import net.smartam.leeloo.common.OAuth;
import net.smartam.leeloo.common.exception.OAuthProblemException;
import net.smartam.leeloo.common.message.OAuthResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.constants.DictionaryConstants;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.dictionary.Dictionary;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.util.iterators.IteratorListMaker;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.openxri.resolve.Resolver;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.ServletContextAware;

import pds.endpoint.oauth2.client.ClientAuthenticator;
import pds.endpoint.oauth2.util.StringUtil;
import pds.xdi.Xdi;
import pds.xdi.XdiContext;
import pds.xdi.XdiException;

public class AuthorizationServlet implements HttpRequestHandler, ServletContextAware {

	private static final Log log = LogFactory.getLog(AuthorizationServlet.class.getName());

	private static final Xdi xdi;

	private ClientAuthenticator clientAuthenticator;

	static {

		try {

			xdi = new Xdi(new Resolver(null));
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize XDI: " + ex.getMessage(), ex);
		}

/*		try {

			Properties properties = new Properties();
			properties.setProperty("resource.loader", "file");
			properties.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
			properties.setProperty("file.resource.loader.path", "path/to/your/templates");
			Velocity.init(properties);
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize Velocity: " + ex.getMessage(), ex);
		}*/
	}

	private ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {

		this.servletContext = servletContext;
	}

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		log.trace(request.getMethod() + ": " + request.getRequestURI() + ", Content-Type: " + request.getContentType() + ", Content-Length: " + request.getContentLength());

		try {

			if ("GET".equals(request.getMethod())) this.doGet(request, response);
			else if ("POST".equals(request.getMethod())) this.doPost(request, response);
			else response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	private void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// OAuth 2.0 request

		OAuthAuthzRequest oauthRequest = null;
		String redirectUri = null;

		try {

			oauthRequest = new OAuthAuthzRequest(request);
			request.getSession().setAttribute("request_uri", oauthRequest.getRedirectURI());
			request.getSession().setAttribute("scope", oauthRequest.getScopes().toArray(new String[oauthRequest.getScopes().size()]));
			request.getSession().setAttribute("response_type", oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE));
			log.info("OAuthAuthzRequest from client \"" + oauthRequest.getClientId() + "\" with response type \"" + oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE) + "\" and with scopes \"" + StringUtil.join(oauthRequest.getScopes(), ", ") + "\"");

			redirectUri = this.clientAuthenticator.getRedirectUri(oauthRequest);
			request.getSession().setAttribute("redirect_uri", redirectUri);

			if (! this.clientAuthenticator.isAuthenticated(oauthRequest)) throw OAuthProblemException.error("Client not authenticated.");
		} catch (OAuthProblemException ex) {

			log.error("OAuth problem: " + ex.getMessage(), ex);

			if (redirectUri == null) {

				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
				return;
			} else {

				OAuthResponse oauthResponse = OAuthResponse
				.errorResponse(HttpServletResponse.SC_FOUND)
				.error(ex)
				.location(redirectUri)
				.buildQueryMessage();

				response.sendRedirect(oauthResponse.getLocationUri());
			}
		}

		// send page

		this.sendPage(request, response, null);
	}

	private void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// find the XDI data

		String iname = request.getParameter("iname");
		String password = request.getParameter("password");

		if (iname == null || iname.trim().isEmpty() || password == null || password.trim().isEmpty()) {

			this.sendErrorPage(request, response, "Please complete the form.");
			return;
		}

		XdiContext context = null;

		try {

			context = this.getContext(iname, password);
			request.getSession().setAttribute("authenticated", Boolean.TRUE);
			request.getSession().setAttribute("context", context);
		} catch (XdiException ex) {

			this.sendErrorPage(request, response, "Incorrect password.");
			return;
		}

		List<XRI3Segment> accountPersonaXris = this.fetch(context);
		request.getSession().setAttribute("personas", accountPersonaXris);

		// go to scope

		response.sendRedirect("scope");
	}

	private void sendPage(HttpServletRequest request, HttpServletResponse response, Map<String, Object> properties) throws Exception {

		if (properties == null) properties = new HashMap<String, Object> ();
		properties.put("servletcontext", this.servletContext);
		properties.put("request", request);
		properties.put("session", request.getSession());

		// output

		response.setContentType("text/html");
		Writer writer = response.getWriter();

		VelocityContext velocityContext = new VelocityContext(properties);

		Velocity.evaluate(velocityContext, writer, "header", new FileReader(new File(this.servletContext.getRealPath("/WEB-INF/header.vm"))));
		Velocity.evaluate(velocityContext, writer, "main", new FileReader(new File(this.servletContext.getRealPath("/WEB-INF/authorization.vm"))));
		Velocity.evaluate(velocityContext, writer, "footer", new FileReader(new File(this.servletContext.getRealPath("/WEB-INF/footer.vm"))));

		writer.flush();
		writer.close();
	}

	private void sendErrorPage(HttpServletRequest request, HttpServletResponse response, String error) throws Exception {

		Map<String, Object> properties = new HashMap<String, Object> ();
		properties.put("error", error);

		this.sendPage(request, response, properties);
	}

	private XdiContext getContext(String iname, String password) throws Exception {

		return xdi.resolveContextByIname(iname, password);
	}

	private List<XRI3Segment> fetch(XdiContext context) throws Exception {

		Operation operation = context.prepareOperation(MessagingConstants.XRI_GET, new XRI3(context.getCanonical() + "/" + DictionaryConstants.XRI_EXTENSION));
		MessageResult messageResult = context.send(operation);

		Subject subject = messageResult.getGraph().getSubject(context.getCanonical());
		if (subject == null) return new ArrayList<XRI3Segment> ();

		Iterator<XRI3Segment> accountPersonaXris = Dictionary.getSubjectExtensions(subject);
		return new IteratorListMaker<XRI3Segment> (accountPersonaXris).list();
	}

	public ClientAuthenticator getClientAuthenticator() {

		return this.clientAuthenticator;
	}

	public void setClientAuthenticator(ClientAuthenticator clientAuthenticator) {

		this.clientAuthenticator = clientAuthenticator;
	}
}
