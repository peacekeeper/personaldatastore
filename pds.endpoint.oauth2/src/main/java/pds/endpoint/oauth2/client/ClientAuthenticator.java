package pds.endpoint.oauth2.client;

import net.smartam.leeloo.as.request.OAuthRequest;

public interface ClientAuthenticator {

	public boolean isAuthenticated(OAuthRequest oauthRequest);
	public String getRedirectUri(OAuthRequest oauthRequest);
}
