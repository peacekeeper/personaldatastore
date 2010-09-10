package pds.web.components.xdi;

import java.util.ResourceBundle;

import nextapp.echo.app.Button;
import nextapp.echo.app.Panel;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;

import pds.web.ui.DeveloperModeComponent;
import pds.web.ui.MainWindow;
import pds.xdi.XdiContext;

public class XdiPanel extends Panel implements DeveloperModeComponent {

	private static final long serialVersionUID = -5082464847478633075L;

	protected ResourceBundle resourceBundle;

	private XdiContext context;
	private XRI3 mainAddress;
	private XRI3[] getAddresses;

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

	public void setContextAndMainAddressAndGetAddresses(XdiContext context, XRI3 mainAddress, XRI3[] getAddresses) {

		this.context = context;
		this.mainAddress = mainAddress;
		this.getAddresses = getAddresses;
	}

	public XdiContext getContext() {

		return this.context;
	}

	public XRI3 getMainAddress() {
		
		return this.mainAddress;
	}

	public XRI3[] getGetAddresses() {
		
		return this.getAddresses;
	}

	private void onButtonActionPerformed(ActionEvent e) {

		XdiWindowPane xdiWindowPane = new XdiWindowPane();
		xdiWindowPane.setContextAndMainAddressAndGetAddresses(this.context, this.mainAddress, this.getAddresses);

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
