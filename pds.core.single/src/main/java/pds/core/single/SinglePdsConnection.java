package pds.core.single;

import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.PdsConnection;
import pds.core.PdsConnectionException;


public class SinglePdsConnection implements PdsConnection {

	private SinglePdsConnectionFactory pdsConnectionFactory;
	private String identifier;

	SinglePdsConnection(SinglePdsConnectionFactory pdsConnectionFactory, String identifier) {

		this.pdsConnectionFactory = pdsConnectionFactory;
		this.identifier = identifier;
	}

	@Override
	public XRI3Segment getCanonical() {

		return new XRI3Segment(identifier);
	}

	public String[] getAliases() {

		return new String[] { this.identifier };
	}

	public String[] getEndpoints() {

		return new String[] { };
	}

	public boolean isSelfAuthenticated(Operation operation) throws PdsConnectionException {

		throw new RuntimeException("Not implemented");
	}

	public String getPassword() throws PdsConnectionException {

		throw new RuntimeException("Not implemented");
	}

	public String getPublicKey() throws PdsConnectionException {

		throw new RuntimeException("Not implemented");
	}

	public String getPrivateKey() throws PdsConnectionException {

		throw new RuntimeException("Not implemented");
	}

	public String getCertificate() throws PdsConnectionException {

		throw new RuntimeException("Not implemented");
	}

	public void setNewPassword(String newPassword) throws PdsConnectionException {

		throw new RuntimeException("Not implemented");
	}
}
