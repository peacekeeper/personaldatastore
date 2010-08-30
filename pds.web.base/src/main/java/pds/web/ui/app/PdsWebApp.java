package pds.web.ui.app;

import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.WindowPane;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.xdi.XdiContext;

public interface PdsWebApp {

	public String getName();
	public ResourceImageReference getResourceImageReference();
	public WindowPane newWindowPane(XdiContext context, XRI3Segment subjectXri);
}
