package pds.util.spring.editorsupport;

import java.beans.PropertyEditorSupport;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class X509CertificatePropertyEditorSupport extends PropertyEditorSupport {

	static {

		Security.addProvider(new BouncyCastleProvider());
	}

	@Override
	public void setAsText(String text) {

		try {

			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
			InputStream certificateInputStream = new ByteArrayInputStream(Base64.decodeBase64(text.getBytes()));
			X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(certificateInputStream);

			this.setValue(x509Certificate);
		} catch (Exception ex) {

			throw new RuntimeException("Invalid X.509 certificate: " + ex.getMessage(), ex);
		}
    }
}
