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

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.dictionary.Dictionary;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.util.iterators.IteratorListMaker;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.components.xdi.XdiPanel;
import pds.web.ui.MainWindow;
import pds.web.ui.MessageDialog;
import pds.xdi.XdiContext;
import pds.xdi.XdiException;
import pds.xdi.events.XdiGraphAddEvent;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import pds.xdi.events.XdiGraphModEvent;

public class DataPredicatePanel extends Panel implements XdiGraphListener {

	private static final long serialVersionUID = -5082464847478633075L;

	protected ResourceBundle resourceBundle;

	private XdiContext context;
	private XRI3Segment subjectXri;
	private XRI3Segment predicateXri;
	private XRI3 address;
	private XRI3 extensionAddress;
	private XRI3 canonicalAddress;
	private XRI3 addAddress;

	private boolean readOnly;

	private XdiPanel xdiPanel;
	private Label predicateXriLabel;
	private TextField addTextField;
	private Column instanceValuesColumn;
	private Button cancelButton;
	private Button addButton;

	/**
	 * Creates a new <code>AccountPersonaPanel</code>.
	 */
	public DataPredicatePanel() {
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

		if (this.context != null) this.context.removeXdiGraphListener(this);
	}

	private void refresh() {

		try {

			this.xdiPanel.setContextAndMainAddressAndGetAddresses(this.context, this.address, this.xdiGetAddresses());
			this.predicateXriLabel.setText(this.predicateXri.toString());

			this.instanceValuesColumn.removeAll();
			List<XRI3Segment> dataPredicateInstanceXris = this.getDataPredicateInstanceXris();

			for (XRI3Segment dataPredicateInstanceXri : dataPredicateInstanceXris) {

				this.addDataPredicateInstancePanel(dataPredicateInstanceXri);
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	private void addDataPredicateInstancePanel(XRI3Segment dataPredicateInstanceXri) {

		DataPredicateInstancePanel dataPredicatePanel = new DataPredicateInstancePanel();
		dataPredicatePanel.setContextAndSubjectXriAndPredicateXri(this.context, this.subjectXri, dataPredicateInstanceXri);
		dataPredicatePanel.setReadOnly(this.readOnly);

		this.instanceValuesColumn.add(dataPredicatePanel);
	}

	public XRI3[] xdiGetAddresses() {

		return new XRI3[] {
				this.address,
				this.extensionAddress,
				this.canonicalAddress
		};
	}

	public XRI3[] xdiAddAddresses() {

		return new XRI3[] {
				this.addAddress
		};
	}

	public XRI3[] xdiModAddresses() {

		return new XRI3[0];
	}

	public XRI3[] xdiSetAddresses() {

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

	public void setContextAndSubjectXriAndPredicateXri(XdiContext context, XRI3Segment subjectXri, XRI3Segment predicateXri) {

		// remove us as listener

		if (this.context != null) this.context.removeXdiGraphListener(this);

		// refresh

		this.context = context;
		this.subjectXri = subjectXri;
		this.predicateXri = predicateXri;
		this.address = new XRI3("" + this.subjectXri + "/" + this.predicateXri);
		this.extensionAddress = new XRI3("" + this.subjectXri + "/" + Dictionary.makeExtensionPredicate(this.predicateXri));
		this.canonicalAddress = new XRI3("" + this.subjectXri + "/" + Dictionary.makeCanonicalPredicate(this.predicateXri));
		this.addAddress = new XRI3("" + this.subjectXri + "/" + this.predicateXri + "$($)");

		this.refresh();

		// add us as listener

		this.context.addXdiGraphListener(this);
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

		for (Component component : MainWindow.findChildComponentsByClass(this, DataPredicateInstancePanel.class)) {

			((DataPredicateInstancePanel) component).setReadOnly(readOnly);
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

	private List<XRI3Segment> getDataPredicateInstanceXris() throws XdiException {

		// $get

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, this.extensionAddress);
		MessageResult messageResult = this.context.send(operation);

		Subject subject = messageResult.getGraph().getSubject(this.subjectXri);
		if (subject == null) return new ArrayList<XRI3Segment> ();

		Iterator<XRI3Segment> dataPredicateInstanceXris = Dictionary.getPredicateExtensions(subject, this.predicateXri);
		return new IteratorListMaker<XRI3Segment> (dataPredicateInstanceXris).list();
	}

	private void addDataPredicateInstance(final String value) throws XdiException {

		XRI3Segment dataPredicateInstanceXri = new XRI3Segment("" + this.predicateXri + "$($)");

		// $add

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_ADD);
		Graph operationGraph = operation.createOperationGraph(null);
		operationGraph.createStatement(this.subjectXri, dataPredicateInstanceXri, value);
		this.context.send(operation);
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
		predicateXriLabel = new Label();
		predicateXriLabel.setStyleName("Bold");
		predicateXriLabel.setText("...");
		RowLayoutData predicateXriLabelLayoutData = new RowLayoutData();
		predicateXriLabelLayoutData.setWidth(new Extent(120, Extent.PX));
		predicateXriLabel.setLayoutData(predicateXriLabelLayoutData);
		row2.add(predicateXriLabel);
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
