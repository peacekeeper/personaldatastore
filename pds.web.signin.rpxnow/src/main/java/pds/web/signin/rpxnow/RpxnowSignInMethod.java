package pds.web.signin.rpxnow;

import nextapp.echo.app.Panel;
import pds.web.ui.context.SignInMethod;

public class RpxnowSignInMethod implements SignInMethod {

	private String baseUrl;
	private String apiKey;
	private String htmlCode;
	private String endpoint;

	@Override
	public Panel newPanel() {

		RpxnowSignInPanel rpxnowSignPanel = new RpxnowSignInPanel();
		rpxnowSignPanel.setRpxnowSignInMethod(this);

		return rpxnowSignPanel;
	}

	public String getBaseUrl() {

		return this.baseUrl;
	}

	public void setBaseUrl(String baseUrl) {

		this.baseUrl = baseUrl;
	}

	public String getApiKey() {

		return this.apiKey;
	}

	public void setApiKey(String apiKey) {

		this.apiKey = apiKey;
	}

	public String getHtmlCode() {

		return this.htmlCode;
	}

	public void setHtmlCode(String htmlCode) {

		this.htmlCode = htmlCode;
	}

	public String getEndpoint() {

		return this.endpoint;
	}

	public void setEndpoint(String endpoint) {

		this.endpoint = endpoint;
	}
}
