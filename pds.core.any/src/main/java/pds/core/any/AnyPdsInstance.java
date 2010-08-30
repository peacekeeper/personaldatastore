package pds.core.any;

import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractMessagingTarget;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.base.PdsInstance;
import pds.core.base.impl.AbstractPdsInstance;

public class AnyPdsInstance extends AbstractPdsInstance implements PdsInstance {

	private XRI3Segment canonical;
	private String[] endpoints;

	AnyPdsInstance(String target, XRI3Segment canonical, String[] endpoints) {

		super(target);

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

		String[] endpoints = new String[this.endpoints.length];

		for (int i=0; i<endpoints.length; i++) {

			endpoints[i] = this.endpoints[i];
			if (! endpoints[i].endsWith("/")) endpoints[i] += "/";
			endpoints[i] += this.getCanonical().toString() + "/";
		}

		return endpoints;
	}

	public AbstractMessagingTarget[] getMessagingTargets() {

		return new AbstractMessagingTarget[0];
	}
}