package pds.web.ui.app;

import nextapp.echo.app.ResourceImageReference;
import pds.web.ui.MainContentPane;
import pds.xdi.XdiEndpoint;

public interface PdsWebApp {

	public String getName();
	public ResourceImageReference getResourceImageReference();
	public void onActionPerformed(MainContentPane mainContentPane, XdiEndpoint endpoint);
}
