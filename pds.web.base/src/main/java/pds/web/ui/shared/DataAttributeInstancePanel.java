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
import pds.web.components.xdi.XdiPanel;
import pds.web.ui.MessageDialog;
import pds.xdi.XdiEndpoint;
import pds.xdi.XdiException;
import pds.xdi.XdiNotExistentException;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import pds.xdi.events.XdiGraphModEvent;
import xdi2.core.Literal;
import xdi2.core.Statement;
import xdi2.core.constants.XDIConstants;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.XDIUtil;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.constants.XDIMessagingConstants;

public class DataAttributeInstancePanel extends Panel implements XdiGraphListener {

	private static final long serialVersionUID = -5082464847478633075L;

	protected ResourceBundle resourceBundle;

	private XdiEndpoint endpoint;
	private XRI3Segment contextNodeXri;
	private XRI3Segment attributeXri;
	private XRI3Segment address;

	private boolean readOnly;

	private XdiPanel xdiPanel;
	private Label valueLabel;
	private TextField valueTextField;
	private Button editButton;
	private Button updateButton;
	private Button deleteButton;

	/**
	 * Creates a new <code>AccountPersonaPanel</code>.
	 */
	public DataAttributeInstancePanel() {
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

			String value = this.getValue();

			this.xdiPanel.setEndpointAndMainAddressAndGetAddresses(this.endpoint, this.address, this.xdiGetAddresses());
			this.valueLabel.setText(value);
			this.valueTextField.setText(value);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	public XRI3Segment[] xdiGetAddresses() {

		return new XRI3Segment[] {
				this.address
		};
	}

	public XRI3Segment[] xdiAddAddresses() {

		return new XRI3Segment[0];
	}

	public XRI3Segment[] xdiModAddresses() {

		return new XRI3Segment[] {
				this.address
		};
	}

	public XRI3Segment[] xdiSetAddresses() {

		return new XRI3Segment[] {
				this.address
		};
	}

	public XRI3Segment[] xdiDelAddresses() {

		return new XRI3Segment[] {
				this.address
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

	public void setEndpointAndContextNodeXriAndAttributeXri(XdiEndpoint endpoint, XRI3Segment contextNodeXri, XRI3Segment attributeXri) {

		// remove us as listener
		
		if (this.endpoint != null) this.endpoint.removeXdiGraphListener(this);
		
		// refresh
		
		this.endpoint = endpoint;
		this.contextNodeXri = contextNodeXri;
		this.attributeXri = attributeXri;
		this.address = new XRI3Segment("" + contextNodeXri + attributeXri);

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

	private void onEditActionPerformed(ActionEvent e) {

		this.valueTextField.setText(this.valueLabel.getText());

		this.valueLabel.setVisible(false);
		this.valueTextField.setVisible(true);
		this.editButton.setVisible(false);
		this.updateButton.setVisible(true);
	}

	private void onUpdateActionPerformed(ActionEvent e) {

		try {

			this.setValue(this.valueTextField.getText());
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

				this.delete();
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

	private String getValue() throws XdiException {

		// $get
		
		Message message = this.endpoint.prepareOperation(XDIMessagingConstants.XRI_S_GET, this.address);
		MessageResult messageResult = this.endpoint.send(message);

		Literal literal = messageResult.getGraph().findLiteral(this.address);
		if (literal == null) throw new XdiNotExistentException();

		return literal.getLiteralData();
	}

	private void setValue(String value) throws XdiException {

		// $mod

		Statement targetStatement = StatementUtil.fromComponents(this.address, XDIConstants.XRI_S_LITERAL, XDIUtil.stringToDataXriSegment(value));
		Message message = this.endpoint.prepareOperation(XDIMessagingConstants.XRI_S_MOD, targetStatement);

		this.endpoint.send(message);
	}

	private void delete() throws XdiException {

		// $del

		Message message = this.endpoint.prepareOperation(XDIMessagingConstants.XRI_S_DEL, this.address);

		this.endpoint.send(message);
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this
				.setInsets(new Insets(new Extent(30, Extent.PX), new Extent(0,
						Extent.PX), new Extent(0, Extent.PX), new Extent(5,
						Extent.PX)));
		Row row1 = new Row();
		row1.setCellSpacing(new Extent(10, Extent.PX));
		add(row1);
		xdiPanel = new XdiPanel();
		row1.add(xdiPanel);
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
	}
}
