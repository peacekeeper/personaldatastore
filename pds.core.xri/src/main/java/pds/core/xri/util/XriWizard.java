package pds.core.xri.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.openxri.xml.CertificateService;
import org.openxri.xml.Service;
import org.openxri.xml.XDIService;

import pds.core.xri.XriPdsConnectionFactory;
import pds.store.xri.Xri;

public class XriWizard {

	private XriWizard() { }

	public static void configure(XriPdsConnectionFactory pdsConnectionFactory, Xri xri) throws Exception {

		Properties properties = pdsConnectionFactory.getProperties();

		xri.deleteAllServices();
		List<Service> services = new ArrayList<Service> ();

		// set up XDI SEP

		services.add(
				new XDIService(
						new URI(properties.getProperty("xdi-service") + xri.getCanonicalID().getValue()),
						properties.getProperty("providerid")));

		// set up keys/certificate and SEP 

		X509Certificate userCertificate;

		if (! xri.hasAuthorityAttribute("publickey") ||
				! xri.hasAuthorityAttribute("privatekey") ||
				! xri.hasAuthorityAttribute("certificate")) {

			X509Certificate brokerCertificate = pdsConnectionFactory.getBrokerCertificate();
			PrivateKey brokerPrivateKey = pdsConnectionFactory.getBrokerPrivateKey();
			KeyPairGenerator keyPairGenerator = pdsConnectionFactory.getKeyPairGenerator();
			X509V3CertificateGenerator certificateGenerator = pdsConnectionFactory.getCertificateGenerator();

			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			PublicKey userPublicKey = keyPair.getPublic();
			PrivateKey userPrivateKey = keyPair.getPrivate();
			Date userCertificateDate = new Date();

			certificateGenerator.setPublicKey(userPublicKey);
			certificateGenerator.setSubjectDN(new X509Name("cn=" + xri.getCanonicalID().getValue()));
			certificateGenerator.setIssuerDN(brokerCertificate.getIssuerX500Principal());
			certificateGenerator.setNotBefore(userCertificateDate);
			certificateGenerator.setNotAfter(new Date(userCertificateDate.getTime() + Long.parseLong(properties.getProperty("user-certificate-validity"))));
			certificateGenerator.setSerialNumber(BigInteger.valueOf(userCertificateDate.getTime()));
			certificateGenerator.setSignatureAlgorithm(properties.getProperty("user-certificate-signaturealgorithm"));

			userCertificate = certificateGenerator.generate(brokerPrivateKey);

			xri.setAuthorityAttribute("publickey", new String(Base64.encodeBase64(userPublicKey.getEncoded()), "UTF-8"));
			xri.setAuthorityAttribute("privatekey", new String(Base64.encodeBase64(userPrivateKey.getEncoded()), "UTF-8"));
			xri.setAuthorityAttribute("certificate", new String(Base64.encodeBase64(userCertificate.getEncoded()), "UTF-8"));
		} else {

			String userCertificateStr = xri.getAuthorityAttribute("certificate");

			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
			InputStream userCertificateInputStream = new ByteArrayInputStream(Base64.decodeBase64(userCertificateStr.getBytes("UTF-8"))); 
			userCertificate = (X509Certificate) certificateFactory.generateCertificate(userCertificateInputStream);
		}

		services.add(
				new CertificateService(
						userCertificate));

		// add service endpoints

		xri.addServices(services.toArray(new Service[services.size()]));
	}
}
