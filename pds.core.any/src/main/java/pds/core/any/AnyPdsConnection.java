package pds.core.any;

import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractMessagingTarget;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.PdsConnection;

public class AnyPdsConnection implements PdsConnection {

	private String identifier;
	private String[] endpoints;

	AnyPdsConnection(String identifier, String[] endpoints) {

		this.identifier = identifier;
		this.endpoints = endpoints;
	}

	@Override
	public XRI3Segment getCanonical() {

		return new XRI3Segment(this.identifier);
	}

	public String[] getAliases() {

		return new String[0];
	}

	public String[] getEndpoints() {

		return this.endpoints;
	}

	public AbstractMessagingTarget[] getMessagingTargets() {

		return new AbstractMessagingTarget[0];
	}
}
