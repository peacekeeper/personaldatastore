package pds.web.ui.context;

import nextapp.echo.app.Panel;

public interface SignInMethod {

	public String getMethodName();
	
	public Panel newPanel();
}
