package pds.web.ui.dataexport;

import java.util.ResourceBundle;

import nextapp.echo.app.Extent;
import nextapp.echo.app.WindowPane;
import pds.xdi.XdiEndpoint;

public class DataExportWindowPane extends WindowPane {

	private static final long serialVersionUID = 4111493581013444404L;

	protected ResourceBundle resourceBundle;

	private DataExportContentPane dataExportContentPane;

	/**
	 * Creates a new <code>ConfigureAPIsWindowPane</code>.
	 */
	public DataExportWindowPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	public void setEndpoint(XdiEndpoint endpoint) {

		this.dataExportContentPane.setEndpoint(endpoint);
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setStyleName("Gray");
		this.setTitle("Data Export");
		this.setHeight(new Extent(400, Extent.PX));
		this.setMinimizeEnabled(false);
		this.setMaximizeEnabled(true);
		this.setClosable(true);
		this.setWidth(new Extent(600, Extent.PX));
		dataExportContentPane = new DataExportContentPane();
		add(dataExportContentPane);
	}
}
