package pds.web.signup.xri.grs;

import nextapp.echo.app.Panel;
import pds.web.signin.xri.XriSignUpMethod;
import pds.web.signup.xri.AbstractXriSignUpMethod;

import com.fullxri.mpay4java.MpayTools;

public class GrsXriSignUpMethod extends AbstractXriSignUpMethod implements XriSignUpMethod {

	private MpayTools mpayTools;

	@Override
	public Panel newPanel() {

		GrsXriSignUpPanel grsXriSignUpPanel = new GrsXriSignUpPanel();
		grsXriSignUpPanel.setGrsXriSignUpMethod(this);

		return grsXriSignUpPanel;
	}

	public MpayTools getMpayTools() {

		return this.mpayTools;
	}

	public void setMpayTools(MpayTools mpayTools) {

		this.mpayTools = mpayTools;
	}
}
