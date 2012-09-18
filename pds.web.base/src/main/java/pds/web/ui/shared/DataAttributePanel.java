package pds.web.ui.shared;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import pds.xdi.XdiException;
import pds.xdi.events.XdiGraphAddEvent;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import pds.xdi.events.XdiGraphModEvent;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.dictionary.Dictionary;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.constants.XDIMessagingConstants;

public class DataAttributePanel extends Panel implements XdiGraphListener {

	private static final long serialVersionUID = -5082464847478633075L;

	protected ResourceBundle resourceBundle;

	private XdiEndpoint endpoint;
	private XRI3Segment contextNodeXri;
	private XRI3Segment attributeXri;
	private XRI3Segment address;
	private XRI3Segment extensionAddress;
	private XRI3Segment canonicalAddress;
	private XRI3Segment addAddress;

	private boolean readOnly;

	private XdiPanel xdiPanel;
	private Label attributeXriLabel;
	private TextField addTextField;
	private Column instanceValuesColumn;
	private Button cancelButton;
	private Button addButton;

	/**
	 * Creates a new <code>AccountPersonaPanel</code>.
	 */
	public DataAttributePanel() {
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

			this.xdiPanel.setEndpointAndMainAddressAndGetAddresses(this.endpoint, this.address, this.xdiGetAddresses());
			this.attributeXriLabel.setText(this.attributeXri.toString());

			this.instanceValuesColumn.removeAll();
			List<XRI3Segment> dataAttributeInstanceXris = this.getDataAttributeInstanceXris();

			for (XRI3Segment dataAttributeInstanceXri : dataAttributeInstanceXris) {

				this.addDataAttributeInstancePanel(dataAttributeInstanceXri);
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	private void addDataAttributeInstancePanel(XRI3Segment attributeInstanceXri) {

		DataAttributeInstancePanel dataAttributeInstancePanel = new DataAttributeInstancePanel();
		dataAttributeInstancePanel.setEndpointAndContextNodeXriAndAttributeXri(this.endpoint, this.contextNodeXri, attributeInstanceXri);
		dataAttributeInstancePanel.setReadOnly(this.readOnly);

		this.instanceValuesColumn.add(dataAttributeInstancePanel);
	}

	public XRI3Segment[] xdiGetAddresses() {

		return new XRI3Segment[] {
				this.address,
				this.extensionAddress,
				this.canonicalAddress
		};
	}

	public XRI3Segment[] xdiAddAddresses() {

		return new XRI3Segment[] {
				this.addAddress
		};
	}

	public XRI3Segment[] xdiModAddresses() {

		return new XRI3Segment[0];
	}

	public XRI3Segment[] xdiSetAddresses() {

		return new XRI3Segment[0];
	}

	public XRI3Segment[] xdiDelAddresses() {

		return new XRI3Segment[] {
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

	public void setEndpointAndContextNodeXriAndAttributeXri(XdiEndpoint endpoint, XRI3Segment contextNodeXri, XRI3Segment attributeXri) {

		// remove us as listener

		if (this.endpoint != null) this.endpoint.removeXdiGraphListener(this);

		// refresh

		this.endpoint = endpoint;
		this.contextNodeXri = contextNodeXri;
		this.attributeXri = attributeXri;
		this.address = new XRI3Segment("" + this.contextNodeXri + this.attributeXri);
		this.extensionAddress = new XRI3Segment("" + this.contextNodeXri + Dictionary.makeExtensionPredicate(this.attributeXri));
		this.canonicalAddress = new XRI3Segment("" + this.contextNodeXri + Dictionary.makeCanonicalPredicate(this.attributeXri));
		this.addAddress = new XRI3Segment("" + this.contextNodeXri + this.attributeXri + "$($)");

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

		for (Component component : MainWindow.findChildComponentsByClass(this, DataAttributeInstancePanel.class)) {

			((DataAttributeInstancePanel) component).setReadOnly(readOnly);
		}
	}

	private void onAddActionPerformed(ActionEvent e) {

		if (this.addTextField.isVisible()) {

			String value = this.addTextField.getText();
			if (value == null || value.trim().equals("")) return;

			try {

				this.addDataPredicateInstance(value);
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

	private List<XRI3Segment> getDataAttributeInstanceXris() throws XdiException {

		// $get

		Message message = this.endpoint.prepareOperation(XDIMessagingConstants.XRI_S_GET, this.extensionAddress);
		MessageResult messageResult = this.endpoint.send(message);

		ContextNode contextNode = messageResult.getGraph().findContextNode(this.contextNodeXri, false);
		if (contextNode == null) return new ArrayList<XRI3Segment> ();

		Iterator<XRI3Segment> dataAttributeInstanceXris = Dictionary.getPredicateExtensions(contextNode, this.attributeXri);
		return new IteratorListMaker<XRI3Segment> (dataAttributeInstanceXris).list();
	}

	private void addDataPredicateInstance(final String value) throws XdiException {

		XRI3Segment dataPredicateInstanceAddXri = new XRI3Segment("" + this.attributeXri + "$($)");
		XRI3Segment dataPredicateInstanceSetXri = new XRI3Segment("$" + this.attributeXri);

		// $add and $set

		Message message = this.endpoint.prepareMessage();
		Operation operation = message.createOperation(MessagingConstants.XRI_ADD);
		Graph operationGraph = operation.createOperationGraph(null);
		operationGraph.createStatement(this.contextNodeXri, dataPredicateInstanceAddXri, value);
		Operation operation2 = message.createOperation(MessagingConstants.XRI_SET);
		Graph operationGraph2 = operation2.createOperationGraph(null);
		operationGraph2.createStatement(this.contextNodeXri, dataPredicateInstanceSetXri, value);
		this.endpoint.send(operation);
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
		attributeXriLabel = new Label();
		attributeXriLabel.setStyleName("Bold");
		attributeXriLabel.setText("...");
		RowLayoutData predicateXriLabelLayoutData = new RowLayoutData();
		predicateXriLabelLayoutData.setWidth(new Extent(120, Extent.PX));
		attributeXriLabel.setLayoutData(predicateXriLabelLayoutData);
		row2.add(attributeXriLabel);
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
		instanceValuesColumn = new Column();
		column1.add(instanceValuesColumn);
	}
}
