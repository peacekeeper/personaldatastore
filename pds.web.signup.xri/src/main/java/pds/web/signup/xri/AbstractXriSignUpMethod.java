package pds.web.signup.xri;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringWriter;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import pds.web.signin.xri.XriSignUpMethod;
import pds.web.signup.xri.util.Email;

public abstract class AbstractXriSignUpMethod implements XriSignUpMethod {

	private static final Log log = LogFactory.getLog(AbstractXriSignUpMethod.class.getName());

	private pds.store.xri.XriStore xriStore;
	private pds.store.user.Store userStore;
	private String[] endpoints;
	private X509Certificate brokerCertificate;
	private PrivateKey brokerPrivateKey;
	private String userCertificateValidity;
	private String userCertificateSignatureAlgorithm;
	private String emailRegisterTemplate;
	private Properties emailProperties;

	public void sendEmail(String iname, String to) {

		// send e-mail

		try {

			String subject = "I-Name registration successful: " + iname;
			String from = this.emailProperties.getProperty("email-from");
			String server = this.emailProperties.getProperty("email-server");

			StringWriter writer = new StringWriter();
			StringBuffer buffer;

			VelocityContext context = new VelocityContext(this.emailProperties);
			context.put("iname", iname);

			Reader templateReader = new FileReader(new File(this.emailRegisterTemplate));

			Velocity.evaluate(context, writer, null, templateReader);
			templateReader.close();
			buffer = writer.getBuffer();

			Email email = new Email(subject, from, to, server);
			email.println(buffer.toString());
			email.send();
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
		}			
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

	public String[] getEndpoints() {

		return this.endpoints;
	}

	public void setEndpoints(String[] endpoints) {

		this.endpoints = endpoints;
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

	public String getEmailRegisterTemplate() {

		return this.emailRegisterTemplate;
	}

	public void setEmailRegisterTemplate(String emailRegisterTemplate) {

		this.emailRegisterTemplate = emailRegisterTemplate;
	}

	public Properties getEmailProperties() {

		return this.emailProperties;
	}

	public void setEmailProperties(Properties emailProperties) {

		this.emailProperties = emailProperties;
	}
}
