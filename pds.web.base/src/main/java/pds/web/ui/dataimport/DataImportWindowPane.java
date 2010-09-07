package pds.web.ui.dataimport;

import java.util.ResourceBundle;

import nextapp.echo.app.Extent;
import nextapp.echo.app.WindowPane;
import pds.web.xdi.XdiContext;
import pds.web.ui.dataimport.DataImportContentPane;

public class DataImportWindowPane extends WindowPane {

	private static final long serialVersionUID = 4111493581013444404L;

	protected ResourceBundle resourceBundle;

	private DataImportContentPane dataImportContentPane;

	/**
	 * Creates a new <code>ConfigureAPIsWindowPane</code>.
	 */
	public DataImportWindowPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	public void setContext(XdiContext context) {

		this.dataImportContentPane.setContext(context);
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setStyleName("Gray");
		this.setTitle("Data Import");
		this.setHeight(new Extent(400, Extent.PX));
		this.setMinimizeEnabled(false);
		this.setMaximizeEnabled(true);
		this.setClosable(true);
		this.setWidth(new Extent(600, Extent.PX));
		dataImportContentPane = new DataImportContentPane();
		add(dataImportContentPane);
	}
}
