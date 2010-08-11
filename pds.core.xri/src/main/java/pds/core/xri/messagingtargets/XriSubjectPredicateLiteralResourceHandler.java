package pds.core.xri.messagingtargets;


import org.eclipse.higgins.xdi4j.Literal;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractResourceHandler;

import pds.core.PdsConnection;

public class XriSubjectPredicateLiteralResourceHandler extends AbstractResourceHandler {

	private PdsConnection pdsConnection;

	public XriSubjectPredicateLiteralResourceHandler(Message message, Subject operationSubject, Predicate operationPredicate, Literal operationLiteral, PdsConnection pdsConnection) {

		super(message, operationSubject, operationPredicate, operationLiteral);

		this.pdsConnection = pdsConnection;
	}

	@Override
	public boolean executeMod(Operation operation, MessageResult messageResult, Object executionContext) throws MessagingException {

/*		// read information from the message

		String newPassword = this.operationLiteral.getData();

		// operation authenticated?

		boolean isSelfAuthenticated; 

		try {

			isSelfAuthenticated = this.pdsConnection.isSelfAuthenticated(operation);
		} catch (PdsConnectionException ex) {

			throw new MessagingException("Cannot check authentication: " + ex.getMessage(), ex);
		}

		// authenticated?

		if (! isSelfAuthenticated) {

			throw new MessagingException("Not authenticated!");
		}

		// with the correct password we can modify the password

		try {

			this.pdsConnection.setNewPassword(newPassword);
		} catch (PdsConnectionException ex) {

			throw new MessagingException("Cannot set new password: " + ex.getMessage(), ex);
		}*/

		return true;
	}
}
