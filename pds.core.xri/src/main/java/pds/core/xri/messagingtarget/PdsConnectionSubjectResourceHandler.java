package pds.core.xri.messagingtarget;


import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractResourceHandler;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.PdsConnectionException;
import pds.core.xri.XriPdsConnection;

public class PdsConnectionSubjectResourceHandler extends AbstractResourceHandler {

	private XriPdsConnection pdsConnection;

	public PdsConnectionSubjectResourceHandler(Message message, Subject subject, XriPdsConnection pdsConnection) {

		super(message, subject);

		this.pdsConnection = pdsConnection;
	}

	@Override
	public boolean executeGet(Operation operation, MessageResult messageResult, Object executionContext) throws MessagingException {

		// password

		try {

			String password = this.pdsConnection.getu
			String privateKey = this.pdsConnection.getPrivateKey();
			String certificate = this.pdsConnection.getCertificate();

			if (publicKey != null) messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), new XRI3Segment("$key$public"), publicKey);
			if (privateKey != null) messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), new XRI3Segment("$key$private"), privateKey);
			if (certificate != null) messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), new XRI3Segment("$certificate$x.509"), certificate);
		} catch (PdsConnectionException ex) {

			throw new MessagingException("Cannot get private/public key from PDS connection: " + ex.getMessage(), ex);
		}

		// done

		return true;
	}
}
