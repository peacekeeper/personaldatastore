package pds.core.xri;

import java.util.List;

import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractMessagingTarget;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.openxri.xml.CanonicalID;

import pds.core.PdsConnection;
import pds.core.impl.AbstractPdsConnection;
import pds.core.xri.messagingtargets.XriResourceMessagingTarget;
import pds.store.user.User;
import pds.store.xri.Xri;
import pds.store.xri.XriStoreException;

public class XriPdsConnection extends AbstractPdsConnection implements PdsConnection {

	private XriPdsConnectionFactory pdsConnectionFactory;
	private Xri xri;
	private User user;
	private String[] endpoints;

	XriPdsConnection(String identifier, XriPdsConnectionFactory pdsConnectionFactory, Xri xri, User user, String[] endpoints) {

		super(identifier);

		this.pdsConnectionFactory = pdsConnectionFactory;
		this.xri = xri;
		this.user = user;
		this.endpoints = endpoints;
	}

	public XriPdsConnectionFactory getPdsConnectionFactory() {

		return this.pdsConnectionFactory;
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

	public XRI3Segment[] getAliases() {

		List<String> aliases = this.xri.getAliases();
		XRI3Segment canonical = this.getCanonical();

		int size = aliases.size();
		if (canonical != null) size++;

		XRI3Segment[] xriAliases = new XRI3Segment[size];

		for (int i=0; i<aliases.size(); i++) xriAliases[i] = new XRI3Segment(aliases.get(i));
		if (canonical != null) xriAliases[size-1] = canonical;

		return xriAliases;
	}

	public String[] getEndpoints() {

		String[] endpoints = new String[this.endpoints.length];

		for (int i=0; i<endpoints.length; i++) {

			endpoints[i] = this.endpoints[i];
			if (! endpoints[i].endsWith("/")) endpoints[i] += "/";
			endpoints[i] += this.getCanonical().toString() + "/";
		}

		return endpoints;
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

	public AbstractMessagingTarget[] getMessagingTargets() {

		XriResourceMessagingTarget pdsConnectionResourceMessagingTarget = new XriResourceMessagingTarget();
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
