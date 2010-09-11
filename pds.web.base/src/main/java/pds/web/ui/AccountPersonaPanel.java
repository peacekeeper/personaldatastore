package pds.web.ui;

import java.util.ResourceBundle;

import nextapp.echo.app.Button;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Panel;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import org.eclipse.higgins.xdi4j.addressing.Addressing;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.ui.accountpersona.AccountPersonaWindowPane;
import pds.xdi.XdiContext;
import pds.xdi.XdiException;
import pds.xdi.events.XdiGraphAddEvent;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import pds.xdi.events.XdiGraphModEvent;

public class AccountPersonaPanel extends Panel implements XdiGraphListener {

	private static final long serialVersionUID = -5082464847478633075L;

	protected ResourceBundle resourceBundle;

	private XdiContext context;
	private XRI3Segment subjectXri;
	private XRI3 address;
	private XRI3 nameAddress;

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
		
		if (this.context != null) this.context.removeXdiGraphListener(this);
	}

	private void refresh() {

		try {

			this.button.setText(this.getName());
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	public XRI3[] xdiGetAddresses() {

		return new XRI3[] {
				this.nameAddress
		};
	}

	public XRI3[] xdiAddAddresses() {

		return new XRI3[0];
	}

	public XRI3[] xdiModAddresses() {

		return new XRI3[] {
				new XRI3("" + this.nameAddress + "/$$")
		};
	}

	public XRI3[] xdiDelAddresses() {

		return new XRI3[] {
				this.address
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

	public void setContextAndSubjectXri(XdiContext context, XRI3Segment subjectXri) {

		// remove us as listener
		
		if (this.context != null) this.context.removeXdiGraphListener(this);

		// refresh
		
		this.context = context;
		this.subjectXri = subjectXri;
		this.address = new XRI3("" + this.subjectXri);
		this.nameAddress = new XRI3("" + this.subjectXri + "/$a$string");

		this.refresh();

		// add us as listener

		this.context.addXdiGraphListener(this);
	}

	private void onButtonActionPerformed(ActionEvent e) {

		AccountPersonaWindowPane accountPersonaWindowPane = new AccountPersonaWindowPane();
		accountPersonaWindowPane.setContextAndSubjectXri(this.context, this.subjectXri);

		MainWindow.findMainContentPane(this).add(accountPersonaWindowPane);
	}

	private String getName() throws XdiException {

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, this.nameAddress);
		MessageResult messageResult = this.context.send(operation);

		return Addressing.findLiteralData(messageResult.getGraph(), this.nameAddress);
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
