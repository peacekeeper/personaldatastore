package pds.web.signin.predefined;

import java.io.Serializable;

import xdi2.core.xri3.impl.XRI3Segment;

public class PredefinedSignIn implements Serializable {

	private static final long serialVersionUID = -608966612789716281L;

	private String endpointUrl;
	private String identifier;
	private XRI3Segment canonical;
	private String secretToken;

	public String getEndpointUrl() {

		return this.endpointUrl;
	}

	public void setEndpointUrl(String endpointUrl) {

		this.endpointUrl = endpointUrl;
	}

	public String getIdentifier() {

		return this.identifier;
	}

	public void setIdentifier(String identifier) {

		this.identifier = identifier;
	}

	public XRI3Segment getCanonical() {

		return this.canonical;
	}

	public void setCanonical(XRI3Segment canonical) {

		this.canonical = canonical;
	}

	public String getSecretToken() {

		return this.secretToken;
	}

	public void setSecretToken(String secretToken) {

		this.secretToken = secretToken;
	}
}
