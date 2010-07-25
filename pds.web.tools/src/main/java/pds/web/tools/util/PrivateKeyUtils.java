package pds.web.tools.util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class PrivateKeyUtils {

	static {

		org.apache.xml.security.Init.init();
		Security.addProvider(new BouncyCastleProvider());
	}

	public static PrivateKey privateKeyFromPemData(String pemData) throws Exception {

		byte[] derData = Base64.decodeBase64(pemData.getBytes("UTF-8"));

		KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
		KeySpec privateKeySpec = new PKCS8EncodedKeySpec(derData);
		return keyFactory.generatePrivate(privateKeySpec);
	}
}
