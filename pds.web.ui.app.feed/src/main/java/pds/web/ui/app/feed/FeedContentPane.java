package pds.web.ui.app.feed;

import java.net.URI;
import java.util.Date;
import java.util.ResourceBundle;

import javax.activation.MimeType;

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

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Text.Type;
import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.types.Timestamps;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.openxrd.xrd.core.Link;
import org.openxrd.xrd.core.XRD;

import pds.dictionary.feed.FeedDictionary;
import pds.discovery.xrd.XRDDiscovery;
import pds.web.PDSApplication;
import pds.web.components.xdi.XdiPanel;
import pds.web.logger.Logger;
import pds.web.ui.MessageDialog;
import pds.web.ui.app.feed.components.EntriesColumn;
import pds.web.ui.app.feed.components.TopicPanel.TopicPanelDelegate;
import pds.web.ui.app.feed.components.TopicsColumn;
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
import nextapp.echo.app.TextArea;
import nextapp.echo.app.Font;

public class FeedContentPane extends ContentPane implements XdiGraphListener {

	private static final long serialVersionUID = -8925773333194027452L;

	private static final XRI3Segment XRI_FEED = new XRI3Segment("+ostatus+feed");
	private static final XRI3Segment XRI_MENTIONS = new XRI3Segment("+ostatus+mentions");
	private static final XRI3Segment XRI_TOPICS = new XRI3Segment("+ostatus+topics");
	private static final XRI3Segment XRI_ENTRY = new XRI3Segment("+entry");
	private static final XRI3Segment XRI_VERIFYTOKEN = new XRI3Segment("+push+verify.token");
	private static final XRI3Segment XRI_HUB = new XRI3Segment("+push+hub");
	private static final XRI3Segment XRI_NAME = new XRI3Segment("+name");

	private static final IRI ACTIVITY_VERB = new IRI("http://activitystrea.ms/schema/1.0/post");
	private static final IRI ACTIVITY_OBJECTTYPE = new IRI("http://activitystrea.ms/schema/1.0/note");

	protected ResourceBundle resourceBundle;

	private FeedPdsWebApp feedPdsWebApp;
	private XdiContext context;
	private XRI3Segment subjectXri;
	private XRI3 address;
	private XRI3 feedAddress;
	private XRI3 mentionsAddress;
	private XRI3 topicsAddress;

	private XdiPanel xdiPanel;
	private TextField subscribeTextField;
	private TextArea contentTextArea;
	private TopicsColumn topicsColumn;
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

