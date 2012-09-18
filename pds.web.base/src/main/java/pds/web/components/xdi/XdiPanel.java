package pds.web.components.xdi;

import java.util.ResourceBundle;

import nextapp.echo.app.Button;
import nextapp.echo.app.Panel;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import pds.web.ui.DeveloperModeComponent;
import pds.web.ui.MainWindow;
import pds.xdi.XdiEndpoint;
import xdi2.core.xri3.impl.XRI3Segment;

public class XdiPanel extends Panel implements DeveloperModeComponent {

	private static final long serialVersionUID = -5082464847478633075L;

	protected ResourceBundle resourceBundle;

	private XdiEndpoint endpoint;
	private XRI3Segment mainAddress;
	private XRI3Segment[] getAddresses;

	/**
	 * Creates a new <code>ClaimPanel</code>.
	 */
	public XdiPanel() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void init() {

		super.init();
		
		this.setVisible(MainWindow.findMainContentPane(this).isDeveloperModeSelected());
	}

	@Override
	public void dispose() {

		super.dispose();
	}

	public void setEndpointAndMainAddressAndGetAddresses(XdiEndpoint endpoint, XRI3Segment mainAddress, XRI3Segment[] getAddresses) {

		this.endpoint = endpoint;
		this.mainAddress = mainAddress;
		this.getAddresses = getAddresses;
	}

	public XdiEndpoint getEndpoint() {

		return this.endpoint;
	}

	public XRI3Segment getMainAddress() {
		
		return this.mainAddress;
	}

	public XRI3Segment[] getGetAddresses() {
		
		return this.getAddresses;
	}

	private void onButtonActionPerformed(ActionEvent e) {

		XdiWindowPane xdiWindowPane = new XdiWindowPane();
		xdiWindowPane.setEndpointAndMainAddressAndGetAddresses(this.endpoint, this.mainAddress, this.getAddresses);

		MainWindow.findMainContentPane(this).add(xdiWindowPane);
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setVisible(false);
		Button button1 = new Button();
		button1.setStyleName("Plain");
		ResourceImageReference imageReference1 = new ResourceImageReference(
				"/pds/web/resource/image/xdi.png");
		button1.setIcon(imageReference1);
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onButtonActionPerformed(e);
			}
		});
		add(button1);
	}
}
