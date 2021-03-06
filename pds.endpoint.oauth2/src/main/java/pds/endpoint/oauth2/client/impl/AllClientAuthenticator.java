package pds.endpoint.oauth2.client.impl;

import org.apache.amber.oauth2.as.request.OAuthRequest;

import pds.endpoint.oauth2.client.ClientAuthenticator;

public class AllClientAuthenticator implements ClientAuthenticator {

	@Override
	public boolean isAuthenticated(OAuthRequest oauthRequest) {

		return true;
	}

	@Override
	public String getRedirectUri(OAuthRequest oauthRequest) {

		return oauthRequest.getRedirectURI();
	}
}
