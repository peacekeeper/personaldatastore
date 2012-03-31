package pds.web.ui.app.addressbook;

import nextapp.echo.app.ResourceImageReference;

import org.eclipse.higgins.XDI2.xri3.impl.XRI3Segment;

import pds.web.ui.MainContentPane;
import pds.web.ui.app.PdsWebApp;
import pds.xdi.XdiContext;

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
	public void onActionPerformed(MainContentPane mainContentPane, XdiContext context, XRI3Segment subjectXri) {

		AddressBookWindowPane addressBookWindowPane = new AddressBookWindowPane();
		addressBookWindowPane.setContextAndSubjectXri(context, subjectXri);

		mainContentPane.add(addressBookWindowPane);
	}
}
