package pds.web.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import nextapp.echo.app.Button;
import nextapp.echo.app.Column;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.Panel;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.TextField;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.constants.DictionaryConstants;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.dictionary.Dictionary;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.util.iterators.IteratorListMaker;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3SubSegment;

import pds.web.events.ApplicationContextClosedEvent;
import pds.web.events.ApplicationEvent;
import pds.web.events.ApplicationListener;
import pds.xdi.XdiContext;
import pds.xdi.XdiException;
import pds.xdi.XdiUtil;
import pds.xdi.events.XdiGraphAddEvent;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import pds.xdi.events.XdiGraphModEvent;

public class AccountRootGrid extends Grid implements ApplicationListener, XdiGraphListener {

	private static final long serialVersionUID = 7130946736971438102L;

	protected ResourceBundle resourceBundle;

	private XdiContext context;
	private XRI3 address;
	private XRI3 extensionAddress;

	private Button addAccountPersonaButton;
	private Panel addAccountPersonaPanel;
	private TextField addAccountPersonaTextField;

	/**
	 * Creates a new <code>AccountRootGrid</code>.
	 */
	public AccountRootGrid() {
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

			List<XRI3Segment> accountPersonaXris = this.getAccountPersonaXris();

			this.removeAll();
			this.add(this.addAccountPersonaButton);
			this.add(this.addAccountPersonaPanel);

			for (XRI3Segment accountPersonaXri : accountPersonaXris) {

				this.addAccountPersonaPanel(accountPersonaXri);
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	public XRI3[] xdiGetAddresses() {

		return new XRI3[] {
				this.extensionAddress
		};
	}

	public XRI3[] xdiAddAddresses() {

		return new XRI3[] {
				new XRI3("" + this.extensionAddress + "/$$")
		};
	}

	public XRI3[] xdiModAddresses() {

		return new XRI3[0];
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

	private void addAccountPersonaPanel(final XRI3Segment accountPersonaXri) {

		this.remove(this.addAccountPersonaButton);

		AccountPersonaPanel accountPersonaPanel = new AccountPersonaPanel();
		accountPersonaPanel.setContextAndSubjectXri(this.context, accountPersonaXri);

		this.add(accountPersonaPanel);

		this.add(this.addAccountPersonaButton);
	}

	public void setContext(XdiContext context) {

		// remove us as listener

		if (this.context != null) this.context.removeXdiGraphListener(this);

		// refresh

		this.context = context;
		this.address = new XRI3("" + this.context.getCanonical());
		this.extensionAddress = new XRI3("" + this.context.getCanonical() + "/" + DictionaryConstants.XRI_EXTENSION);

		this.refresh();

		// add us as listener

		this.context.addXdiGraphListener(this);
	}

	public void onApplicationEvent(ApplicationEvent applicationEvent) {

		if (applicationEvent instanceof ApplicationContextClosedEvent) {

			this.removeAll();
			this.add(this.addAccountPersonaButton);
		}
	}

	private void onAddAccountPersonaActionPerformed(ActionEvent e) {

		this.addAccountPersonaButton.setVisible(false);
		this.addAccountPersonaPanel.setVisible(true);
		this.addAccountPersonaTextField.setText("Home");
	}

	private void onCreateActionPerformed(ActionEvent e) {

		try {

			this.addAccountPersona(this.addAccountPersonaTextField.getText());
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while creating the Account Persona: " + ex.getMessage(), ex);
			return;
		}

		this.addAccountPersonaButton.setVisible(true);
		this.addAccountPersonaPanel.setVisible(false);
	}

	private void onCancelActionPerformed(ActionEvent e) {

		this.addAccountPersonaButton.setVisible(true);
		this.addAccountPersonaPanel.setVisible(false);
	}

	public List<XRI3Segment> getAccountPersonaXris() throws XdiException {

		// $get

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, this.extensionAddress);
		MessageResult messageResult = this.context.send(operation);

		Subject subject = messageResult.getGraph().getSubject(this.context.getCanonical());
		if (subject == null) return new ArrayList<XRI3Segment> ();

		Iterator<XRI3Segment> accountPersonaXris = Dictionary.getSubjectExtensions(subject);
		return new IteratorListMaker<XRI3Segment> (accountPersonaXris).list();
	}

	private void addAccountPersona(String name) throws XdiException {

		XRI3SubSegment accountPersonaSubSegment = XdiUtil.randomSubSegment();
		XRI3Segment accountPersonaXri = new XRI3Segment("" + this.context.getCanonical() + accountPersonaSubSegment);

		// $add

		Message message = this.context.prepareMessage();
		Operation operation = message.createAddOperation();
		Graph operationGraph = operation.createOperationGraph(null);
		operationGraph.createStatement(this.context.getCanonical(), DictionaryConstants.XRI_EXTENSION, new XRI3Segment("" + accountPersonaSubSegment));
		operationGraph.createStatement(accountPersonaXri, new XRI3Segment("$a$xsd$string"), name);

		this.context.send(message);
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setOrientation(Grid.ORIENTATION_HORIZONTAL);
		this.setInsets(new Insets(new Extent(0, Extent.PX)));
		this.setSize(5);
		addAccountPersonaButton = new Button();
		addAccountPersonaButton.setStyleName("PlainWhite");
		ResourceImageReference imageReference1 = new ResourceImageReference(
		"/pds/web/resource/image/accountpersonanew.png");
		addAccountPersonaButton.setIcon(imageReference1);
		addAccountPersonaButton.setText("Create New");
		addAccountPersonaButton
		.setInsets(new Insets(new Extent(10, Extent.PX)));
		addAccountPersonaButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onAddAccountPersonaActionPerformed(e);
			}
		});
		add(addAccountPersonaButton);
		addAccountPersonaPanel = new Panel();
		addAccountPersonaPanel.setVisible(false);
		add(addAccountPersonaPanel);
		Column column1 = new Column();
		column1.setCellSpacing(new Extent(5, Extent.PX));
		addAccountPersonaPanel.add(column1);
		Label label1 = new Label();
		label1.setStyleName("Default");
		label1.setText("Enter a name for the new Persona (e.g. \"Home\"):");
		column1.add(label1);
		Row row1 = new Row();
		row1.setCellSpacing(new Extent(10, Extent.PX));
		column1.add(row1);
		addAccountPersonaTextField = new TextField();
		addAccountPersonaTextField.setStyleName("Default");
		row1.add(addAccountPersonaTextField);
		Button button1 = new Button();
		button1.setStyleName("Default");
		button1.setText("Create");
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onCreateActionPerformed(e);
			}
		});
		row1.add(button1);
		Button button2 = new Button();
		button2.setStyleName("Default");
		button2.setText("Cancel");
		button2.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onCancelActionPerformed(e);
			}
		});
		row1.add(button2);
	}
}
