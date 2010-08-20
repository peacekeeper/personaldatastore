package pds.core;

import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractMessagingTarget;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

/**
 * A PdsConnection represents a single PDS account backed by an XDI4j Graph object.
 */
public interface PdsConnection {

	/**
	 * The identifier for which this PdsConnection has been originally instantiated.
	 */
	public String getIdentifier();

	/**
	 * The canonical identifier for this PdsConnection (e.g. an I-Number).
	 */
	public XRI3Segment getCanonical();

	/**
	 * Known synonym identifiers for this PdsConnection.
	 */
	public XRI3Segment[] getAliases();

	/**
	 * XDI endpoint URIs for this PdsConnection.
	 */
	public String[] getEndpoints();

	/**
	 * Optional additional XDI4j MessagingTargets for this PdsConnection.
	 */
	public AbstractMessagingTarget[] getMessagingTargets();
}
