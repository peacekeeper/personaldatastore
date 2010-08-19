package pds.core.xri.messagingtargets;


import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractResourceHandler;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.xri.XriPdsConnection;
import pds.store.user.User;
import pds.store.xri.Xri;

public class XriSubjectResourceHandler extends AbstractResourceHandler {

	private XriPdsConnection pdsConnection;

	public XriSubjectResourceHandler(Message message, Subject subject, XriPdsConnection pdsConnection) {

		super(message, subject);

		this.pdsConnection = pdsConnection;
	}

	@Override
	public boolean executeGet(Operation operation, MessageResult messageResult, Object executionContext) throws MessagingException {

		Xri xri = this.pdsConnection.getXri();
		User user = this.pdsConnection.getUser();
		
		// private/public key, password

		try {

			String password = user != null ? user.getPass() : null;
			String publicKey = xri != null ? xri.getAuthorityAttribute("publickey") : null;
			String privateKey = xri != null ? xri.getAuthorityAttribute("privatekey") : null;
			String certificate = xri != null ? xri.getAuthorityAttribute("certificate") : null;

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