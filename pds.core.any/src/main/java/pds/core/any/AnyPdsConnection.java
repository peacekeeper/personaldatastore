package pds.core.any;

import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractMessagingTarget;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.PdsConnection;
import pds.core.impl.AbstractPdsConnection;

public class AnyPdsConnection extends AbstractPdsConnection implements PdsConnection {

	private XRI3Segment canonical;
	private String[] endpoints;

	AnyPdsConnection(String identifier, XRI3Segment canonical, String[] endpoints) {

		super(identifier);
		
		this.canonical = canonical;
		this.endpoints = endpoints;
	}

	@Override
	public XRI3Segment getCanonical() {

		return this.canonical;
	}

	public XRI3Segment[] getAliases() {

		return new XRI3Segment[0];
	}

	public String[] getEndpoints() {

		return this.endpoints;
	}

	public AbstractMessagingTarget[] getMessagingTargets() {

		return new AbstractMessagingTarget[0];
	}
}
