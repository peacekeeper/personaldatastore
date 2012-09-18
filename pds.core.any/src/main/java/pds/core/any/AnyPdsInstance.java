package pds.core.any;

import pds.core.base.PdsInstance;
import pds.core.base.impl.AbstractPdsInstance;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.target.AbstractMessagingTarget;

public class AnyPdsInstance extends AbstractPdsInstance implements PdsInstance {

	private XRI3Segment canonical;
	private String[] endpoints;
	private String privateKey;
	private String publicKey;
	private String certificate;

	AnyPdsInstance(String pdsPath, XRI3Segment canonical, String[] endpoints, String privateKey, String publicKey, String certificate) {

		super(pdsPath);

		this.canonical = canonical;
		this.endpoints = endpoints;
		this.privateKey = privateKey;
		this.publicKey = publicKey;
		this.certificate = certificate;
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

	public String getPrivateKey() {

		return this.privateKey;
	}

	public String getPublicKey() {

		return this.publicKey;
	}

	public String getCertificate() {

		return this.certificate;
	}

	public AbstractMessagingTarget[] getAdditionalMessagingTargets() {

		return new AbstractMessagingTarget[0];
	}
}
