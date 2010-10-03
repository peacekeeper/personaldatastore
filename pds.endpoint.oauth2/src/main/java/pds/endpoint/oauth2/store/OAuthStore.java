package pds.endpoint.oauth2.store;


public interface OAuthStore {

	public int getDefaultAuthorizationCodeTtl();
	public String[] getScopesForAuthorizationCode(String authorizationCode);
	public void setScopesForAuthorizationCode(String authorizationCode, String[] scopes, int ttl);

	public int getDefaultAccessTokenTtl();
	public String[] getScopesForAccessToken(String accessToken);
	public void setScopesForAccessToken(String accessToken, String[] scopes, int ttl);

	public int getDefaultRefreshTokenTtl();
	public String[] getScopesForRefreshToken(String accessToken);
	public void setScopesForRefreshToken(String accessToken, String[] scopes, int ttl);
}
