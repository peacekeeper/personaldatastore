package pds.endpoint.oauth2;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.smartam.leeloo.as.issuer.OAuthIssuer;
import net.smartam.leeloo.as.request.OAuthTokenRequest;
import net.smartam.leeloo.as.response.OAuthASResponse;
import net.smartam.leeloo.common.exception.OAuthProblemException;
import net.smartam.leeloo.common.message.OAuthResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.HttpRequestHandler;

import pds.endpoint.oauth2.client.ClientAuthenticator;
import pds.endpoint.oauth2.store.OAuthStore;

public class TokenServlet implements HttpRequestHandler {

	private static final Log log = LogFactory.getLog(TokenServlet.class.getName());

	private ClientAuthenticator clientAuthenticator;
	private OAuthStore oauthStore;
	private OAuthIssuer oauthIssuer;

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		log.trace(request.getMethod() + ": " + request.getRequestURI() + ", Content-Type: " + request.getContentType() + ", Content-Length: " + request.getContentLength());

		try {

			if ("POST".equals(request.getMethod())) this.doPost(request, response);
			else response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	private void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {

		try {

			OAuthTokenRequest oauthRequest = new OAuthTokenRequest(request);

			String inAuthorizationCode = oauthRequest.getCode();
			String inRefreshToken = oauthRequest.getRefreshToken();

			// issue access token (and maybe refresh token)

			OAuthResponse oauthResponse;
			String[] scopes;

			if (inAuthorizationCode != null && (scopes = this.oauthStore.getScopesForAuthorizationCode(inAuthorizationCode)) != null) {

				String accessToken = this.oauthIssuer.accessToken();
				String refreshToken = this.oauthIssuer.refreshToken();

				int accessTokenTtl = this.oauthStore.getDefaultAccessTokenTtl();
				int refreshTokenTtl = this.oauthStore.getDefaultRefreshTokenTtl();

				this.oauthStore.setScopesForAccessToken(accessToken, scopes, accessTokenTtl);
				this.oauthStore.setScopesForRefreshToken(refreshToken, scopes, refreshTokenTtl);

				oauthResponse = OAuthASResponse
				.tokenResponse(HttpServletResponse.SC_OK)
				.setAccessToken(accessToken)
				.setRefreshToken(refreshToken)
				.setExpiresIn(Integer.toString(accessTokenTtl))
				.buildJSONMessage();
			} else if (inRefreshToken != null && (scopes = this.oauthStore.getScopesForRefreshToken(inRefreshToken)) != null) {

				String accessToken = this.oauthIssuer.accessToken();

				int accessTokenTtl = this.oauthStore.getDefaultAccessTokenTtl();

				this.oauthStore.setScopesForAccessToken(accessToken, scopes, accessTokenTtl);

				oauthResponse = OAuthASResponse
				.tokenResponse(HttpServletResponse.SC_OK)
				.setAccessToken(accessToken)
				.setExpiresIn(Integer.toString(accessTokenTtl))
				.buildJSONMessage();
			} else {

				throw OAuthProblemException.error("Invalid credentials.");
			}

			// OAuth 2.0 response

			response.setStatus(oauthResponse.getResponseStatus());

			PrintWriter writer = response.getWriter();
			writer.print(oauthResponse.getBody());
			writer.flush();
			writer.close();
		} catch (OAuthProblemException ex) {

			log.error("OAuth problem: " + ex.getMessage(), ex);

			OAuthResponse oauthResponse = OAuthResponse
			.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
			.error(ex)
			.buildJSONMessage();

			response.setStatus(oauthResponse.getResponseStatus());

			PrintWriter writer = response.getWriter();
			writer.print(oauthResponse.getBody());
			writer.flush();
			writer.close();
		}
	}

	public ClientAuthenticator getClientAuthenticator() {

		return this.clientAuthenticator;
	}

	public void setClientAuthenticator(ClientAuthenticator clientAuthenticator) {

		this.clientAuthenticator = clientAuthenticator;
	}

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
