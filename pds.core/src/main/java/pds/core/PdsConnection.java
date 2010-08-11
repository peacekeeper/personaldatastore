package pds.core;

import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractMessagingTarget;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

public interface PdsConnection {

	public XRI3Segment getCanonical();
	public String[] getAliases();
	public String[] getEndpoints();
	public AbstractMessagingTarget[] getMessagingTargets();
}
