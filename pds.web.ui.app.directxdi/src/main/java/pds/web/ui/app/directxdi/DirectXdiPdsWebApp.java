package pds.web.ui.app.directxdi;

import nextapp.echo.app.ResourceImageReference;
import pds.web.ui.MainContentPane;
import pds.web.ui.app.PdsWebApp;
import pds.xdi.XdiEndpoint;

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
	public void onActionPerformed(MainContentPane mainContentPane, XdiEndpoint endpoint) {

		DirectXdiWindowPane directXdiWindowPane = new DirectXdiWindowPane();
		directXdiWindowPane.setEndpoint(endpoint);

		mainContentPane.add(directXdiWindowPane);
	}
}
