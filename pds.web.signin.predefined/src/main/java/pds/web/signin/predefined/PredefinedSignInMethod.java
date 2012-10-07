package pds.web.signin.predefined;

import java.util.List;

import nextapp.echo.app.Panel;
import pds.web.ui.endpoint.SignInMethod;

public class PredefinedSignInMethod implements SignInMethod {

	private List<PredefinedSignIn> predefinedSignIns;

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

	public List<PredefinedSignIn> getPredefinedSignIns() {

		return this.predefinedSignIns;
	}

	public void setPredefinedSignIns(List<PredefinedSignIn> predefinedSignIns) {

		this.predefinedSignIns = predefinedSignIns;
	}
}
