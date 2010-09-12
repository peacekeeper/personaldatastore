package pds.web.ui.accountroot;

import java.util.ResourceBundle;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.layout.RowLayoutData;
import nextapp.echo.app.layout.SplitPaneLayoutData;

import org.eclipse.higgins.xdi4j.constants.DictionaryConstants;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.components.xdi.XdiPanel;
import pds.web.ui.MessageDialog;
import pds.web.ui.shared.DataPredicatesColumn;
import pds.xdi.XdiContext;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import echopoint.ImageIcon;

public class AccountRootContentPane extends ContentPane implements XdiGraphListener {

	private static final long serialVersionUID = 5781883512857770059L;

	protected ResourceBundle resourceBundle;

	private XdiContext context;
	private XRI3Segment subjectXri;
	private XRI3 address;
	private XRI3 extensionAddress;
	private XRI3 equivalenceAddress;
	private XRI3 inheritanceAddress;

	private Label inumberLabel;
	private XdiPanel xdiPanel;
	private DataPredicatesColumn dataPredicatesColumn;

	/**
	 * Creates a new <code>ConfigureAPIsContentPane</code>.
	 */
	public AccountRootContentPane() {
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

			this.inumberLabel.setText(this.subjectXri.toString());
			this.xdiPanel.setContextAndMainAddressAndGetAddresses(this.context, this.address, this.xdiGetAddresses());
			this.dataPredicatesColumn.setContextAndSubjectXri(this.context, this.subjectXri);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	public XRI3[] xdiGetAddresses() {

		return new XRI3[] {
				this.extensionAddress,
				this.equivalenceAddress,
				this.inheritanceAddress
		};
	}

	public XRI3[] xdiAddAddresses() {

		return new XRI3[] {
				new XRI3("" + this.extensionAddress + "/$$"),
				new XRI3("" + this.equivalenceAddress + "/$$"),
				new XRI3("" + this.inheritanceAddress + "/$$")
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

		this.refresh();
	}

	public void setContextAndSubjectXri(XdiContext context, XRI3Segment subjectXri) {

		// remove us as listener
		
		if (this.context != null) this.context.removeXdiGraphListener(this);

		// refresh
		
		this.context = context;
		this.subjectXri = subjectXri;
		this.address = new XRI3("" + this.subjectXri);
		this.extensionAddress = new XRI3("" + this.subjectXri + "/" + DictionaryConstants.XRI_EXTENSION);
		this.equivalenceAddress = new XRI3("" + this.subjectXri + "/" + DictionaryConstants.XRI_IS);
		this.inheritanceAddress = new XRI3("" + this.subjectXri + "/" + DictionaryConstants.XRI_IS_A);

		this.refresh();

		// add us as listener

		this.context.addXdiGraphListener(this);
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
		SplitPaneLayoutData row2LayoutData = new SplitPaneLayoutData();
		row2LayoutData.setMinimumSize(new Extent(70, Extent.PX));
		row2LayoutData.setMaximumSize(new Extent(70, Extent.PX));
		row2.setLayoutData(row2LayoutData);
		splitPane1.add(row2);
		Row row3 = new Row();
		row3.setCellSpacing(new Extent(10, Extent.PX));
		RowLayoutData row3LayoutData = new RowLayoutData();
		row3LayoutData.setWidth(new Extent(50, Extent.PERCENT));
		row3.setLayoutData(row3LayoutData);
		row2.add(row3);
		ImageIcon imageIcon2 = new ImageIcon();
		ResourceImageReference imageReference1 = new ResourceImageReference(
				"/pds/web/resource/image/accountroot.png");
		imageIcon2.setIcon(imageReference1);
		imageIcon2.setHeight(new Extent(48, Extent.PX));
		imageIcon2.setWidth(new Extent(48, Extent.PX));
		row3.add(imageIcon2);
		Label label2 = new Label();
		label2.setStyleName("Header");
		label2.setText("Account Root");
		row3.add(label2);
		Row row1 = new Row();
		row1.setAlignment(new Alignment(Alignment.RIGHT, Alignment.DEFAULT));
		row1.setCellSpacing(new Extent(10, Extent.PX));
		RowLayoutData row1LayoutData = new RowLayoutData();
		row1LayoutData.setWidth(new Extent(50, Extent.PERCENT));
		row1.setLayoutData(row1LayoutData);
		row2.add(row1);
		Label label1 = new Label();
		label1.setStyleName("Header");
		label1.setText("I-Number:");
		row1.add(label1);
		inumberLabel = new Label();
		inumberLabel.setStyleName("Bold");
		inumberLabel.setText("...");
		row1.add(inumberLabel);
		xdiPanel = new XdiPanel();
		row1.add(xdiPanel);
		dataPredicatesColumn = new DataPredicatesColumn();
		splitPane1.add(dataPredicatesColumn);
	}
}
