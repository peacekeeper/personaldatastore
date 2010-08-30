package pds.core.base;

import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractMessagingTarget;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

/**
 * A PdsInstance represents a single PDS account backed by an XDI4j Graph object.
 * 
 * @author Markus
 */
public interface PdsInstance {

	/**
	 * The target for which this PdsInstance has been originally instantiated.
	 */
	public String getTarget();

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

	/**
	 * Optional additional XDI4j MessagingTargets for this PdsInstance.
	 */
	public AbstractMessagingTarget[] getMessagingTargets();
}
