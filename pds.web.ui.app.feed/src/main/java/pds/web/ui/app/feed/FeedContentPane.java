package pds.web.ui.app.feed;

import java.util.Date;
import java.util.ResourceBundle;
import java.util.UUID;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.Button;
import nextapp.echo.app.Column;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.FillImage;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.TextField;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.SplitPaneLayoutData;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.types.Timestamps;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.PDSApplication;
import pds.web.components.xdi.XdiPanel;
import pds.web.ui.MessageDialog;
import pds.web.ui.shared.EntriesColumn;
import pds.web.xdi.XdiException;
import pds.web.xdi.events.XdiGraphAddEvent;
import pds.web.xdi.events.XdiGraphDelEvent;
import pds.web.xdi.events.XdiGraphEvent;
import pds.web.xdi.events.XdiGraphListener;
import pds.web.xdi.events.XdiGraphModEvent;
import pds.web.xdi.objects.XdiContext;
import echopoint.ImageIcon;

public class FeedContentPane extends ContentPane implements XdiGraphListener {

	private static final long serialVersionUID = -8925773333194027452L;

	protected ResourceBundle resourceBundle;

	private XdiContext context;
	private XRI3Segment subjectXri;
	private XRI3 address;
	private XRI3 feedAddress;

	private XdiPanel xdiPanel;
	private TextField contentTextField;
	private EntriesColumn entriesColumn;

	/**
	 * Creates a new <code>AddressBookContentPane</code>.
	 */
	public FeedContentPane() {
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

		// remove us as listener

		PDSApplication.getApp().getOpenContext().removeXdiGraphListener(this);
	}

	private void refresh() {

	}

	public XRI3[] xdiGetAddresses() {

		return new XRI3[] {
				this.feedAddress
		};
	}

	public XRI3[] xdiAddAddresses() {

		return new XRI3[] {
				new XRI3("" + this.feedAddress + "/$$")
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

				this.getParent().getParent().remove(this.getParent());
				return;
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your personal data: " + ex.getMessage(), ex);
			return;
		}
	}

	public void setContextAndSubjectXri(XdiContext context, XRI3Segment subjectXri) {

		this.context = context;
		this.subjectXri = subjectXri;
		this.address = new XRI3("" + this.subjectXri);
		this.feedAddress = new XRI3("" + this.subjectXri + "/+feed");

		this.xdiPanel.setContextAndMainAddressAndGetAddresses(this.context, this.address, this.xdiGetAddresses());
		this.entriesColumn.setContextAndSubjectXri(context, subjectXri);

		this.refresh();

		// add us as listener

		PDSApplication.getApp().getOpenContext().addXdiGraphListener(this);
	}

	private void onPostActionPerformed(ActionEvent e) {

		try {

			this.addEntry(new Date(), this.contentTextField.getText());
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while storing your personal data: " + ex.getMessage(), ex);
			return;
		}

		// reset

		this.contentTextField.setText("");
	}

	private void addEntry(Date timestamp, String content) throws XdiException {

		String entryGuid = UUID.randomUUID().toString();

		// $add

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_ADD);
		Graph operationGraph = operation.createOperationGraph(null);
		operationGraph.createStatement(this.subjectXri, new XRI3Segment("+feed"), (Graph) null);
		Graph feedGraph = operationGraph.getSubject(this.subjectXri).getPredicate(new XRI3Segment("+feed")).getInnerGraph();
		feedGraph.createStatement(new XRI3Segment("$" + entryGuid), new XRI3Segment("$d"), Timestamps.dateToXri(timestamp));
		feedGraph.createStatement(new XRI3Segment("$" + entryGuid), new XRI3Segment("$a$xsd$string"), content);
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
		Row row1 = new Row();
		SplitPaneLayoutData row1LayoutData = new SplitPaneLayoutData();
		row1LayoutData.setMinimumSize(new Extent(70, Extent.PX));
		row1LayoutData.setMaximumSize(new Extent(70, Extent.PX));
		row1.setLayoutData(row1LayoutData);
		splitPane1.add(row1);
		Row row2 = new Row();
		row2.setCellSpacing(new Extent(10, Extent.PX));
		row1.add(row2);
		ImageIcon imageIcon2 = new ImageIcon();
		ResourceImageReference imageReference1 = new ResourceImageReference(
				"/pds/web/resource/image/app-feed.png");
		imageIcon2.setIcon(imageReference1);
		imageIcon2.setHeight(new Extent(48, Extent.PX));
		imageIcon2.setWidth(new Extent(48, Extent.PX));
		row2.add(imageIcon2);
		Label label1 = new Label();
		label1.setStyleName("Header");
		label1.setText("Feed");
		row2.add(label1);
		Row row3 = new Row();
		row3.setAlignment(new Alignment(Alignment.RIGHT, Alignment.DEFAULT));
		row1.add(row3);
		xdiPanel = new XdiPanel();
		row3.add(xdiPanel);
		SplitPane splitPane2 = new SplitPane();
		splitPane2.setStyleName("Default");
		ResourceImageReference imageReference2 = new ResourceImageReference(
				"/pds/web/resource/image/separator-blue.png");
		splitPane2.setSeparatorHorizontalImage(new FillImage(imageReference2));
		splitPane2.setOrientation(SplitPane.ORIENTATION_HORIZONTAL_LEFT_RIGHT);
		splitPane2.setSeparatorWidth(new Extent(10, Extent.PX));
		splitPane2.setResizable(true);
		splitPane2.setSeparatorVisible(true);
		splitPane1.add(splitPane2);
		Column column1 = new Column();
		column1.setCellSpacing(new Extent(10, Extent.PX));
		splitPane2.add(column1);
		Column column6 = new Column();
		column6.setCellSpacing(new Extent(10, Extent.PX));
		column1.add(column6);
		Label label3 = new Label();
		label3.setStyleName("Default");
		label3.setText("What's up?");
		column6.add(label3);
		Row row5 = new Row();
		row5.setCellSpacing(new Extent(10, Extent.PX));
		column6.add(row5);
		Label label4 = new Label();
		label4.setStyleName("Default");
		label4.setText("Content:");
		row5.add(label4);
		contentTextField = new TextField();
		contentTextField.setStyleName("Default");
		contentTextField.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onPostActionPerformed(e);
			}
		});
		row5.add(contentTextField);
		Button button1 = new Button();
		button1.setStyleName("Default");
		button1.setText("Post!");
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onPostActionPerformed(e);
			}
		});
		row5.add(button1);
		entriesColumn = new EntriesColumn();
		splitPane2.add(entriesColumn);
	}
}
