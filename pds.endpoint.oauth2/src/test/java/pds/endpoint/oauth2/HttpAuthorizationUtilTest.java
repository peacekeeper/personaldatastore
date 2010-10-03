package pds.endpoint.oauth2;

import pds.endpoint.oauth2.util.HttpAuthorizationUtil;
import junit.framework.TestCase;

public class HttpAuthorizationUtilTest extends TestCase {

	public void testFromAuthorizationHeader() {

		String header = "Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW";
		String[] authorization = HttpAuthorizationUtil.fromAuthorizationHeader(header);
		assertNotNull(authorization);
		assertTrue(authorization.length == 2);
		assertEquals(authorization[0], "s6BhdRkqt3");
		assertEquals(authorization[1], "gX1fBat3bV");
	}
}
