package pds.endpoint.oauth2.client.impl;

import java.util.Map;

import net.smartam.leeloo.as.request.OAuthRequest;
import pds.endpoint.oauth2.client.ClientAuthenticator;

public class StaticClientPasswordAuthenticator implements ClientAuthenticator {

	private Map<?, ?> clients;

	@Override
	public boolean isAuthenticated(OAuthRequest oauthRequest) {
		
		String clientId = oauthRequest.getClientId();
		String clientSecret = oauthRequest.getClientSecret();
		
		return this.clients.get(clientId).equals(clientSecret);
	}

	@Override
	public String getRedirectUri(OAuthRequest oauthRequest) {

		return oauthRequest.getRedirectURI();
	}

	public Map<?, ?> getClients() {

		return this.clients;
	}

	public void setClients(Map<?, ?> clients) {

		this.clients = clients;
	}
}
