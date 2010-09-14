package pds.web.ui.app.feed;

import java.util.Date;
import java.util.ResourceBundle;

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
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.types.Timestamps;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.dictionary.feed.FeedDictionary;
import pds.web.components.xdi.XdiPanel;
import pds.web.ui.MessageDialog;
import pds.web.ui.app.feed.components.EntriesColumn;
import pds.web.ui.app.feed.util.PuSHDiscovery;
import pds.web.ui.app.feed.util.PuSHUtil;
import pds.xdi.XdiContext;
import pds.xdi.XdiException;
import pds.xdi.events.XdiGraphAddEvent;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import pds.xdi.events.XdiGraphModEvent;
import echopoint.ImageIcon;
import nextapp.echo.app.Panel;
import pds.web.ui.app.feed.components.TopicsColumn;

public class FeedContentPane extends ContentPane implements XdiGraphListener {

	private static final long serialVersionUID = -8925773333194027452L;

	private static final XRI3Segment XRI_FEED = new XRI3Segment("+ostatus+feed");
	private static final XRI3Segment XRI_TOPICS = new XRI3Segment("+ostatus+topics");
	private static final XRI3Segment XRI_ENTRY = new XRI3Segment("+entry");
	private static final XRI3Segment XRI_VERIFYTOKEN = new XRI3Segment("+push+verify.token");
	private static final XRI3Segment XRI_HUB = new XRI3Segment("+push+hub");

	protected ResourceBundle resourceBundle;

	private FeedPdsWebApp feedPdsWebApp;
	private XdiContext context;
	private XRI3Segment subjectXri;
	private XRI3 address;
	private XRI3 feedAddress;
	private XRI3 topicsAddress;

	private XdiPanel xdiPanel;
	private TextField subscribeTextField;
	private EntriesColumn entriesColumn;
	private TextField contentTextField;
	private TopicsColumn topicsColumn;

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

