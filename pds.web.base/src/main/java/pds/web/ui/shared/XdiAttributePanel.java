package pds.web.ui.shared;

import java.util.ResourceBundle;

import nextapp.echo.app.Button;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.Panel;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.TextField;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.RowLayoutData;
import pds.web.components.xdi.XdiPanel;
import pds.web.ui.MessageDialog;
import pds.xdi.XdiEndpoint;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import pds.xdi.events.XdiGraphModEvent;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.connector.facebook.mapping.FacebookMapping;
import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.features.multiplicity.XdiAttribute;
import xdi2.core.util.StatementUtil;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;

public class XdiAttributePanel extends Panel implements XdiGraphListener {

	private static final long serialVersionUID = -5082464847478633075L;

	protected ResourceBundle resourceBundle;

	private XdiEndpoint endpoint;
	private XdiAttribute xdiAttribute;
	private XRI3Segment xdiAttributeXri;
	private String label;

	private boolean readOnly;

	private XdiPanel xdiPanel;
	private Label xdiAttributeXriLabel;
	private Label valueLabel;
	private TextField valueTextField;
	private Button editButton;
	private Button updateButton;
	private Button deleteButton;

	/**
	 * Creates a new <code>AccountPersonaPanel</code>.
	 */
	public XdiAttributePanel() {
		super();

		this.readOnly = false;

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

			// refresh data

			if (this.xdiAttribute == null) this.xdiGet();

			// refresh UI

			Literal literal = this.xdiAttribute.getContextNode().getLiteral();
			String value = literal == null ? null : literal.getLiteralData();

			this.xdiPanel.setEndpointAndGraphListener(this.endpoint, this);
			this.xdiAttributeXriLabel.setText(this.label);
			this.valueLabel.setText(value);
			this.valueTextField.setText(value);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	private void xdiGet() throws Xdi2ClientException {

		// $get

		Message message = this.endpoint.prepareMessage();
		message.createGetOperation(this.xdiAttributeXri);

		MessageResult messageResult = this.endpoint.send(message);

		ContextNode contextNode = messageResult.getGraph().findContextNode(this.xdiAttributeXri, false);
		if (contextNode == null) this.xdiAttribute = null;

		this.xdiAttribute = XdiAttribute.fromContextNode(contextNode);
	}

	private void xdiAdd(String value) throws Xdi2ClientException {

		// $add

		XRI3Segment contextNodeXri = this.xdiAttribute.getContextNode().getXri();

		Message message = this.endpoint.prepareMessage();
		message.createAddOperation(StatementUtil.fromLiteralComponents(contextNodeXri, value));

		this.endpoint.send(message);
	}

	private void xdiMod(String value) throws Xdi2ClientException {

		// $mod

		XRI3Segment contextNodeXri = this.xdiAttribute.getContextNode().getXri();

		Message message = this.endpoint.prepareMessage();
		message.createModOperation(StatementUtil.fromLiteralComponents(contextNodeXri, value));

		this.endpoint.send(message);
	}

	private void xdiDel() throws Xdi2ClientException {

		// $del

		XRI3Segment contextNodeXri = this.xdiAttribute.getContextNode().getXri();

		Message message = this.endpoint.prepareMessage();
		message.createDelOperation(contextNodeXri);

		this.endpoint.send(message);
	}

	public XRI3Segment xdiMainAddress() {

		return this.xdiAttribute.getContextNode().getXri();
	}

	public XRI3Segment[] xdiGetAddresses() {

		return new XRI3Segment[] {
				this.xdiAttribute.getContextNode().getXri()
		};
	}

	public XRI3Segment[] xdiAddAddresses() {

		return new XRI3Segment[0];
	}

	public XRI3Segment[] xdiModAddresses() {

		return new XRI3Segment[] {
				this.xdiAttribute.getContextNode().getXri()
		};
	}

	public XRI3Segment[] xdiDelAddresses() {

		return new XRI3Segment[] {
				this.xdiAttribute.getContextNode().getXri()
		};
	}

	public void onXdiGraphEvent(XdiGraphEvent xdiGraphEvent) {

		try {

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

	public void setEndpointAndXdiAttribute(XdiEndpoint endpoint, XdiAttribute xdiAttribute, XRI3Segment xdiAttributeXri, String label) {

		// remove us as listener

		if (this.endpoint != null) this.endpoint.removeXdiGraphListener(this);

		// refresh

		this.endpoint = endpoint;
		this.xdiAttribute = xdiAttribute;
		this.xdiAttributeXri = xdiAttributeXri;
		this.label = label;

		this.refresh();

		// add us as listener

		this.endpoint.addXdiGraphListener(this);
	}

	public void setReadOnly(boolean readOnly) {

		if (readOnly && (! this.readOnly)) {

			this.valueLabel.setVisible(true);
			this.valueTextField.setVisible(false);
			this.editButton.setVisible(false);
			this.updateButton.setVisible(false);
			this.deleteButton.setVisible(false);
		} else if ((! readOnly) && this.readOnly){

			this.valueLabel.setVisible(true);
			this.valueTextField.setVisible(false);
			this.editButton.setVisible(true);
			this.updateButton.setVisible(false);
			this.deleteButton.setVisible(true);
		}

		this.readOnly = readOnly;
	}

	private boolean needAdd = false;
	
	private void onEditActionPerformed(ActionEvent e) {

		this.needAdd = this.valueLabel.getText() == null;
		
		this.valueTextField.setText(this.valueLabel.getText());

		this.valueLabel.setVisible(false);
		this.valueTextField.setVisible(true);
		this.editButton.setVisible(false);
		this.updateButton.setVisible(true);
	}

	private void onUpdateActionPerformed(ActionEvent e) {

		try {

			if (this.needAdd)
				this.xdiAdd(this.valueTextField.getText());
			else
				this.xdiMod(this.valueTextField.getText());
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while storing your Personal Data: " + ex.getMessage(), ex);
			return;
		}

		this.valueLabel.setVisible(true);
		this.valueTextField.setVisible(false);
		this.editButton.setVisible(true);
		this.updateButton.setVisible(false);
	}

	private void onDeleteActionPerformed(ActionEvent e) {

		if (this.valueLabel.isVisible()) {

			try {

				this.xdiDel();
			} catch (Exception ex) {

				MessageDialog.problem("Sorry, a problem occurred while storing your Personal Data: " + ex.getMessage(), ex);
				return;
			}
		} else {

			this.valueLabel.setVisible(true);
			this.valueTextField.setVisible(false);
			this.editButton.setVisible(true);
			this.updateButton.setVisible(false);
		}
	}

	private void onLinkFacebookActionPerformed(ActionEvent e) {

		FacebookMapping.getInstance();
		//TODO Implement.
	}

	private void onLinkPersonalActionPerformed(ActionEvent e) {
		//TODO Implement.
	}

	private void onLinkAllfiledActionPerformed(ActionEvent e) {
		//TODO Implement.
	}

	private void onUnlinkActionPerformed(ActionEvent e) {
		//TODO Implement.
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setInsets(new Insets(new Extent(30, Extent.PX), new Extent(0,
				Extent.PX), new Extent(0, Extent.PX), new Extent(5, Extent.PX)));
		Row row1 = new Row();
		row1.setCellSpacing(new Extent(10, Extent.PX));
		add(row1);
		xdiPanel = new XdiPanel();
		row1.add(xdiPanel);
		xdiAttributeXriLabel = new Label();
		xdiAttributeXriLabel.setStyleName("Bold");
		xdiAttributeXriLabel.setText("...");
		RowLayoutData xdiAttributeXriLabelLayoutData = new RowLayoutData();
		xdiAttributeXriLabelLayoutData.setWidth(new Extent(120, Extent.PX));
		xdiAttributeXriLabel.setLayoutData(xdiAttributeXriLabelLayoutData);
		row1.add(xdiAttributeXriLabel);
		valueLabel = new Label();
		valueLabel.setStyleName("Default");
		valueLabel.setText("...");
		row1.add(valueLabel);
		valueTextField = new TextField();
		valueTextField.setStyleName("Default");
		valueTextField.setVisible(false);
		valueTextField.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onUpdateActionPerformed(e);
			}
		});
		row1.add(valueTextField);
		editButton = new Button();
		editButton.setStyleName("Plain");
		ResourceImageReference imageReference1 = new ResourceImageReference(
				"/pds/web/resource/image/op-edit.png");
		editButton.setIcon(imageReference1);
		editButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onEditActionPerformed(e);
			}
		});
		row1.add(editButton);
		updateButton = new Button();
		updateButton.setStyleName("Plain");
		ResourceImageReference imageReference2 = new ResourceImageReference(
				"/pds/web/resource/image/op-ok.png");
		updateButton.setIcon(imageReference2);
		updateButton.setVisible(false);
		updateButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onUpdateActionPerformed(e);
			}
		});
		row1.add(updateButton);
		deleteButton = new Button();
		deleteButton.setStyleName("Plain");
		ResourceImageReference imageReference3 = new ResourceImageReference(
				"/pds/web/resource/image/op-cancel.png");
		deleteButton.setIcon(imageReference3);
		deleteButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onDeleteActionPerformed(e);
			}
		});
		row1.add(deleteButton);
		Button button1 = new Button();
		button1.setStyleName("Default");
		ResourceImageReference imageReference4 = new ResourceImageReference(
				"/pds/web/resource/image/connect-facebook.png");
		button1.setIcon(imageReference4);
		button1.setText("Link to Facebook");
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onLinkFacebookActionPerformed(e);
			}
		});
		row1.add(button1);
		Button button2 = new Button();
		button2.setStyleName("Default");
		ResourceImageReference imageReference5 = new ResourceImageReference(
				"/pds/web/resource/image/connect-personal.png");
		button2.setIcon(imageReference5);
		button2.setText("Link to Personal.com");
		button2.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onLinkPersonalActionPerformed(e);
			}
		});
		row1.add(button2);
		Button button4 = new Button();
		button4.setStyleName("Default");
		ResourceImageReference imageReference6 = new ResourceImageReference(
				"/pds/web/resource/image/connect-allfiled.png");
		button4.setIcon(imageReference6);
		button4.setText("Link to Allfiled");
		button4.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onLinkAllfiledActionPerformed(e);
			}
		});
		row1.add(button4);
		Button button3 = new Button();
		button3.setStyleName("Default");
		button3.setText("Unlink");
		button3.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onUnlinkActionPerformed(e);
			}
		});
		row1.add(button3);
	}
}
