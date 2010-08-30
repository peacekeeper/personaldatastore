package pds.util.spring.editorsupport;

import java.beans.PropertyEditorSupport;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class PrivateKeyPropertyEditorSupport extends PropertyEditorSupport {

	static {

		Security.addProvider(new BouncyCastleProvider());
	}

	@Override
	public void setAsText(String text) {

		try {

			KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
			KeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(text.getBytes()));
			PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

			this.setValue(privateKey);
		} catch (Exception ex) {

			throw new RuntimeException("Invalid private key: " + ex.getMessage(), ex);
		}
    }
}
