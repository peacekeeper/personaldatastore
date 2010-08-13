package pds.core.single;

import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractMessagingTarget;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.PdsConnection;

public class SinglePdsConnection implements PdsConnection {

	private XRI3Segment identifier;
	private XRI3Segment[] aliases;
	private String[] endpoints;

	SinglePdsConnection(XRI3Segment identifier, XRI3Segment[] aliases, String[] endpoints) {

		this.identifier = identifier;
		this.aliases = aliases;
		this.endpoints = endpoints;
	}

	@Override
	public XRI3Segment getCanonical() {

		return this.identifier;
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
