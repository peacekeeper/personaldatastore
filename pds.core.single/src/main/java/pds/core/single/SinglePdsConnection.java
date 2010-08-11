package pds.core.single;

import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractMessagingTarget;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.PdsConnection;

public class SinglePdsConnection implements PdsConnection {

	private String identifier;
	private String[] aliases;
	private String[] endpoints;

	SinglePdsConnection(String identifier, String[] aliases, String[] endpoints) {

		this.identifier = identifier;
		this.aliases = aliases;
		this.endpoints = endpoints;
	}

	@Override
	public XRI3Segment getCanonical() {

		return new XRI3Segment(this.identifier);
	}

	public String[] getAliases() {

		return this.aliases;
	}

	public String[] getEndpoints() {

		return this.endpoints;
	}

	public AbstractMessagingTarget[] getMessagingTargets() {

		return new AbstractMessagingTarget[0];
	}
}
