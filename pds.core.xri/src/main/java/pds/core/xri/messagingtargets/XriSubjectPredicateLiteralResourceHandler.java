package pds.core.xri.messagingtargets;


import pds.core.xri.XriPdsInstance;
import pds.store.user.StoreException;
import pds.store.user.StoreUtil;
import pds.store.user.User;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.messaging.MessageResult;
import xdi2.messaging.target.impl.AbstractResourceHandler;

public class XriSubjectPredicateLiteralResourceHandler extends AbstractResourceHandler {

	private XriPdsInstance pdsInstance;

	public XriSubjectPredicateLiteralResourceHandler(Message message, Subject operationSubject, Predicate operationPredicate, Literal operationLiteral, XriPdsInstance pdsInstance) {

		super(message, operationSubject, operationPredicate, operationLiteral);

		this.pdsInstance = pdsInstance;
	}

	@Override
	public boolean executeMod(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		User user = this.pdsInstance.getUser();
		pds.store.user.Store userStore = this.pdsInstance.getPdsInstanceFactory().getUserStore();

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
