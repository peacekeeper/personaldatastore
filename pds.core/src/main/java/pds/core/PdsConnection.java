package pds.core;

import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

public interface PdsConnection {

	public XRI3Segment getCanonical();
	public String[] getAliases();
	public String[] getEndpoints();
	public boolean isSelfAuthenticated(Operation operation) throws PdsConnectionException;
	public String getPassword() throws PdsConnectionException;
	public String getPublicKey() throws PdsConnectionException;
	public String getPrivateKey() throws PdsConnectionException;
	public String getCertificate() throws PdsConnectionException;
	public void setNewPassword(String newPassword) throws PdsConnectionException;
}
