package pds.web.signin.xri;

import java.util.List;

import nextapp.echo.app.Panel;
import pds.web.ui.context.SignInMethod;

public class XriSignInMethod implements SignInMethod {

	private List<XriSignUpMethod> xriSignUpMethods;

	@Override
	public String getMethodName() {
		
		return "I-Name Sign-In";
	}

	@Override
	public Panel newPanel() {

		XriSignInPanel xriSignPanel = new XriSignInPanel();
		xriSignPanel.setXriSignInMethod(this);

		return xriSignPanel;
	}

	public List<XriSignUpMethod> getXriSignUpMethods() {

		return this.xriSignUpMethods;
	}

	public void setXriSignUpMethods(List<XriSignUpMethod> xriSignUpMethods) {

		this.xriSignUpMethods = xriSignUpMethods;
	}
}
