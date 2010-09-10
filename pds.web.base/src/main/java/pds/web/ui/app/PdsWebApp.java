package pds.web.ui.app;

import nextapp.echo.app.ResourceImageReference;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.ui.MainContentPane;
import pds.xdi.XdiContext;

public interface PdsWebApp {

	public String getName();
	public ResourceImageReference getResourceImageReference();
	public void onActionPerformed(MainContentPane mainContentPane, XdiContext context, XRI3Segment subjectXri);
}
