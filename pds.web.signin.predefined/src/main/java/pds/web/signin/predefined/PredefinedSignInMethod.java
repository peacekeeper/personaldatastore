package pds.web.signin.predefined;

import java.util.Map;

import nextapp.echo.app.Panel;
import pds.web.ui.context.SignInMethod;

public class PredefinedSignInMethod implements SignInMethod {

	private Map<String, String> contexts;

	@Override
	public String getMethodName() {

		return "Predefined Sign-In";
	}

	@Override
	public Panel newPanel() {

		PredefinedSignInPanel predefinedSignInPanel = new PredefinedSignInPanel();
		predefinedSignInPanel.setPredefinedSignInMethod(this);

		return predefinedSignInPanel;
	}

	public Map<String, String> getContexts() {

		return this.contexts;
	}

	public void setContexts(Map<String, String> contexts) {

		this.contexts = contexts;
	}
}
