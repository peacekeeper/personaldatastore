package pds.web.ui;

import java.util.ResourceBundle;

import nextapp.echo.app.Button;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Panel;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import pds.web.ui.accountpersona.AccountPersonaWindowPane;
import pds.xdi.XdiEndpoint;
import pds.xdi.events.XdiGraphAddEvent;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import pds.xdi.events.XdiGraphModEvent;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.Literal;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;

public class AccountPersonaPanel extends Panel implements XdiGraphListener {

	private static final long serialVersionUID = -5082464847478633075L;

	protected ResourceBundle resourceBundle;

	private XdiEndpoint endpoint;
	private XRI3Segment contextNodeXri;

	private Button button;

	/**
	 * Creates a new <code>AccountPersonaPanel</code>.
	 */
	public AccountPersonaPanel() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void init() {

		super.init();
	}

	@Override
	public void dispose() {

		super.dispose();

		// remove us as listener

		if (this.endpoint != null) this.endpoint.removeXdiGraphListener(this);
	}

	private void refresh() {

		try {

			this.button.setText(this.getName());
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	public XRI3Segment xdiMainAddress() {
		
		return this.contextNodeXri;
	}
	
	public XRI3Segment[] xdiGetAddresses() {

		return new XRI3Segment[] {
				this.contextNodeXri
		};
	}

	public XRI3Segment[] xdiAddAddresses() {

		return new XRI3Segment[0];
	}

	public XRI3Segment[] xdiModAddresses() {

		return new XRI3Segment[] {
				this.contextNodeXri
		};
	}

	public XRI3Segment[] xdiDelAddresses() {

		return new XRI3Segment[] {
				this.contextNodeXri
		};
	}

	public void onXdiGraphEvent(XdiGraphEvent xdiGraphEvent) {

		try {

			if (xdiGraphEvent instanceof XdiGraphAddEvent) {

				this.refresh();
				return;
			}

			if (xdiGraphEvent instanceof XdiGraphModEvent) {

				this.refresh();
				return;
			}

			if (xdiGraphEvent instanceof XdiGraphDelEvent) {

				this.getParent().remove(this);
				return;
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	public void setEndpointAndContextNodeXri(XdiEndpoint endpoint, XRI3Segment contextNodeXri) {

		// remove us as listener

		if (this.endpoint != null) this.endpoint.removeXdiGraphListener(this);

		// refresh

		this.endpoint = endpoint;
		this.contextNodeXri = contextNodeXri;

		this.refresh();

		// add us as listener

		this.endpoint.addXdiGraphListener(this);
	}

	private void onButtonActionPerformed(ActionEvent e) {

		AccountPersonaWindowPane accountPersonaWindowPane = new AccountPersonaWindowPane();
		accountPersonaWindowPane.setEndpointAndContextNodeXri(this.endpoint, this.contextNodeXri);

		MainWindow.findMainContentPane(this).add(accountPersonaWindowPane);
	}

	private String getName() throws Xdi2ClientException {

		XRI3Segment nameAddress = new XRI3Segment("" + this.contextNodeXri + "$!(+name)");
		
		Message message = this.endpoint.prepareMessage();
		message.createGetOperation(nameAddress);

		MessageResult messageResult = this.endpoint.send(message);

		Literal literal = messageResult.getGraph().findLiteral(nameAddress);
		if (literal == null) return null;

		return literal.getLiteralData();
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setInsets(new Insets(new Extent(10, Extent.PX)));
		button = new Button();
		button.setStyleName("PlainWhite");
		ResourceImageReference imageReference1 = new ResourceImageReference(
				"/pds/web/resource/image/accountpersona.png");
		button.setIcon(imageReference1);
		button.setText("...");
		button.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onButtonActionPerformed(e);
			}
		});
		add(button);
	}
}
