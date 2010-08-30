package pds.web.ui.app.addressbook;

import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.WindowPane;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.ui.app.PdsWebApp;
import pds.web.xdi.objects.XdiContext;

public class AddressBookPdsWebApp implements PdsWebApp {

	@Override
	public String getName() {

		return "Address Book";
	}

	@Override
	public ResourceImageReference getResourceImageReference() {

		return new ResourceImageReference("/pds/web/ui/app/addressbook/app.png");
	}

	@Override
	public WindowPane newWindowPane(XdiContext context, XRI3Segment subjectXri) {

		AddressBookWindowPane addressBookWindowPane = new AddressBookWindowPane();
		addressBookWindowPane.setContextAndSubjectXri(context, subjectXri);

		return addressBookWindowPane;
	}
}
