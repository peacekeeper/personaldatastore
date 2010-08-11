package pds.core.xri;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Properties;

import javax.servlet.FilterConfig;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.PdsConnection;
import pds.core.PdsConnectionException;
import pds.core.PdsConnectionFactory;
import pds.store.user.User;
import pds.store.xri.Xri;

public class XriPdsConnectionFactory implements PdsConnectionFactory {

	private Properties properties;
	private pds.store.xri.XriStore xriStore;
	private pds.store.user.Store userStore;
	private X509Certificate brokerCertificate;
	private PrivateKey brokerPrivateKey;
	private KeyPairGenerator keyPairGenerator;
	private X509V3CertificateGenerator certificateGenerator;

	@Override
	public void init(FilterConfig filterConfig) throws PdsConnectionException {

		// load keys, token generator and token verifier

		try {

			Security.addProvider(new BouncyCastleProvider());
			KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");

			InputStream publicBrokerCertificateInputStream = new ByteArrayInputStream(Base64.decodeBase64(this.properties.getProperty("broker-certificate").getBytes("UTF-8"))); 
			this.brokerCertificate = (X509Certificate) certificateFactory.generateCertificate(publicBrokerCertificateInputStream);

			KeySpec publicBrokerPrivateKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(this.properties.getProperty("broker-private-key").getBytes("UTF-8")));
			this.brokerPrivateKey = keyFactory.generatePrivate(publicBrokerPrivateKeySpec);

			this.keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
			this.keyPairGenerator.initialize(2048);

			this.certificateGenerator = new X509V3CertificateGenerator();
		} catch (Exception ex) {

			throw new PdsConnectionException("Cannot initialize keys: " + ex.getMessage(), ex);
		}
	}

	public PdsConnection getPdsConnection(String identifier) throws PdsConnectionException {

		Xri xri = null;
		XRI3Segment inumber = null;
		String userIdentifier = null;
		User user = null;

		// find xri and user

		try {

			xri = this.xriStore.findXri(identifier);
			if (xri != null && xri.getCanonicalID() != null) inumber = new XRI3Segment(xri.getCanonicalID().getValue());
			if (xri != null) userIdentifier = xri.getUserIdentifier();
			if (userIdentifier != null) user = this.userStore.findUser(userIdentifier);
			if (inumber == null) return null;
		} catch (Exception ex) {

			throw new PdsConnectionException("Cannot find xri: " + ex.getMessage(), ex);
		}

		// done

		return new XriPdsConnection(this, xri, user);
	}

	public Properties getProperties() {

		return this.properties;
	}

	public void setProperties(Properties properties) {

		this.properties = properties;
	}

	public pds.store.xri.XriStore getXriStore() {

		return this.xriStore;
	}

	public void setXriStore(pds.store.xri.XriStore xriStore) {

		this.xriStore = xriStore;
	}

	public pds.store.user.Store getUserStore() {

		return this.userStore;
	}

	public void setUserStore(pds.store.user.Store userStore) {

		this.userStore = userStore;
	}

	public X509Certificate getBrokerCertificate() {

		return this.brokerCertificate;
	}

	public void setBrokerCertificate(X509Certificate brokerCertificate) {

		this.brokerCertificate = brokerCertificate;
	}

	public PrivateKey getBrokerPrivateKey() {

		return this.brokerPrivateKey;
	}

	public void setBrokerPrivateKey(PrivateKey brokerPrivateKey) {

		this.brokerPrivateKey = brokerPrivateKey;
	}

	public KeyPairGenerator getKeyPairGenerator() {

		return this.keyPairGenerator;
	}

	public void setKeyPairGenerator(KeyPairGenerator keyPairGenerator) {

		this.keyPairGenerator = keyPairGenerator;
	}

	public X509V3CertificateGenerator getCertificateGenerator() {

		return this.certificateGenerator;
	}

	public void setCertificateGenerator( X509V3CertificateGenerator certificateGenerator) {

		this.certificateGenerator = certificateGenerator;
	}
}
