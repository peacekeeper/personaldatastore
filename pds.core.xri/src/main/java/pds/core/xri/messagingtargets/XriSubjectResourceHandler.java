package pds.core.xri.messagingtargets;


import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractResourceHandler;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.xri.XriPdsConnection;

public class XriSubjectResourceHandler extends AbstractResourceHandler {

	private XriPdsConnection pdsConnection;

	public XriSubjectResourceHandler(Message message, Subject subject, XriPdsConnection pdsConnection) {

		super(message, subject);

		this.pdsConnection = pdsConnection;
	}

	@Override
	public boolean executeGet(Operation operation, MessageResult messageResult, Object executionContext) throws MessagingException {

		// private/public key, password

		try {

			String password = this.pdsConnection.getUser() != null ? this.pdsConnection.getUser().getPass() : null;
			String publicKey = this.pdsConnection.getXri() != null ? this.pdsConnection.getXri().getAuthorityAttribute("publickey") : null;
			String privateKey = this.pdsConnection.getXri() != null ? this.pdsConnection.getXri().getAuthorityAttribute("privatekey") : null;
			String certificate = this.pdsConnection.getXri() != null ? this.pdsConnection.getXri().getAuthorityAttribute("certificate") : null;

			if (password != null) messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), new XRI3Segment("$password"), password);
			if (publicKey != null) messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), new XRI3Segment("$key$public"), publicKey);
			if (privateKey != null) messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), new XRI3Segment("$key$private"), privateKey);
			if (certificate != null) messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), new XRI3Segment("$certificate$x.509"), certificate);
		} catch (Exception ex) {

			throw new MessagingException("Cannot get private/public key from PDS connection: " + ex.getMessage(), ex);
		}

		// done

		return true;
	}
}
