package pds.web.ui.app.directxdi;

import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.WindowPane;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.ui.app.PdsWebApp;
import pds.web.xdi.objects.XdiContext;

public class DirectXdiPdsWebApp implements PdsWebApp {

	@Override
	public String getName() {

		return "Direct XDI";
	}

	@Override
	public ResourceImageReference getResourceImageReference() {

		return new ResourceImageReference("/pds/web/ui/app/directxdi/app.png");
	}

	@Override
	public WindowPane newWindowPane(XdiContext context, XRI3Segment subjectXri) {

		DirectXdiWindowPane directXdiWindowPane = new DirectXdiWindowPane();
		directXdiWindowPane.setContext(context);

		return directXdiWindowPane;
	}
}
