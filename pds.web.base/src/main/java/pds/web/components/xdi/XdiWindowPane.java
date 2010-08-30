package pds.web.components.xdi;

import java.util.ResourceBundle;

import nextapp.echo.app.Extent;
import nextapp.echo.app.WindowPane;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;

import pds.web.xdi.objects.XdiContext;

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

	public void setContextAndMainAddressAndGetAddresses(XdiContext context, XRI3 mainAddress, XRI3[] getAddresses) {

		this.xdiContentPane.setContextAndMainAddressAndGetAddresses(context, mainAddress, getAddresses);
	}

	public XdiContext getContext() {

		return this.xdiContentPane.getContext();
	}

	public XRI3 getMainAddress() {

		return this.xdiContentPane.getMainAddress();
	}

	public XRI3[] getGetAddresses() {

		return this.xdiContentPane.getGetAddresses();
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
