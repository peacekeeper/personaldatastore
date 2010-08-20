package pds.core.xri;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.servlet.FilterConfig;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.PdsConnection;
import pds.core.PdsConnectionFactory;
import pds.core.PdsException;
import pds.store.user.User;
import pds.store.xri.Xri;

public class XriPdsConnectionFactory implements PdsConnectionFactory {

	private String providerId;
	private String[] endpoints;
	private pds.store.xri.XriStore xriStore;
	private pds.store.user.Store userStore;
	private X509Certificate brokerCertificate;
	private PrivateKey brokerPrivateKey;
	private String userCertificateValidity;
	private String userCertificateSignatureAlgorithm;

	static {

		Security.addProvider(new BouncyCastleProvider());
	}

	@Override
	public void init(FilterConfig filterConfig) throws PdsException {

		// check providerId

		if (this.providerId == null) {

			throw new PdsException("Please configure a providerId for pds-core-xri! See http://www.personaldatastore.info/pds-core-xri/ for more information.");
		}

		// check endpoints

		if (this.endpoints == null || this.endpoints.length < 1) {

			this.endpoints = new String[] { filterConfig.getServletContext().getContextPath() };
		}

		// check xriStore

		if (this.xriStore == null) {

			throw new PdsException("Please configure an xriStore for pds-core-xri! See http://www.personaldatastore.info/pds-core-xri/ for more information.");
		}
	}

	public PdsConnection getPdsConnection(String identifier) throws PdsException {

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

			throw new PdsException("Cannot find xri: " + ex.getMessage(), ex);
		}

		// done

		return new XriPdsConnection(identifier, this, xri, user, this.endpoints);
	}

	public String getProviderId() {

		return this.providerId;
	}

	public void setProviderId(String providerId) {

		this.providerId = providerId;
	}

	public String[] getEndpoints() {

		return this.endpoints;
	}

	public void setEndpoints(String[] endpoints) {

		this.endpoints = endpoints;
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

	public void setBrokerCertificate(String brokerCertificate) {

		try {

			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");

			InputStream publicBrokerCertificateInputStream = new ByteArrayInputStream(Base64.decodeBase64(brokerCertificate.getBytes())); 
			this.brokerCertificate = (X509Certificate) certificateFactory.generateCertificate(publicBrokerCertificateInputStream);
		} catch (Exception ex) {

			throw new RuntimeException("Invalid broker certificate: " + ex.getMessage(), ex);
		}
	}

	public PrivateKey getBrokerPrivateKey() {

		return this.brokerPrivateKey;
	}

	public void setBrokerPrivateKey(PrivateKey brokerPrivateKey) {

		this.brokerPrivateKey = brokerPrivateKey;
	}

	public void setBrokerPrivateKey(String brokerPrivateKey) {

		try {

			KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");

			KeySpec publicBrokerPrivateKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(brokerPrivateKey.getBytes()));
			this.brokerPrivateKey = keyFactory.generatePrivate(publicBrokerPrivateKeySpec);
		} catch (Exception ex) {

			throw new RuntimeException("Invalid broker certificate: " + ex.getMessage(), ex);
		}
	}

	public String getUserCertificateValidity() {

		return this.userCertificateValidity;
	}

	public void setUserCertificateValidity(String userCertificateValidity) {

		this.userCertificateValidity = userCertificateValidity;
	}

	public String getUserCertificateSignatureAlgorithm() {
		return this.userCertificateSignatureAlgorithm;
	}

	public void setUserCertificateSignaturealgorithm(String userCertificateSignatureAlgorithm) {

		this.userCertificateSignatureAlgorithm = userCertificateSignatureAlgorithm;
	}
}
