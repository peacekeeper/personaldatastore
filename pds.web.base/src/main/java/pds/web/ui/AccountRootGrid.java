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
import pds.web.PDSApplication;
import pds.web.events.ApplicationContextClosedEvent;
import pds.web.events.ApplicationContextOpenedEvent;
import pds.web.events.ApplicationEvent;
import pds.web.events.ApplicationListener;
import pds.xdi.XdiEndpoint;
import pds.xdi.events.XdiGraphAddEvent;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import pds.xdi.events.XdiGraphModEvent;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.ContextNode;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingContextNodeXrisIterator;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;

public class AccountRootGrid extends Grid implements ApplicationListener, XdiGraphListener {

	private static final long serialVersionUID = 7130946736971438102L;

	protected ResourceBundle resourceBundle;

	private XdiEndpoint endpoint;
	private XRI3Segment contextNodeXri;

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

		// add us as listener

		PDSApplication.getApp().addApplicationListener(this);
	}

	@Override
	public void dispose() {

		super.dispose();

		// remove us as listener

		PDSApplication.getApp().removeApplicationListener(this);
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

	public XRI3Segment xdiMainAddress() {
		
		return this.contextNodeXri;
	}
	
	public XRI3Segment[] xdiGetAddresses() {

		return new XRI3Segment[] {
				this.contextNodeXri
		};
	}

	public XRI3Segment[] xdiAddAddresses() {

		return new XRI3Segment[] {
				this.contextNodeXri
		};
	}

	public XRI3Segment[] xdiModAddresses() {

		return new XRI3Segment[0];
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

	private void addAccountPersonaPanel(final XRI3Segment accountPersonaXri) {

		this.remove(this.addAccountPersonaButton);

		AccountPersonaPanel accountPersonaPanel = new AccountPersonaPanel();
		accountPersonaPanel.setEndpointAndContextNodeXri(this.endpoint, accountPersonaXri);

		this.add(accountPersonaPanel);

		this.add(this.addAccountPersonaButton);
	}

	public void onApplicationEvent(ApplicationEvent applicationEvent) {

		if (applicationEvent instanceof ApplicationContextClosedEvent) {

			this.removeAll();
			this.add(this.addAccountPersonaButton);

			this.addAccountPersonaButton.setVisible(true);
			this.addAccountPersonaPanel.setVisible(false);

			// remove us as listener

			if (this.endpoint != null) this.endpoint.removeXdiGraphListener(this);
		}

		if (applicationEvent instanceof ApplicationContextOpenedEvent) {

			// refresh

			this.endpoint = ((ApplicationContextOpenedEvent) applicationEvent).getEndpoint();
			this.contextNodeXri = new XRI3Segment("" + this.endpoint.getCanonical());

			this.refresh();

			// add us as listener

			this.endpoint.addXdiGraphListener(this);
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

	public List<XRI3Segment> getAccountPersonaXris() throws Xdi2ClientException {

		// $get

		Message message = this.endpoint.prepareMessage();
		message.createGetOperation(this.contextNodeXri);

		MessageResult messageResult = this.endpoint.send(message);

		ContextNode contextNode = messageResult.getGraph().findContextNode(this.endpoint.getCanonical(), false);
		if (contextNode == null) return new ArrayList<XRI3Segment> ();

		Iterator<XRI3Segment> accountPersonaXris = new MappingContextNodeXrisIterator(contextNode.getContextNodes());
		return new IteratorListMaker<XRI3Segment> (accountPersonaXris).list();
	}

	private void addAccountPersona(String name) throws Xdi2ClientException {

		XRI3Segment accountPersonaXri = new XRI3Segment("" + this.endpoint.getCanonical() + "$($)");

		// $add

		Message message = this.endpoint.prepareMessage();
		message.createAddOperation(StatementUtil.fromLiteralComponents(accountPersonaXri, name));

		this.endpoint.send(message);
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
		addAccountPersonaTextField.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onCreateActionPerformed(e);
			}
		});
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
