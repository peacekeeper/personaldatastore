package pds.endpoint.oauth2.util;

import org.apache.commons.codec.binary.Base64;

public class HttpAuthorizationUtil {

	private HttpAuthorizationUtil() {

	}

	public static String[] fromAuthorizationHeader(String header) {

		if (header == null) return null;

		String[] authorization = header.split(" ");
		if (authorization == null || authorization.length != 2) return null;
		if (! authorization[0].equals("Basic")) return null;
		
		authorization = new String(Base64.decodeBase64(authorization[1])).split(":");
		if (authorization.length != 2) return null;

		return authorization;
	}
}