		this.topicsColumn.setTopicPanelDelegate(new MyTopicPanelDelegate());
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
				this.mentionsAddress,
				this.topicsAddress
		};
	}

	public XRI3[] xdiAddAddresses() {

		return new XRI3[] {
				new XRI3("" + this.feedAddress + "/$$"),
				new XRI3("" + this.mentionsAddress + "/$$"),
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
		this.feedAddress = new XRI3("" + this.address + "/" + XRI_FEED);
		this.mentionsAddress = new XRI3("" + this.address + "/" + XRI_MENTIONS);
		this.topicsAddress = new XRI3("" + this.address + "/" + XRI_TOPICS);

		this.xdiPanel.setContextAndMainAddressAndGetAddresses(this.context, this.address, this.xdiGetAddresses());
		this.entriesColumn.setContextAndAddress(context, this.feedAddress);
		this.topicsColumn.setContextAndSubjectXri(context, subjectXri);

		this.refresh();

		// add us as listener

		this.context.addXdiGraphListener(this);
	}

	private void onPostActionPerformed(ActionEvent e) {

		// add the entry

		try {

			this.addEntry(
					null,	// activityId
					ACTIVITY_VERB,	// activityVerb
					this.contentTextArea.getText(),	// title
					this.contentTextArea.getText(),	// summary
					null,	// summaryType
					this.contentTextArea.getText(),	// content
					new MimeType("text/plain"),	// contentMimeType 
					new Date(),	// publishedDate
					new Date(),	// updatedDate
					null, // editedDate
					null,	// authorName
					null,	// authorEmail
					null,	// authorUri
					ACTIVITY_OBJECTTYPE,	// activityObjectType
					null,	// activityActorGivenName
					null,	// activityActorFamilyName
					null,	// activityActorPreferredUserName
					null);	// activityActorDisplayName
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

		this.contentTextArea.setText("");
	}

	private void onSubscribeActionPerformed(ActionEvent e) {

		Logger logger = PDSApplication.getApp().getLogger();

		String hubcallback = this.feedPdsWebApp.getPubsubhubbubEndpoint() + this.context.getCanonical();
		String hubleaseseconds = this.feedPdsWebApp.getLeaseSeconds();

		// determine the user URI

		String userUri = this.subscribeTextField.getText();

		if ((! userUri.startsWith("http://")) &&
				(! userUri.startsWith("https://")) &&
				(! userUri.startsWith("acct:"))) {

			if (userUri.contains("@"))
				userUri = "acct:" + userUri;
			else
				userUri = "http://" + userUri;
		}

		// discover the topic URI

		String hubtopic = null;

		try {

			XRD xrd = XRDDiscovery.discoverXRD(URI.create(userUri));

			if (xrd != null) {

				Link link = XRDDiscovery.selectLink(xrd, "http://schemas.google.com/g/2010#updates-from", null);
				if (link == null) link = XRDDiscovery.selectLink(xrd, "alternate", "application/atom+xml");

				if (link != null) hubtopic = link.getHref();
			}

			if (hubtopic == null) {

				MessageDialog.problem("Sorry, this does not seem to be a valid user to subscribe to.", null);
				return;
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while discovering the user's feed: " + ex.getMessage(), ex);
			return;
		}

		// discover the feed's hub

		String hub;

		try {

			hub = PuSHDiscovery.getHub(hubtopic);
			if (hub == null) throw new RuntimeException("No hub found.");
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while discovering the Feed's hub: " + ex.getMessage(), ex);
			return;
		}

		// create a hub.verify_token

		String hubverifytoken = PuSHUtil.makeVerifyToken();

		// add the topic

		try {

			this.addTopic(hubtopic, hubverifytoken, hub, userUri);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while storing your Personal Data: " + ex.getMessage(), ex);
			return;
		}

		// subscribe to the topic at the hub

		try {

			logger.info("Subscribing to topic " + hubtopic + " at hub " + hub, null);

			PuSHUtil.subscribe(
					hub, 
					hubcallback, 
					hubtopic, 
					hubleaseseconds, 
					null,
					hubverifytoken);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while subscribing to the topic: " + ex.getMessage(), ex);
			return;
		}

		// done

		this.subscribeTextField.setText("");
		MessageDialog.info("Successfully subscribed!");
	}

	private void onGotoFeedActionPerformed(ActionEvent e) {

		this.entriesColumn.setContextAndAddress(this.context, this.feedAddress);
	}

	private void onGotoMentionsActionPerformed(ActionEvent e) {

		this.entriesColumn.setContextAndAddress(this.context, this.mentionsAddress);
	}

	private void addEntry(
			IRI activityId, 
			IRI activityVerb, 
			String title, 
			String summary, 
			Type summaryType, 
			String content, 
			MimeType contentMimeType, 
			Date publishedDate, 
			Date updatedDate, 
			Date editedDate, 
			String authorName,
			String authorEmail,
			IRI authorUri,
			IRI activityObjectType,
			String activityActorGivenName,
			String activityActorFamilyName,
			String activityActorPreferredUsername,
			String activityActorDisplayName) throws XdiException {

		// $add

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_ADD);
		Graph operationGraph = operation.createOperationGraph(null);
		Graph feedGraph = operationGraph.createStatement(this.subjectXri, XRI_FEED, (Graph) null).getInnerGraph();

		Subject subject = feedGraph.createSubject(new XRI3Segment(XRI_ENTRY.toString() + "$($)"));
		FeedDictionary.fromEntry(
				subject,
				activityId, 
				activityVerb, 
				title, 
				summary,
				summaryType,
				content, 
				contentMimeType, 
				publishedDate, 
				updatedDate, 
				editedDate, 
				authorName,
				authorEmail,
				authorUri,
				activityObjectType,
				activityActorGivenName,
				activityActorFamilyName,
				activityActorPreferredUsername,
				activityActorDisplayName);

		this.context.send(operation);
	}

	private void addTopic(String hubtopic, String hubverifytoken, String hub, String name) throws XdiException {

		// $add

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_ADD);
		Graph operationGraph = operation.createOperationGraph(null);
		Graph topicsGraph = operationGraph.createStatement(this.subjectXri, XRI_TOPICS, (Graph) null).getInnerGraph();
		topicsGraph.createStatement(new XRI3Segment("$(" + hubtopic + ")"), XRI_VERIFYTOKEN, hubverifytoken);
		topicsGraph.createStatement(new XRI3Segment("$(" + hubtopic + ")"), XRI_NAME, name);
		topicsGraph.createStatement(new XRI3Segment("$(" + hubtopic + ")"), XRI_HUB, hub);
		topicsGraph.createStatement(new XRI3Segment("$(" + hubtopic + ")"), new XRI3Segment("$d"), Timestamps.dateToXri(new Date()));

		this.context.send(operation);
	}

	private void setTopicVerifyToken(String hubtopic, String hubverifytoken) throws XdiException {

		// $set

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_SET);
		Graph operationGraph = operation.createOperationGraph(null);
		Graph topicsGraph = operationGraph.createStatement(this.subjectXri, XRI_TOPICS, (Graph) null).getInnerGraph();
		topicsGraph.createStatement(new XRI3Segment("$(" + hubtopic + ")"), XRI_VERIFYTOKEN, hubverifytoken);

		this.context.send(operation);
	}

	private void deleteTopic(String hubtopic) throws XdiException {

		// $del

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_DEL);
		Graph operationGraph = operation.createOperationGraph(null);
		operationGraph.createStatement(this.subjectXri, XRI_TOPICS);

		this.context.send(operation);
	}

	private class MyTopicPanelDelegate implements TopicPanelDelegate {

		@Override
		public void onTopicActionPerformed(ActionEvent e, XRI3Segment topicXri) {

			XRI3 topicAddress = new XRI3("" + FeedContentPane.this.topicsAddress + "//" + topicXri + "/+entries");

			FeedContentPane.this.entriesColumn.setContextAndAddress(FeedContentPane.this.context, topicAddress);
		}

		@Override
		public void onResubscribeActionPerformed(ActionEvent e, XRI3Segment topicXri, String hub) {

			Logger logger = PDSApplication.getApp().getLogger();

			String hubtopic = topicXri.getFirstSubSegment().getXRef().getIRI();
			String hubcallback = FeedContentPane.this.feedPdsWebApp.getPubsubhubbubEndpoint() + FeedContentPane.this.context.getCanonical();
			String hubleaseseconds = FeedContentPane.this.feedPdsWebApp.getLeaseSeconds();

			// create a hub.verify_token

			String hubverifytoken = PuSHUtil.makeVerifyToken();

			// set the hub.verify_token for the topic

			try {

				FeedContentPane.this.setTopicVerifyToken(hubtopic, hubverifytoken);
			} catch (Exception ex) {

				MessageDialog.problem("Sorry, a problem occurred while storing your Personal Data: " + ex.getMessage(), ex);
				return;
			}

			// subscribe to the topic at the hub

			try {

				logger.info("Subscribing to topic " + hubtopic + " at hub " + hub, null);

				PuSHUtil.subscribe(
						hub, 
						hubcallback, 
						hubtopic, 
						hubleaseseconds, 
						null,
						hubverifytoken);
			} catch (Exception ex) {

				MessageDialog.problem("Sorry, a problem occurred while subscribing to the topic: " + ex.getMessage(), ex);
				return;
			}

			// done

			MessageDialog.info("Successfully subscribed!");
		}

		@Override
		public void onUnsubscribeActionPerformed(ActionEvent e, XRI3Segment topicXri, String hub) {

			Logger logger = PDSApplication.getApp().getLogger();

			String hubtopic = topicXri.getFirstSubSegment().getXRef().getIRI();
			String hubcallback = FeedContentPane.this.feedPdsWebApp.getPubsubhubbubEndpoint() + FeedContentPane.this.context.getCanonical();

			// create a hub.verify_token

			String hubverifytoken = PuSHUtil.makeVerifyToken();

			// set the hub.verify_token for the topic

			try {

				FeedContentPane.this.setTopicVerifyToken(hubtopic, hubverifytoken);
			} catch (Exception ex) {

				MessageDialog.problem("Sorry, a problem occurred while storing your Personal Data: " + ex.getMessage(), ex);
				return;
			}

			// unsubscribe from the topic at the hub

			try {

				logger.info("Unsubscribing from topic " + hubtopic + " at hub " + hub, null);

				PuSHUtil.unsubscribe(
						hub, 
						hubcallback, 
						hubtopic,
						null,
						hubverifytoken);
			} catch (Exception ex) {

				MessageDialog.problem("Sorry, a problem occurred while unsubscribing from the topic: " + ex.getMessage(), ex);
				return;
			}

			// delete the topic

			try {

				FeedContentPane.this.deleteTopic(hubtopic);
			} catch (Exception ex) {

				MessageDialog.problem("Sorry, a problem occurred while storing your Personal Data: " + ex.getMessage(), ex);
				return;
			}

			// done

			MessageDialog.info("Successfully unsubscribed!");
		}
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
		row1.setInsets(new Insets(new Extent(0, Extent.PX), new Extent(0,
				Extent.PX), new Extent(0, Extent.PX), new Extent(10, Extent.PX)));
		SplitPaneLayoutData row1LayoutData = new SplitPaneLayoutData();
		row1LayoutData.setMinimumSize(new Extent(65, Extent.PX));
		row1LayoutData.setMaximumSize(new Extent(65, Extent.PX));
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
		label1.setText("Network");
		row2.add(label1);
		Row row3 = new Row();
		row3.setAlignment(new Alignment(Alignment.RIGHT, Alignment.DEFAULT));
		row1.add(row3);
		xdiPanel = new XdiPanel();
		row3.add(xdiPanel);
		SplitPane splitPane2 = new SplitPane();
		splitPane2.setStyleName("Default");
		splitPane2.setOrientation(SplitPane.ORIENTATION_VERTICAL_TOP_BOTTOM);
		splitPane2.setResizable(false);
		splitPane2.setSeparatorVisible(false);
		splitPane1.add(splitPane2);
		Column column1 = new Column();
		column1.setCellSpacing(new Extent(10, Extent.PX));
		SplitPaneLayoutData column1LayoutData = new SplitPaneLayoutData();
		column1LayoutData.setMinimumSize(new Extent(100, Extent.PX));
		column1LayoutData.setMaximumSize(new Extent(100, Extent.PX));
		column1.setLayoutData(column1LayoutData);
		splitPane2.add(column1);
		Row row5 = new Row();
		row5.setCellSpacing(new Extent(10, Extent.PX));
		column1.add(row5);
		Label label4 = new Label();
		label4.setStyleName("Default");
		label4.setText("What's up?");
		row5.add(label4);
		contentTextArea = new TextArea();
		contentTextArea.setStyleName("Default");
		contentTextArea.setHeight(new Extent(50, Extent.PX));
		contentTextArea.setFont(new Font(new Font.Typeface("sans-serif"),
				Font.PLAIN, new Extent(12, Extent.PT)));
		contentTextArea.setWidth(new Extent(400, Extent.PX));
		row5.add(contentTextArea);
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
		Row row4 = new Row();
		row4.setCellSpacing(new Extent(10, Extent.PX));
		column1.add(row4);
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
		button2.setText("Subscribe!");
		button2.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onSubscribeActionPerformed(e);
			}
		});
		row4.add(button2);
		SplitPane splitPane3 = new SplitPane();
		splitPane3.setStyleName("Default");
		ResourceImageReference imageReference2 = new ResourceImageReference(
				"/pds/web/resource/image/separator-blue.png");
		splitPane3.setSeparatorHorizontalImage(new FillImage(imageReference2));
		splitPane3.setOrientation(SplitPane.ORIENTATION_HORIZONTAL_LEFT_RIGHT);
		splitPane3.setSeparatorWidth(new Extent(10, Extent.PX));
		splitPane3.setResizable(true);
		splitPane3.setSeparatorVisible(true);
		splitPane2.add(splitPane3);
		Column column2 = new Column();
		column2.setCellSpacing(new Extent(10, Extent.PX));
		splitPane3.add(column2);
		Row row6 = new Row();
		row6.setCellSpacing(new Extent(10, Extent.PX));
		column2.add(row6);
		Button button4 = new Button();
		button4.setStyleName("Default");
		button4.setText("YOUR Messages");
		button4.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onGotoFeedActionPerformed(e);
			}
		});
		row6.add(button4);
		Button button3 = new Button();
		button3.setStyleName("Default");
		button3.setText("Messages about YOU");
		button3.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onGotoMentionsActionPerformed(e);
			}
		});
		row6.add(button3);
		topicsColumn = new TopicsColumn();
		column2.add(topicsColumn);
		entriesColumn = new EntriesColumn();
		splitPane3.add(entriesColumn);
	}
}
