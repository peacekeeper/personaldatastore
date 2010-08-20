package pds.core.single;

import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractMessagingTarget;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.PdsConnection;
import pds.core.impl.AbstractPdsConnection;

public class SinglePdsConnection extends AbstractPdsConnection implements PdsConnection {

	private XRI3Segment canonical;
	private XRI3Segment[] aliases;
	private String[] endpoints;

	SinglePdsConnection(String identifier, XRI3Segment canonical, XRI3Segment[] aliases, String[] endpoints) {

		super(identifier);

		this.canonical = canonical;
		this.aliases = aliases;
		this.endpoints = endpoints;
	}

	@Override
	public XRI3Segment getCanonical() {

		return this.canonical;
	}

	public XRI3Segment[] getAliases() {

		return this.aliases;
	}

	public String[] getEndpoints() {

		return this.endpoints;
	}

	public AbstractMessagingTarget[] getMessagingTargets() {

		return new AbstractMessagingTarget[0];
	}
}
