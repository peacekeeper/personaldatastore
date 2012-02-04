package pds.endpoint.oauth2.client;

import org.apache.amber.oauth2.as.request.OAuthRequest;

public interface ClientAuthenticator {

	public boolean isAuthenticated(OAuthRequest oauthRequest);
	public String getRedirectUri(OAuthRequest oauthRequest);
}
