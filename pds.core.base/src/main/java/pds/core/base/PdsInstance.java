package pds.core.base;

import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.target.AbstractMessagingTarget;

/**
 * A PdsInstance represents a single PDS account backed by an XDI2 Graph object.
 * 
 * @author Markus
 */
public interface PdsInstance {

	/**
	 * The PDS path for which this PdsInstance has been originally instantiated.
	 */
	public String getPdsPath();

	/**
	 * The canonical identifier for this PdsInstance (e.g. an I-Number).
	 */
	public XRI3Segment getCanonical();

	/**
	 * Known synonym identifiers for this PdsInstance.
	 */
	public XRI3Segment[] getAliases();

	/**
	 * XDI endpoint URIs for this PdsInstance.
	 */
	public String[] getEndpoints();
	
	public String getPrivateKey();

	public String getPublicKey();
	
	public String getCertificate();
	
	/**
	 * Optional additional XDI2 MessagingTargets for this PdsInstance.
	 */
	public AbstractMessagingTarget[] getAdditionalMessagingTargets();
}
