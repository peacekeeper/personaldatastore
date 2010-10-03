package pds.endpoint.oauth2.store.impl;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import pds.endpoint.oauth2.store.OAuthStore;

public class EhCacheOAuthStore implements OAuthStore {

	private Cache authorizationCodeCache;
	private Cache accessTokenCache;
	private Cache refreshTokenCache;

	private int defaultAuthorizationCodeTtl;
	private int defaultAccessTokenTtl;
	private int defaultRefreshTokenTtl;

	public EhCacheOAuthStore() {

		CacheManager cacheManager = new CacheManager(EhCacheOAuthStore.class.getResourceAsStream("ehcache.xml"));
		this.authorizationCodeCache = cacheManager.getCache("authorizationCodeCache");
		if (this.authorizationCodeCache == null) throw new NullPointerException("No authorizationCodeCache.");
		this.accessTokenCache = cacheManager.getCache("accessTokenCache");
		if (this.accessTokenCache == null) throw new NullPointerException("No accessTokenCache.");
		this.refreshTokenCache = cacheManager.getCache("refreshTokenCache");
		if (this.refreshTokenCache == null) throw new NullPointerException("No refreshTokenCache.");
	}

	public String[] getScopesForAuthorizationCode(String authorizationCode) {

		Element element = this.authorizationCodeCache.get(authorizationCode);
		if (element == null) return null;

		return (String[]) element.getObjectValue();
	}

	public void setScopesForAuthorizationCode(String authorizationCode, String[] scopes, int authorizationCodeTtl) {

		Element element = new Element(authorizationCode, scopes);
		element.setTimeToLive(authorizationCodeTtl);

		this.authorizationCodeCache.put(element);
	}

	public String[] getScopesForAccessToken(String accessToken) {

		Element element = this.accessTokenCache.get(accessToken);
		if (element == null) return null;

		return (String[]) element.getObjectValue();
	}

	public void setScopesForAccessToken(String accessToken, String[] scopes, int accessTokenTtl) {

		Element element = new Element(accessToken, scopes);
		element.setTimeToLive(accessTokenTtl);

		this.accessTokenCache.put(element);
	}

	public String[] getScopesForRefreshToken(String refreshToken) {

		Element element = this.refreshTokenCache.get(refreshToken);
		if (element == null) return null;

		return (String[]) element.getObjectValue();
	}

	public void setScopesForRefreshToken(String refreshToken, String[] scopes, int refreshTokenTtl) {

		Element element = new Element(refreshToken, scopes);
		element.setTimeToLive(refreshTokenTtl);

		this.accessTokenCache.put(element);
	}

	public int getDefaultAuthorizationCodeTtl() {

		return this.defaultAuthorizationCodeTtl;
	}

	public void setDefaultAuthorizationCodeTtl(int defaultAuthorizationCodeTtl) {

		this.defaultAuthorizationCodeTtl = defaultAuthorizationCodeTtl;
	}

	public int getDefaultAccessTokenTtl() {

		return this.defaultAccessTokenTtl;
	}

	public void setDefaultAccessTokenTtl(int defaultAccessTokenTtl) {

		this.defaultAccessTokenTtl = defaultAccessTokenTtl;
	}

	public int getDefaultRefreshTokenTtl() {

		return this.defaultRefreshTokenTtl;
	}

	public void setDefaultRefreshTokenTtl(int defaultRefreshTokenTtl) {

		this.defaultRefreshTokenTtl = defaultRefreshTokenTtl;
	}
}
