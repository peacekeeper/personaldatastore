package pds.core.messagingtargets.pds;


import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.constants.DictionaryConstants;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractResourceHandler;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.PdsConnection;
import pds.core.PdsConnectionException;

public class PdsSubjectResourceHandler extends AbstractResourceHandler {

	private PdsConnection pdsConnection;

	public PdsSubjectResourceHandler(Message message, Subject subject, PdsConnection pdsConnection) {

		super(message, subject);

		this.pdsConnection = pdsConnection;
	}

	@Override
	public boolean executeGet(Operation operation, MessageResult messageResult, Object executionContext) throws MessagingException {

		// operation authenticated?

		boolean isSelfAuthenticated; 

		try {

			isSelfAuthenticated = this.pdsConnection.isSelfAuthenticated(operation);
		} catch (PdsConnectionException ex) {

			throw new MessagingException("Cannot check authentication: " + ex.getMessage(), ex);
		}

		// anyone can check if a PDS exists or not

		messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), DictionaryConstants.XRI_INHERITANCE, new XRI3Segment(this.operationSubject.getSubjectXri().getFirstSubSegment().getGCS().toString()));

		String[] aliases = this.pdsConnection.getAliases();
		for (String alias : aliases) {

			messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), DictionaryConstants.XRI_EQUIVALENCE, new XRI3Segment(alias));
		}

		if (this.pdsConnection.getCanonical() != null) {

			messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), new XRI3Segment("$$is"), this.pdsConnection.getCanonical());
		}

		// if authenticated you can check anything on yourself

		if (isSelfAuthenticated) {

			try {

				String password = this.pdsConnection.getPassword();
				String publicKey = this.pdsConnection.getPublicKey();
				String privateKey = this.pdsConnection.getPrivateKey();
				String certificate = this.pdsConnection.getCertificate();

				if (password != null) messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), new XRI3Segment("$password"), password);

				if (publicKey != null) messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), new XRI3Segment("$key$public"), publicKey);
				if (privateKey != null) messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), new XRI3Segment("$key$private"), privateKey);
				if (certificate != null) messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), new XRI3Segment("$certificate$x.509"), certificate);
			} catch (PdsConnectionException ex) {

				throw new MessagingException("Cannot get XRI attributes from store: " + ex.getMessage(), ex);
			}
		}

		return true;
	}
}