		if (this.context != null) this.context.removeXdiGraphListener(this);
	}

	private void refresh() {

	}

	public XRI3[] xdiGetAddresses() {

		return new XRI3[] {
				this.feedAddress,
				this.topicsAddress
		};
	}

	public XRI3[] xdiAddAddresses() {

		return new XRI3[] {
				new XRI3("" + this.feedAddress + "/$$"),
				new XRI3("" + this.topicsAddress + "/$$")
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

				this.getParent().getParent().remove(this.getParent());
				return;
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	public void setFeedPdsWebApp(FeedPdsWebApp feedPdsWebApp) {

		this.feedPdsWebApp = feedPdsWebApp;
	}

	public void setContextAndSubjectXri(XdiContext context, XRI3Segment subjectXri) {

		// remove us as listener

		if (this.context != null) this.context.removeXdiGraphListener(this);

		// refresh

		this.context = context;
		this.subjectXri = subjectXri;
		this.address = new XRI3("" + this.subjectXri);
		this.feedAddress = new XRI3("" + this.subjectXri + "/" + XRI_FEED);
		this.topicsAddress = new XRI3("" + this.subjectXri + "/" + XRI_TOPICS);

		this.xdiPanel.setContextAndMainAddressAndGetAddresses(this.context, this.address, this.xdiGetAddresses());
		this.entriesColumn.setContextAndSubjectXri(context, subjectXri);
		this.topicsColumn.setContextAndSubjectXri(context, subjectXri);

		this.refresh();

		// add us as listener

		this.context.addXdiGraphListener(this);
	}

	private void onPostActionPerformed(ActionEvent e) {
		
		// add the entry

		try {

			this.addEntry(this.contentTextField.getText(), this.contentTextField.getText(), new Date());
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while storing your Personal Data: " + ex.getMessage(), ex);
			return;
		}

		// publish it

		String hubtopic = this.feedPdsWebApp.getAtomFeedEndpoint() + this.context.getCanonical().toString();

		try {

			PuSHUtil.publish(this.feedPdsWebApp.getHub(), hubtopic);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while publishing an item: " + ex.getMessage(), ex);
			return;
		}

		// reset

		this.contentTextField.setText("");
	}

	private void onSubscribeActionPerformed(ActionEvent e) {

		String hubtopic = this.subscribeTextField.getText();

		// discover the feed's hub

		String hub;

		try {

			hub = PuSHDiscovery.getHub(hubtopic);
			if (hub == null) throw new RuntimeException("No hub found.");
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while discoverying the Feed's hub: " + ex.getMessage(), ex);
			return;
		}

		// create a hub.verify_token

		String hubverifytoken = PuSHUtil.makeVerifyToken();

		// add the topic
		
		try {

			this.addTopic(hubtopic, hubverifytoken, hub);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while storing your Personal Data: " + ex.getMessage(), ex);
			return;
		}

		// subscribe to the feed at the hub

		try {

			String pubsubhubbubEndpoint = this.feedPdsWebApp.getPubsubhubbubEndpoint() + this.context.getCanonical();

			PuSHUtil.subscribe(
					hub, 
					pubsubhubbubEndpoint, 
					hubtopic, 
					this.feedPdsWebApp.getLeaseSeconds(), 
					null,
					hubverifytoken);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while subscribing to the feed: " + ex.getMessage(), ex);
			return;
		}

		// reset

		this.subscribeTextField.setText("");
	}

	private void addEntry(String title, String description, Date publishedDate) throws XdiException {

		// $add

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_ADD);
		Graph operationGraph = operation.createOperationGraph(null);
		Graph feedGraph = operationGraph.createStatement(this.subjectXri, XRI_FEED, (Graph) null).getInnerGraph();

		Subject subject = feedGraph.createSubject(new XRI3Segment(XRI_ENTRY.toString() + "$($)"));
		FeedDictionary.fromEntry(subject, title, description, publishedDate);

		this.context.send(operation);
	}

	private void addTopic(String hubtopic, String hubverifytoken, String hub) throws XdiException {

		// $add

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_ADD);
		Graph operationGraph = operation.createOperationGraph(null);
		Graph topicsGraph = operationGraph.createStatement(this.subjectXri, XRI_TOPICS, (Graph) null).getInnerGraph();
		topicsGraph.createStatement(new XRI3Segment("$(" + hubtopic + ")"), XRI_VERIFYTOKEN, hubverifytoken);
		topicsGraph.createStatement(new XRI3Segment("$(" + hubtopic + ")"), XRI_HUB, hub);
		topicsGraph.createStatement(new XRI3Segment("$(" + hubtopic + ")"), new XRI3Segment("$d"), Timestamps.dateToXri(new Date()));

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
				"/pds/web/ui/app/feed/app.png");
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
		Column column1 = new Column();
		splitPane1.add(column1);
		Row row5 = new Row();
		row5.setCellSpacing(new Extent(10, Extent.PX));
		column1.add(row5);
		Label label4 = new Label();
		label4.setStyleName("Default");
		label4.setText("What's up?");
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
		Panel panel1 = new Panel();
		column1.add(panel1);
		SplitPane splitPane2 = new SplitPane();
		splitPane2.setStyleName("Default");
		ResourceImageReference imageReference2 = new ResourceImageReference(
				"/pds/web/resource/image/separator-blue.png");
		splitPane2.setSeparatorHorizontalImage(new FillImage(imageReference2));
		splitPane2.setOrientation(SplitPane.ORIENTATION_HORIZONTAL_LEFT_RIGHT);
		splitPane2.setSeparatorWidth(new Extent(10, Extent.PX));
		splitPane2.setResizable(true);
		splitPane2.setSeparatorVisible(true);
		panel1.add(splitPane2);
		Column column2 = new Column();
		column2.setCellSpacing(new Extent(10, Extent.PX));
		splitPane2.add(column2);
		Row row4 = new Row();
		row4.setCellSpacing(new Extent(10, Extent.PX));
		column2.add(row4);
		Label label2 = new Label();
		label2.setStyleName("Default");
		label2.setText("Subscribe to a topic:");
		row4.add(label2);
		subscribeTextField = new TextField();
		subscribeTextField.setStyleName("Default");
		subscribeTextField.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onSubscribeActionPerformed(e);
			}
		});
		row4.add(subscribeTextField);
		Button button2 = new Button();
		button2.setStyleName("Default");
		button2.setText("Subscribe");
		button2.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onSubscribeActionPerformed(e);
			}
		});
		row4.add(button2);
		topicsColumn = new TopicsColumn();
		column2.add(topicsColumn);
		entriesColumn = new EntriesColumn();
		splitPane2.add(entriesColumn);
	}
}
