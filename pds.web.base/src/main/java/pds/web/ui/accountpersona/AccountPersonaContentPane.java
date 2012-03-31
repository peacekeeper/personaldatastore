package pds.web.ui.accountpersona;

import java.util.ResourceBundle;

import javax.xml.ws.soap.Addressing;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.Button;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.RowLayoutData;
import nextapp.echo.app.layout.SplitPaneLayoutData;
import pds.web.components.xdi.XdiPanel;
import pds.web.ui.MessageDialog;
import pds.web.ui.shared.DataPredicatesColumn;
import pds.xdi.XdiContext;
import pds.xdi.XdiException;
import pds.xdi.events.XdiGraphAddEvent;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import pds.xdi.events.XdiGraphModEvent;
import xdi2.core.Graph;
import xdi2.core.xri3.impl.XRI3;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import echopoint.ImageIcon;

public class AccountPersonaContentPane extends ContentPane implements XdiGraphListener {

	private static final long serialVersionUID = 5781883512857770059L;

	protected ResourceBundle resourceBundle;

	private XdiContext context;
	private XRI3Segment subjectXri;
	private XRI3 address;
	private XRI3 nameAddress;

	private Label nameLabel;
	private XdiPanel xdiPanel;
	private DataPredicatesColumn dataPredicatesColumn;

	/**
	 * Creates a new <code>ConfigureAPIsContentPane</code>.
	 */
	public AccountPersonaContentPane() {
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

			this.nameLabel.setText(this.getName());
			this.xdiPanel.setContextAndMainAddressAndGetAddresses(this.context, this.address, this.xdiGetAddresses());
			this.dataPredicatesColumn.setContextAndSubjectXri(this.context, this.subjectXri);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	public XRI3[] xdiGetAddresses() {

		return new XRI3[] {
				new XRI3("" + this.nameAddress + "/$$")
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

	public XRI3[] xdiSetAddresses() {

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
				
				this.getParent().getParent().remove(this.getParent());
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
		this.nameAddress = new XRI3("" + this.subjectXri + "/$a$xsd$string");

		this.refresh();

		// add us as listener

		this.context.addXdiGraphListener(this);
	}

	private void onDeletePersona(ActionEvent e) {

		try {

			this.delete();
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while storing your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	private String getName() throws XdiException {

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, this.nameAddress);
		MessageResult messageResult = this.context.send(operation);

		return Addressing.findLiteralData(messageResult.getGraph(), this.nameAddress);
	}

	private void delete() throws XdiException {

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_DEL);
		Graph operationGraph = operation.createOperationGraph(null);
		operationGraph.createStatement(this.subjectXri);

		this.context.send(operation);
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setInsets(new Insets(new Extent(10, Extent.PX)));
		SplitPane splitPane1 = new SplitPane();
		splitPane1.setStyleName("Default");
		splitPane1.setOrientation(SplitPane.ORIENTATION_VERTICAL_TOP_BOTTOM);
		splitPane1.setResizable(false);
		splitPane1.setSeparatorVisible(false);
		add(splitPane1);
		Row row2 = new Row();
		row2.setCellSpacing(new Extent(10, Extent.PX));
		SplitPaneLayoutData row2LayoutData = new SplitPaneLayoutData();
		row2LayoutData.setMinimumSize(new Extent(70, Extent.PX));
		row2LayoutData.setMaximumSize(new Extent(70, Extent.PX));
		row2.setLayoutData(row2LayoutData);
		splitPane1.add(row2);
		Row row5 = new Row();
		RowLayoutData row5LayoutData = new RowLayoutData();
		row5LayoutData.setWidth(new Extent(50, Extent.PERCENT));
		row5.setLayoutData(row5LayoutData);
		row2.add(row5);
		ImageIcon imageIcon2 = new ImageIcon();
		ResourceImageReference imageReference1 = new ResourceImageReference(
				"/pds/web/resource/image/accountpersona.png");
		imageIcon2.setIcon(imageReference1);
		imageIcon2.setHeight(new Extent(48, Extent.PX));
		imageIcon2.setWidth(new Extent(48, Extent.PX));
		row5.add(imageIcon2);
		nameLabel = new Label();
		nameLabel.setStyleName("Header");
		nameLabel.setText("...");
		row5.add(nameLabel);
		Row row3 = new Row();
		row3.setAlignment(new Alignment(Alignment.RIGHT, Alignment.DEFAULT));
		row3.setCellSpacing(new Extent(10, Extent.PX));
		RowLayoutData row3LayoutData = new RowLayoutData();
		row3LayoutData.setWidth(new Extent(50, Extent.PERCENT));
		row3.setLayoutData(row3LayoutData);
		row2.add(row3);
		xdiPanel = new XdiPanel();
		row3.add(xdiPanel);
		Button button1 = new Button();
		button1.setStyleName("Default");
		button1.setText("Delete this Persona");
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onDeletePersona(e);
			}
		});
		row3.add(button1);
		dataPredicatesColumn = new DataPredicatesColumn();
		splitPane1.add(dataPredicatesColumn);
	}
}
