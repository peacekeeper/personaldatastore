package pds.endpoint.oauth2.client.impl;

import javax.servlet.http.HttpServletRequest;

import pds.endpoint.oauth2.client.ClientAuthenticator;
import pds.endpoint.oauth2.util.HttpAuthorizationUtil;

public abstract class AbstractClientAuthenticator implements ClientAuthenticator {

	public final boolean isAuthenticated(HttpServletRequest request) {

		String clientId = null;
		String clientPassword = null;

		String[] authorization = HttpAuthorizationUtil.fromAuthorizationHeader(request.getHeader("Authorization"));
		if (authorization != null && authorization.length == 2) {

			clientId = authorization[0];
			clientPassword = authorization[1];
		}

		if (clientId == null) clientId = request.getParameter("client_id");
		if (clientPassword == null) clientPassword = request.getParameter("client_password");

		return this.isAuthenticated(clientId, clientPassword);
	}

	public abstract boolean isAuthenticated(String clientId, String clientPassword);
}
