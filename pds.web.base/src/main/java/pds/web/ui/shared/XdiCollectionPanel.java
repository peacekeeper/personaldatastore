package pds.web.ui.shared;

import java.util.Iterator;
import java.util.ResourceBundle;

import nextapp.echo.app.Button;
import nextapp.echo.app.Column;
import nextapp.echo.app.Component;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Label;
import nextapp.echo.app.Panel;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.TextField;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.RowLayoutData;
import pds.web.components.xdi.XdiPanel;
import pds.web.ui.MainWindow;
import pds.web.ui.MessageDialog;
import pds.xdi.XdiEndpoint;
import pds.xdi.events.XdiGraphAddEvent;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import pds.xdi.events.XdiGraphModEvent;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.ContextNode;
import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.features.multiplicity.XdiAttribute;
import xdi2.core.features.multiplicity.XdiAttributeMember;
import xdi2.core.features.multiplicity.XdiCollection;
import xdi2.core.util.StatementUtil;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;

public class XdiCollectionPanel extends Panel implements XdiGraphListener {

	private static final long serialVersionUID = -5082464847478633075L;

	protected ResourceBundle resourceBundle;

	private XdiEndpoint endpoint;
	private XdiCollection xdiCollection;
	private XRI3Segment xdiCollectionXri;

	private boolean readOnly;

	private XdiPanel xdiPanel;
	private Label xdiCollectionXriLabel;
	private Column xdiAttributesColumn;
	private TextField addTextField;
	private Button cancelButton;
	private Button addButton;

