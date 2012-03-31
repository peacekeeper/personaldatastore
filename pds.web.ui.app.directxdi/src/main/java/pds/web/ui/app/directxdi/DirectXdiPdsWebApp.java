package pds.web.ui.app.directxdi;

import nextapp.echo.app.ResourceImageReference;

import org.eclipse.higgins.XDI2.xri3.impl.XRI3Segment;

import pds.web.ui.MainContentPane;
import pds.web.ui.app.PdsWebApp;
import pds.xdi.XdiContext;

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
	public void onActionPerformed(MainContentPane mainContentPane, XdiContext context, XRI3Segment subjectXri) {

		DirectXdiWindowPane directXdiWindowPane = new DirectXdiWindowPane();
		directXdiWindowPane.setContext(context);

		mainContentPane.add(directXdiWindowPane);
	}
}
