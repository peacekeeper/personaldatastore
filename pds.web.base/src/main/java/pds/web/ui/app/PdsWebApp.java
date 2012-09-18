package pds.web.ui.app;

import nextapp.echo.app.ResourceImageReference;
import pds.web.ui.MainContentPane;
import pds.xdi.XdiEndpoint;
import xdi2.core.xri3.impl.XRI3Segment;

public interface PdsWebApp {

	public String getName();
	public ResourceImageReference getResourceImageReference();
	public void onActionPerformed(MainContentPane mainContentPane, XdiEndpoint endpoint, XRI3Segment contextNdoeXri);
}
