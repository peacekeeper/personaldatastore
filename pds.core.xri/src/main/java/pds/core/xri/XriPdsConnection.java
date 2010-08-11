package pds.core.xri;

import java.util.List;

import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractMessagingTarget;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.openxri.xml.CanonicalID;

import pds.core.PdsConnection;
import pds.core.xri.messagingtargets.PdsConnectionResourceMessagingTarget;
import pds.store.user.User;
import pds.store.xri.Xri;
import pds.store.xri.XriStoreException;

public class XriPdsConnection implements PdsConnection {

	private XriPdsConnectionFactory pdsConnectionFactory;
	private Xri xri;
	private User user;

	XriPdsConnection(XriPdsConnectionFactory pdsConnectionFactory, Xri xri, User user) {

		this.pdsConnectionFactory = pdsConnectionFactory;
		this.xri = xri;
		this.user = user;
	}

	@Override
	public XRI3Segment getCanonical() {

		CanonicalID canonicalID;

		try {

			canonicalID = this.xri.getCanonicalID();
		} catch (XriStoreException ex) {

			throw new RuntimeException(ex);
		}

		if (canonicalID == null) return null;

		String canonicalIDValue = canonicalID.getValue();
		if (canonicalIDValue == null) return null;

		return new XRI3Segment(canonicalIDValue);
	}

	public String[] getAliases() {

		List<String> aliases = this.xri.getAliases();

		return aliases.toArray(new String[aliases.size()]);
	}

	public String[] getEndpoints() {

		String xdiService = this.pdsConnectionFactory.getProperties().getProperty("xdi-service");

		return new String[] { xdiService };
	}

	/*	public boolean isSelfAuthenticated(Operation operation) throws PdsConnectionException {

		// read information from the operation

		String senderXriString = operation.getSenderXri().toString();
		String senderPassword = Addressing.findLiteralData(operation.getSender(), new XRI3("$password"));

		// retrieve the sender xri and user

		Xri senderXri = null;
		String senderUserIdentifier = null;
		User senderUser = null;

		try {

			senderXri = this.pdsConnectionFactory.getXriStore().findXri(senderXriString);
			if (senderXri != null) senderUserIdentifier = senderXri.getUserIdentifier();
			if (senderUserIdentifier != null) senderUser = this.pdsConnectionFactory.getIbrokerStore().findUser(senderUserIdentifier);
		} catch (Exception ex) {

			throw new PdsConnectionException("Cannot find sender user " + senderXriString + ": " + ex.getMessage(), ex);
		}

		// check if the sender user is the same as the one on which we operate

		boolean isSelf = (senderUser != null && senderUser.equals(this.user));

		// check if the user password is correct

		boolean userPasswordCorrect = senderPassword != null && senderUser != null && StoreUtil.checkPass(senderUser.getPass(), senderPassword);

		// authenticated?

		return isSelf && userPasswordCorrect;
	}

	public String getPassword() throws PdsConnectionException {

		try {

			this.user.getPass();
			return "xxx";
		} catch (Exception ex) {

			throw new PdsConnectionException("Cannot read user password: " + ex.getMessage(), ex);
		}
	}*/

	public AbstractMessagingTarget[] getPdsConnectionMessagingTargets() {

		PdsConnectionResourceMessagingTarget pdsConnectionResourceMessagingTarget = new PdsConnectionResourceMessagingTarget();
		pdsConnectionResourceMessagingTarget.setPdsConnection(this);

		return new AbstractMessagingTarget[] { pdsConnectionResourceMessagingTarget };
	}

	public Xri getXri() {

		return this.xri;
	}

	public User getUser() {

		return this.user;
	}
}