	/**
	 * Creates a new <code>AccountPersonaPanel</code>.
	 */
	public XdiCollectionPanel() {
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

			if (this.xdiCollection == null) this.xdiGet();

			// refresh UI

			this.xdiPanel.setEndpointAndGraphListener(this.endpoint, this);
			this.xdiCollectionXriLabel.setText(this.xdiCollection.getContextNode().getArcXri().toString());

			this.xdiAttributesColumn.removeAll();

			for (Iterator<XdiAttributeMember> xdiAttributeMembers = this.xdiCollection.attributes(); xdiAttributeMembers.hasNext(); ) {

				XdiAttributeMember xdiAttributeMember = xdiAttributeMembers.next();

				this.addXdiAttributePanel(xdiAttributeMember);
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	private void xdiGet() throws Xdi2ClientException {

		// $get

		Message message = this.endpoint.prepareMessage();
		message.createGetOperation(this.xdiCollectionXri);

		MessageResult messageResult = this.endpoint.send(message);

		ContextNode contextNode = messageResult.getGraph().findContextNode(this.xdiCollectionXri, false);
		if (contextNode == null) this.xdiCollection = null;

		this.xdiCollection = XdiCollection.fromContextNode(contextNode);
	}

	private void xdiAdd(String value) throws Xdi2ClientException {

		// $add

		XRI3Segment xdiAttributeMemberXri = new XRI3Segment("" + this.xdiCollectionXri + Multiplicity.attributeMemberArcXri());

		Message message = this.endpoint.prepareMessage();
		message.createAddOperation(StatementUtil.fromLiteralComponents(xdiAttributeMemberXri, value));

		this.endpoint.send(message);
	}

	public XRI3Segment xdiMainAddress() {

		return this.xdiCollection.getContextNode().getXri();
	}

	public XRI3Segment[] xdiGetAddresses() {

		return new XRI3Segment[] {
				this.xdiCollection.getContextNode().getXri()
		};
	}

	public XRI3Segment[] xdiAddAddresses() {

		return new XRI3Segment[] {
				this.xdiCollection.getContextNode().getXri()
		};
	}

	public XRI3Segment[] xdiModAddresses() {

		return new XRI3Segment[0];
	}

	public XRI3Segment[] xdiDelAddresses() {

		return new XRI3Segment[] {
				this.xdiCollection.getContextNode().getXri()
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

	public void setEndpointAndXdiCollection(XdiEndpoint endpoint, XdiCollection xdiCollection, XRI3Segment xdiCollectionXri) {

		// remove us as listener

		if (this.endpoint != null) this.endpoint.removeXdiGraphListener(this);

		// refresh

		this.endpoint = endpoint;
		this.xdiCollection = xdiCollection;
		this.xdiCollectionXri = xdiCollectionXri;

		this.refresh();

		// add us as listener

		this.endpoint.addXdiGraphListener(this);
	}

	public void setReadOnly(boolean readOnly) {

		if (readOnly && (! this.readOnly)) {

			this.addTextField.setVisible(false);
			this.addButton.setVisible(false);
			this.cancelButton.setVisible(false);
		} else if ((! readOnly) && this.readOnly){

			this.addTextField.setVisible(false);
			this.addButton.setVisible(true);
			this.cancelButton.setVisible(false);
		}

		this.readOnly = readOnly;

		for (Component component : MainWindow.findChildComponentsByClass(this, XdiAttributePanel.class)) {

			((XdiAttributePanel) component).setReadOnly(readOnly);
		}
	}

	private void addXdiAttributePanel(XdiAttribute xdiAttribute) {

		XdiAttributePanel xdiAttributePanel = new XdiAttributePanel();
		xdiAttributePanel.setEndpointAndXdiAttribute(this.endpoint, xdiAttribute, xdiAttribute.getContextNode().getXri());
		xdiAttributePanel.setReadOnly(this.readOnly);

		this.xdiAttributesColumn.add(xdiAttributePanel);
	}

	private void onAddActionPerformed(ActionEvent e) {

		if (this.addTextField.isVisible()) {

			String value = this.addTextField.getText();
			if (value == null || value.trim().equals("")) return;

			try {

				this.xdiAdd(value);
			} catch (Exception ex) {

				MessageDialog.problem("Sorry, a problem occurred while storing your Personal Data: " + ex.getMessage(), ex);
				return;
			}

			this.addTextField.setVisible(false);
			this.cancelButton.setVisible(false);
		} else {

			this.addTextField.setText("");

			this.addTextField.setVisible(true);
			this.cancelButton.setVisible(true);
		}
	}

	private void onCancelActionPerformed(ActionEvent e) {

		this.addTextField.setVisible(false);
		this.cancelButton.setVisible(false);
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		Column column1 = new Column();
		column1.setCellSpacing(new Extent(10, Extent.PX));
		add(column1);
		Row row2 = new Row();
		row2.setCellSpacing(new Extent(10, Extent.PX));
		column1.add(row2);
		xdiPanel = new XdiPanel();
		row2.add(xdiPanel);
		xdiCollectionXriLabel = new Label();
		xdiCollectionXriLabel.setStyleName("Bold");
		xdiCollectionXriLabel.setText("...");
		RowLayoutData xdiCollectionXriLabelLayoutData = new RowLayoutData();
		xdiCollectionXriLabelLayoutData.setWidth(new Extent(120, Extent.PX));
		xdiCollectionXriLabel.setLayoutData(xdiCollectionXriLabelLayoutData);
		row2.add(xdiCollectionXriLabel);
		addTextField = new TextField();
		addTextField.setStyleName("Default");
		addTextField.setVisible(false);
		addTextField.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onAddActionPerformed(e);
			}
		});
		row2.add(addTextField);
		addButton = new Button();
		addButton.setStyleName("Plain");
		ResourceImageReference imageReference1 = new ResourceImageReference(
				"/pds/web/resource/image/op-add.png");
		addButton.setIcon(imageReference1);
		addButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onAddActionPerformed(e);
			}
		});
		row2.add(addButton);
		cancelButton = new Button();
		cancelButton.setStyleName("Plain");
		ResourceImageReference imageReference2 = new ResourceImageReference(
				"/pds/web/resource/image/op-cancel.png");
		cancelButton.setIcon(imageReference2);
		cancelButton.setVisible(false);
		cancelButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onCancelActionPerformed(e);
			}
		});
		row2.add(cancelButton);
		xdiAttributesColumn = new Column();
		column1.add(xdiAttributesColumn);
	}
}
