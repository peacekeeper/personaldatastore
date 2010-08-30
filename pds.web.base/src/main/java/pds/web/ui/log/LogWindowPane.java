package pds.web.ui.log;

import java.util.ResourceBundle;

import nextapp.echo.app.Extent;
import nextapp.echo.app.WindowPane;
import pds.web.ui.log.LogContentPane;

public class LogWindowPane extends WindowPane {

	private static final long serialVersionUID = -6951477220179356866L;

	protected ResourceBundle resourceBundle;

	/**
	 * Creates a new <code>LogWindowPane</code>.
	 */
	public LogWindowPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void init() {

		// put us in the bottom left corner

		this.setPositionX(new Extent(0));
		this.setPositionY(new Extent(99999));
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setStyleName("Glass");
		this.setTitle("Log Window");
		this.setHeight(new Extent(240, Extent.PX));
		this.setMinimizeEnabled(false);
		this.setMaximizeEnabled(true);
		this.setVisible(true);
		this.setClosable(false);
		this.setWidth(new Extent(600, Extent.PX));
		LogContentPane logContentPane2 = new LogContentPane();
		add(logContentPane2);
	}
}
