package pds.core.single;

import pds.core.base.PdsInstance;
import pds.core.base.impl.AbstractPdsInstance;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.target.impl.AbstractMessagingTarget;

public class SinglePdsInstance extends AbstractPdsInstance implements PdsInstance {

	private XRI3Segment canonical;
	private XRI3Segment[] aliases;
	private String[] endpoints;
	private String privateKey;
	private String publicKey;
	private String certificate;

	SinglePdsInstance(String pdsPath, XRI3Segment canonical, XRI3Segment[] aliases, String[] endpoints, String privateKey, String publicKey, String certificate) {

		super(pdsPath);

		this.canonical = canonical;
		this.aliases = aliases;
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

		return this.aliases;
	}

	public String[] getEndpoints() {

		return this.endpoints;
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
