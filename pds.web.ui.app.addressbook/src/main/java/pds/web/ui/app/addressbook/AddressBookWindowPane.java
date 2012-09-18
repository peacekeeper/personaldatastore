package pds.web.ui.app.addressbook;

import java.util.ResourceBundle;

import nextapp.echo.app.Extent;
import nextapp.echo.app.WindowPane;

import org.eclipse.higgins.XDI2.xri3.impl.XRI3Segment;

import pds.xdi.XdiContext;

public class AddressBookWindowPane extends WindowPane {

	private static final long serialVersionUID = 4111493581013444404L;

	protected ResourceBundle resourceBundle;

	private AddressBookContentPane addressBookContentPane;

	/**
	 * Creates a new <code>ConfigureAPIsWindowPane</code>.
	 */
	public AddressBookWindowPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	public void setContextAndSubjectXri(XdiContext context, XRI3Segment subjectXri) {

		this.addressBookContentPane.setEndpointAndContextNodeXri(context, subjectXri);
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setStyleName("Blue");
		this.setTitle("Address Book");
		this.setHeight(new Extent(600, Extent.PX));
		this.setMinimizeEnabled(false);
		this.setMaximizeEnabled(true);
		this.setClosable(true);
		this.setDefaultCloseOperation(WindowPane.DISPOSE_ON_CLOSE);
		this.setWidth(new Extent(800, Extent.PX));
		addressBookContentPane = new AddressBookContentPane();
		add(addressBookContentPane);
	}
}
