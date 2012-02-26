package pds.web.signup.xri.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Email {

	private MimeMessage message;
	private StringWriter stringWriter;
	private PrintWriter printWriter;

	public Email(String subject, String from, String tos, String server) throws Xdi2MessagingException {

		Properties sessionProperties = new Properties();
		sessionProperties.put("mail.smtp.host", server);

		Session session = Session.getDefaultInstance(sessionProperties, null);
		this.message = new MimeMessage(session);
		this.message.setFrom(new InternetAddress(from, false));
		this.message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(tos, false));
		this.message.setSubject(subject);

		this.stringWriter = new StringWriter();
		this.printWriter = new PrintWriter(this.stringWriter);
	}

	public void println(String line) {

		this.printWriter.println(line);
	}

	public void println() {

		this.printWriter.println();
	}

	public void send() throws Xdi2MessagingException {

		this.message.setSentDate(new Date());

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(this.stringWriter.getBuffer().toString(), "UTF-8");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		this.message.setContent(multipart);
		this.message.saveChanges();
		Transport.send(this.message);
	}
}
