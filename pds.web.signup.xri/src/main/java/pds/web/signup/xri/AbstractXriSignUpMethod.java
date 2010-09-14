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

import pds.web.PDSApplication;
import pds.web.signin.xri.XriSignUpMethod;
import pds.web.signup.xri.util.Email;

public abstract class AbstractXriSignUpMethod implements XriSignUpMethod {

	private static final Log log = LogFactory.getLog(AbstractXriSignUpMethod.class.getName());

	private pds.store.xri.XriStore xriStore;
	private pds.store.user.Store userStore;
	private String[] endpoints;
	private String feedEndpoint;
	private String hcardEndpoint;
	private String pocoEndpoint;
	private String salmonEndpoint;
	private String foafEndpoint;
	private X509Certificate brokerCertificate;
	private PrivateKey brokerPrivateKey;
	private String userCertificateValidity;
	private String userCertificateSignatureAlgorithm;
	private String emailRegisterTemplate;
	private Properties emailProperties;

	public void sendEmail(PDSApplication pdsApplication, String iname, String to) {

		// send e-mail

		try {

			String subject = "I-Name registration successful: " + iname;
			String from = this.emailProperties.getProperty("email-from");
			String server = this.emailProperties.getProperty("email-server");

			StringWriter writer = new StringWriter();
			StringBuffer buffer;

			VelocityContext context = new VelocityContext(this.emailProperties);
			context.put("iname", iname);

			Reader templateReader = new FileReader(new File(pdsApplication.getServlet().getServletContext().getRealPath(this.emailRegisterTemplate)));

			Velocity.evaluate(context, writer, this.emailRegisterTemplate, templateReader);
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

	public String getFeedEndpoint() {

		return this.feedEndpoint;
	}

	public void setFeedEndpoint(String feedEndpoint) {

		this.feedEndpoint = feedEndpoint;
		if (! this.feedEndpoint.endsWith("/")) this.feedEndpoint += "/";
	}

	public String getHcardEndpoint() {

		return this.hcardEndpoint;
	}

	public void setHcardEndpoint(String hcardEndpoint) {

		this.hcardEndpoint = hcardEndpoint;
		if (! this.hcardEndpoint.endsWith("/")) this.hcardEndpoint += "/";
	}

	public String getPocoEndpoint() {

		return this.pocoEndpoint;
	}

	public void setPocoEndpoint(String pocoEndpoint) {

		this.pocoEndpoint = pocoEndpoint;
		if (! this.pocoEndpoint.endsWith("/")) this.pocoEndpoint += "/";
	}

	public String getSalmonEndpoint() {

		return this.salmonEndpoint;
	}

	public void setSalmonEndpoint(String salmonEndpoint) {

		this.salmonEndpoint = salmonEndpoint;
		if (! this.salmonEndpoint.endsWith("/")) this.salmonEndpoint += "/";
	}

	public String getFoafEndpoint() {

		return this.foafEndpoint;
	}

	public void setFoafEndpoint(String foafEndpoint) {

		this.foafEndpoint = foafEndpoint;
		if (! this.foafEndpoint.endsWith("/")) this.foafEndpoint += "/";
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
