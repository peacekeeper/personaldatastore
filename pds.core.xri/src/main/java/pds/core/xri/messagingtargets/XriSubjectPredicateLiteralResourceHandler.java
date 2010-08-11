package pds.core.xri.messagingtargets;


import org.eclipse.higgins.xdi4j.Literal;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractResourceHandler;

import pds.core.xri.XriPdsConnection;
import pds.store.user.StoreException;
import pds.store.user.StoreUtil;
import pds.store.user.User;

public class XriSubjectPredicateLiteralResourceHandler extends AbstractResourceHandler {

	private XriPdsConnection pdsConnection;

	public XriSubjectPredicateLiteralResourceHandler(Message message, Subject operationSubject, Predicate operationPredicate, Literal operationLiteral, XriPdsConnection pdsConnection) {

		super(message, operationSubject, operationPredicate, operationLiteral);

		this.pdsConnection = pdsConnection;
	}

	@Override
	public boolean executeMod(Operation operation, MessageResult messageResult, Object executionContext) throws MessagingException {

		User user = this.pdsConnection.getUser();
		pds.store.user.Store userStore = this.pdsConnection.getPdsConnectionFactory().getUserStore();

		// read information from the message

		String newPass = this.operationLiteral.getData();

		// with the correct password we can modify the password

		try {

			user.setPass(StoreUtil.hashPass(newPass));
			userStore.updateObject(user);
		} catch (StoreException ex) {

			throw new MessagingException("Cannot set new password: " + ex.getMessage(), ex);
		}

		return true;
	}
}
