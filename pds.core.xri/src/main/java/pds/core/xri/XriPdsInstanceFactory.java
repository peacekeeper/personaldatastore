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
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;

import pds.core.base.PdsException;
import pds.core.base.PdsInstance;
import pds.core.base.PdsInstanceFactory;
import pds.store.user.User;
import pds.store.xri.Xri;

public class XriPdsInstanceFactory implements PdsInstanceFactory {

	private pds.store.xri.XriStore xriStore;
	private pds.store.user.Store userStore;
	private String[] endpoints;
	private X509Certificate brokerCertificate;
	private PrivateKey brokerPrivateKey;
	private String userCertificateValidity;
	private String userCertificateSignatureAlgorithm;

	static {

		Security.addProvider(new BouncyCastleProvider());
	}

	@Override
	public void init(FilterConfig filterConfig) throws PdsException {

		// check xriStore

		if (this.xriStore == null) {

			throw new PdsException("Please configure an xriStore for pds-core-xri! See http://www.personaldatastore.info/pds-core-xri/ for more information.");
		}

		// check endpoints

		if (this.endpoints == null || this.endpoints.length < 1) {

			this.endpoints = new String[] { filterConfig.getServletContext().getContextPath() };
		}
	}

	public String getTarget(String path) {

		try {

			String target = path;
			while (target.endsWith("/")) target = target.substring(0, target.length() - 1);
			target = new XRI3(target).getAuthority().toString() + "/";

			return target;
		} catch (Exception ex) {

			return null;
		}
	}

	public PdsInstance getPdsInstance(String target) throws PdsException {

		String xriString = target.substring(0, target.length() - 1);
		Xri xri = null;
		String userIdentifier = null;
		User user = null;

		// find xri and user

		try {

			xri = this.xriStore.findXri(xriString);

			if (xri != null) userIdentifier = xri.getUserIdentifier();
			if (userIdentifier != null) user = this.userStore.findUser(userIdentifier);
		} catch (Exception ex) {

			throw new PdsException("Cannot look up xri: " + ex.getMessage(), ex);
		}

		if (xri == null) return null;

		// instantiate a PDS instance for the xri

		return new XriPdsInstance(target, this, xri, user, this.endpoints);
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

	public void setUserCertificateSignatureAlgorithm(String userCertificateSignatureAlgorithm) {

		this.userCertificateSignatureAlgorithm = userCertificateSignatureAlgorithm;
	}
}
