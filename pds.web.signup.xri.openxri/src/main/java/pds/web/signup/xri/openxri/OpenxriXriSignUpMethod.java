package pds.web.signup.xri.openxri;

import nextapp.echo.app.Panel;
import pds.web.signin.xri.XriSignUpMethod;
import pds.web.signup.xri.AbstractXriSignUpMethod;

public class OpenxriXriSignUpMethod extends AbstractXriSignUpMethod implements XriSignUpMethod {

	@Override
	public Panel newPanel() {

		OpenxriXriSignUpPanel openxriXriSignUpPanel = new OpenxriXriSignUpPanel();
		openxriXriSignUpPanel.setOpenxriXriSignUpMethod(this);

		return openxriXriSignUpPanel;
	}
}
