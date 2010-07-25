package pds.web.ui.app.directxdi;

import java.util.ResourceBundle;

import nextapp.echo.app.Extent;
import nextapp.echo.app.WindowPane;
import pds.web.ui.app.directxdi.DirectXdiContentPane;

public class DirectXdiWindowPane extends WindowPane {

	private static final long serialVersionUID = 1031507179316742853L;

	protected ResourceBundle resourceBundle;

	private DirectXdiContentPane addressBookContentPane;

	/**
	 * Creates a new <code>ConfigureAPIsWindowPane</code>.
	 */
	public DirectXdiWindowPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setStyleName("Blue");
		this.setTitle("Direct XDI");
		this.setHeight(new Extent(600, Extent.PX));
		this.setMinimizeEnabled(false);
		this.setMaximizeEnabled(true);
		this.setClosable(true);
		this.setWidth(new Extent(800, Extent.PX));
		addressBookContentPane = new DirectXdiContentPane();
		add(addressBookContentPane);
	}
}
