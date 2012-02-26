package pds.core.xri.messagingtargets;


import pds.core.xri.XriPdsInstance;
import pds.store.user.User;
import pds.store.xri.Xri;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.messaging.MessageResult;
import xdi2.messaging.target.impl.AbstractResourceHandler;

public class XriSubjectResourceHandler extends AbstractResourceHandler {

	private XriPdsInstance pdsInstance;

	public XriSubjectResourceHandler(Message message, Subject subject, XriPdsInstance pdsInstance) {

		super(message, subject);

		this.pdsInstance = pdsInstance;
	}

	@Override
	public boolean executeGet(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		Xri xri = this.pdsInstance.getXri();
		User user = this.pdsInstance.getUser();

		// private/public key, password

		try {

			String password = user != null ? user.getPass() : null;
			String publicKey = xri != null ? xri.getAuthorityAttribute("publickey") : null;
			String privateKey = xri != null ? xri.getAuthorityAttribute("privatekey") : null;
			String certificate = xri != null ? xri.getAuthorityAttribute("certificate") : null;

			if (password != null) messageResult.getGraph().createStatement(this.operationContextNode.getSubjectXri(), new XRI3Segment("$password"), password);
			if (publicKey != null) messageResult.getGraph().createStatement(this.operationContextNode.getSubjectXri(), new XRI3Segment("$key$public"), publicKey);
			if (privateKey != null) messageResult.getGraph().createStatement(this.operationContextNode.getSubjectXri(), new XRI3Segment("$key$private"), privateKey);
			if (certificate != null) messageResult.getGraph().createStatement(this.operationContextNode.getSubjectXri(), new XRI3Segment("$certificate$x.509"), certificate);
		} catch (Exception ex) {

			throw new MessagingException("Cannot get private/public key from PDS connection: " + ex.getMessage(), ex);
		}

		// done

		return true;
	}
}
