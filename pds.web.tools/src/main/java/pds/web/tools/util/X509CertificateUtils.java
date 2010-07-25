package pds.web.tools.util;

import java.io.ByteArrayInputStream;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class X509CertificateUtils {

	static {

		org.apache.xml.security.Init.init();
		Security.addProvider(new BouncyCastleProvider());
	}

	public static X509Certificate certFromPemData(String pemData) throws Exception {

		byte[] derData = Base64.decodeBase64(pemData.getBytes("UTF-8"));

		CertificateFactory certificateFactory = CertificateFactory.getInstance("X509", "BC");
		ByteArrayInputStream stream = new ByteArrayInputStream(derData);
		return (X509Certificate) certificateFactory.generateCertificate(stream);
	}

	public static String cnForCertificate(X509Certificate certificate) {

		String attrString = certificate.getSubjectDN().getName();
		String[] pieces = attrString.split(",\\s+");

		for (String piece : pieces) {

			String[] otherPieces = piece.split("=");

			if (otherPieces[0].equalsIgnoreCase("CN")) return otherPieces[1];
		}

		throw new RuntimeException("Couldn't find CN in: " + attrString);
	}

	public static String commaSeparatedBytesFromPemData(String pemData) throws Exception {

		byte[] derData = Base64.decodeBase64(pemData.getBytes("UTF-8"));

		StringBuffer buffer = new StringBuffer();

		for (int i=0; i<derData.length; i++) {

			int b = derData[i] & 0xFF;
			buffer.append(Integer.toString(b));
			if (i + 1 < derData.length) buffer.append(",");
		}

		return buffer.toString();
	}
}
