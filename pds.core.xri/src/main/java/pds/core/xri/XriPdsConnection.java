package pds.core.xri;

import ibrokerkit.ibrokerstore.store.StoreUtil;
import ibrokerkit.ibrokerstore.store.User;
import ibrokerkit.iname4java.store.Xri;
import ibrokerkit.iname4java.store.XriStoreException;

import java.util.List;

import org.eclipse.higgins.xdi4j.addressing.Addressing;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.openxri.xml.CanonicalID;

import pds.core.PdsConnection;
import pds.core.PdsConnectionException;


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

	public boolean isSelfAuthenticated(Operation operation) throws PdsConnectionException {

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
	}

	public String getPublicKey() throws PdsConnectionException {

		try {

			return this.xri.getAuthorityAttribute("publickey");
		} catch (Exception ex) {

			throw new PdsConnectionException("Cannot read XRI authority attribute: " + ex.getMessage(), ex);
		}
	}

	public String getPrivateKey() throws PdsConnectionException {

		try {

			return this.xri.getAuthorityAttribute("privatekey");
		} catch (Exception ex) {

			throw new PdsConnectionException("Cannot read XRI authority attribute: " + ex.getMessage(), ex);
		}
	}

	public String getCertificate() throws PdsConnectionException {

		try {

			return this.xri.getAuthorityAttribute("certificate");
		} catch (Exception ex) {

			throw new PdsConnectionException("Cannot read XRI authority attribute: " + ex.getMessage(), ex);
		}
	}

	public void setNewPassword(String newPassword) throws PdsConnectionException {

		try {

			this.user.setPass(StoreUtil.hashPass(newPassword));
			this.pdsConnectionFactory.getIbrokerStore().updateObject(this.user);
		} catch (Exception ex) {

			throw new PdsConnectionException("Cannot set user password: " + ex.getMessage(), ex);
		}
	}
}
