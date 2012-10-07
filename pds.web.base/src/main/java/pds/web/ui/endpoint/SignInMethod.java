package pds.web.ui.endpoint;

import nextapp.echo.app.Panel;

public interface SignInMethod {

	public String getMethodName();
	
	public Panel newPanel();
}
