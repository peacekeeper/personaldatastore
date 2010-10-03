package pds.endpoint.oauth2;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.smartam.leeloo.as.issuer.OAuthIssuer;
import net.smartam.leeloo.as.response.OAuthASResponse;
import net.smartam.leeloo.as.response.OAuthASResponse.OAuthAuthorizationResponseBuilder;
import net.smartam.leeloo.common.message.OAuthResponse;
import net.smartam.leeloo.common.message.types.ResponseType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.ServletContextAware;

import pds.endpoint.oauth2.store.OAuthStore;
import pds.xdi.XdiContext;

public class ScopeServlet implements HttpRequestHandler, ServletContextAware {

	private static final Log log = LogFactory.getLog(ScopeServlet.class.getName());

	//	private static final Xdi xdi;

	private OAuthStore oauthStore;
	private OAuthIssuer oauthIssuer;

	/*	static {

		try {

			xdi = new Xdi(new Resolver(null));
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize XDI: " + ex.getMessage(), ex);
		}
	}*/

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

		// authenticated?

		Object authenticated = request.getSession().getAttribute("authenticated");

		if (! Boolean.TRUE.equals(authenticated)) {

			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		// send page

		this.sendPage(request, response, null);
	}

	private void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// selected persona?

		//		String persona = request.getParameter("persona");

		// issue authorization code and/or access token

		String responseType = (String) request.getSession().getAttribute("response_type");
		String redirectUri = (String) request.getSession().getAttribute("redirect_uri");
		String[] scopes = (String[]) request.getSession().getAttribute("scope");

		String authorizationCode = null;
		String accessToken = null;

		int authorizationCodeTtl = 0;
		int accessTokenTtl = 0;

		if (responseType.equals(ResponseType.CODE.toString()) || responseType.equals(ResponseType.CODE_AND_TOKEN.toString())) {

			authorizationCode = this.oauthIssuer.authorizationCode();
			authorizationCodeTtl = this.oauthStore.getDefaultAuthorizationCodeTtl();

			this.oauthStore.setScopesForAuthorizationCode(authorizationCode, scopes, authorizationCodeTtl);
		}

		if (responseType.equals(ResponseType.TOKEN.toString()) || responseType.equals(ResponseType.CODE_AND_TOKEN.toString())) {

			accessToken = this.oauthIssuer.accessToken();
			accessTokenTtl = this.oauthStore.getDefaultAccessTokenTtl();

			this.oauthStore.setScopesForAccessToken(accessToken, scopes, accessTokenTtl);
		}

		// OAuth 2.0 response

		OAuthAuthorizationResponseBuilder oauthAuthorizationResponseBuilder = OAuthASResponse
		.authorizationResponse(HttpServletResponse.SC_FOUND)
		.location(redirectUri);

		if (authorizationCode != null) {

			oauthAuthorizationResponseBuilder.setCode(authorizationCode);
		}

		if (accessToken != null) {

			oauthAuthorizationResponseBuilder.setAccessToken(accessToken);
			oauthAuthorizationResponseBuilder.setExpiresIn(Integer.toString(accessTokenTtl));
		}

		XdiContext context = (XdiContext) request.getSession().getAttribute("context");

		oauthAuthorizationResponseBuilder.setParam("pds_canonical", context.getCanonical().toString());
		oauthAuthorizationResponseBuilder.setParam("pds_endpoint", context.getEndpoint());

		OAuthResponse oauthResponse = oauthAuthorizationResponseBuilder.buildQueryMessage();
		log.info("Redirecting to " + oauthResponse.getLocationUri());

		response.sendRedirect(oauthResponse.getLocationUri());
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
		Velocity.evaluate(velocityContext, writer, "main", new FileReader(new File(this.servletContext.getRealPath("/WEB-INF/scope.vm"))));
		Velocity.evaluate(velocityContext, writer, "footer", new FileReader(new File(this.servletContext.getRealPath("/WEB-INF/footer.vm"))));

		writer.flush();
		writer.close();
	}

	/*	private void sendErrorPage(HttpServletRequest request, HttpServletResponse response, String error) throws Exception {

		Map<String, Object> properties = new HashMap<String, Object> ();
		properties.put("error", error);

		this.sendPage(request, response, properties);
	}*/

	public OAuthStore getOauthStore() {

		return this.oauthStore;
	}

	public void setOauthStore(OAuthStore oauthStore) {

		this.oauthStore = oauthStore;
	}

	public OAuthIssuer getOauthIssuer() {

		return this.oauthIssuer;
	}

	public void setOauthIssuer(OAuthIssuer oauthIssuer) {

		this.oauthIssuer = oauthIssuer;
	}
}
