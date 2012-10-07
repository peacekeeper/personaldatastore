package pds.web.components.xdi;

import java.util.ResourceBundle;

import nextapp.echo.app.Extent;
import nextapp.echo.app.WindowPane;
import pds.xdi.XdiEndpoint;
import pds.xdi.events.XdiGraphListener;

public class XdiWindowPane extends WindowPane {

	private static final long serialVersionUID = 4136493581013444404L;

	protected ResourceBundle resourceBundle;

	private XdiContentPane xdiContentPane;

	/**
	 * Creates a new <code>ConfigureAPIsWindowPane</code>.
	 */
	public XdiWindowPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	public void setEndpointAndGraphListener(XdiEndpoint endpoint, XdiGraphListener graphListener) {

		this.xdiContentPane.setEndpointAndGraphListener(endpoint, graphListener);
	}

	public XdiEndpoint getEndpoint() {

		return this.xdiContentPane.getEndpoint();
	}

	public XdiGraphListener getGraphListener() {

		return this.xdiContentPane.getGraphListener();
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setStyleName("Gray");
		this.setTitle("XDI Data");
		this.setHeight(new Extent(600, Extent.PX));
		this.setMinimizeEnabled(false);
		this.setMaximizeEnabled(true);
		this.setClosable(true);
		this.setWidth(new Extent(800, Extent.PX));
		xdiContentPane = new XdiContentPane();
		add(xdiContentPane);
	}
}
