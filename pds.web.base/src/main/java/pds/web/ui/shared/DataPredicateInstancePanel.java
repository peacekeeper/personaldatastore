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

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.addressing.Addressing;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.dictionary.Dictionary;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.PDSApplication;
import pds.web.components.xdi.XdiPanel;
import pds.web.ui.MessageDialog;
import pds.web.xdi.XdiException;
import pds.web.xdi.XdiNotExistentException;
import pds.web.xdi.XdiUtil;
import pds.web.xdi.events.XdiGraphDelEvent;
import pds.web.xdi.events.XdiGraphEvent;
import pds.web.xdi.events.XdiGraphListener;
import pds.web.xdi.events.XdiGraphModEvent;
import pds.web.xdi.objects.XdiContext;

public class DataPredicateInstancePanel extends Panel implements XdiGraphListener {

	private static final long serialVersionUID = -5082464847478633075L;

	protected ResourceBundle resourceBundle;

	private XdiContext context;
	private XRI3Segment subjectXri;
	private XRI3Segment predicateXri;
	private XRI3 address;

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
	public DataPredicateInstancePanel() {
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

		PDSApplication.getApp().getOpenContext().removeXdiGraphListener(this);
	}

	private void refresh() {

		try {

			String value = this.getValue();

			this.xdiPanel.setContextAndMainAddressAndGetAddresses(this.context, this.address, this.xdiGetAddresses());
			this.valueLabel.setText(value);
			this.valueTextField.setText(value);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your personal data: " + ex.getMessage(), ex);
			return;
		}
	}

	public XRI3[] xdiGetAddresses() {

		return new XRI3[] {
				this.address
		};
	}

	public XRI3[] xdiAddAddresses() {

		return new XRI3[0];
	}

	public XRI3[] xdiModAddresses() {

		return new XRI3[] {
				this.address
		};
	}

	public XRI3[] xdiDelAddresses() {

		return new XRI3[] {
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

			MessageDialog.problem("Sorry, a problem occurred while retrieving your personal data: " + ex.getMessage(), ex);
			return;
		}
	}

	public void setContextAndSubjectXriAndPredicateXri(XdiContext context, XRI3Segment subjectXri, XRI3Segment predicateXri) {

		this.context = context;
		this.subjectXri = subjectXri;
		this.predicateXri = predicateXri;
		this.address = new XRI3("" + subjectXri + "/" + predicateXri);

		this.refresh();

		// add us as listener

		PDSApplication.getApp().getOpenContext().addXdiGraphListener(this);
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
		} catch (XdiException ex) {

			MessageDialog.problem("Sorry, a problem occurred while storing your personal data: " + ex.getMessage(), ex);
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
			} catch (XdiException ex) {

				MessageDialog.problem("Sorry, a problem occurred while storing your personal data: " + ex.getMessage(), ex);
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
		
		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, this.address);
		MessageResult messageResult = this.context.send(operation);

		String data = Addressing.findLiteralData(messageResult.getGraph(), this.address);
		if (data == null) throw new XdiNotExistentException();

		return data;
	}

	private void setValue(String value) throws XdiException {

		// $mod

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_MOD);
		Graph operationGraph = operation.createOperationGraph(null);
		operationGraph.createStatement(this.subjectXri, this.predicateXri, value);

		this.context.send(operation);
	}

	private void delete() throws XdiException {

		// $del

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_DEL);
		Graph operationGraph = operation.createOperationGraph(null);
		operationGraph.createStatement(this.subjectXri, this.predicateXri);
		operationGraph.createStatement(this.subjectXri, Dictionary.makeExtensionPredicate(XdiUtil.extractParentXriSegment(this.predicateXri)), XdiUtil.extractLocalXriSegment(this.predicateXri));

		this.context.send(operation);
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
