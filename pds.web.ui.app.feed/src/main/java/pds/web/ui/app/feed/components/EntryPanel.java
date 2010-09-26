package pds.web.ui.app.feed.components;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.ResourceBundle;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.Border;
import nextapp.echo.app.Button;
import nextapp.echo.app.Color;
import nextapp.echo.app.Column;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.Panel;
import nextapp.echo.app.Row;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.RowLayoutData;

import org.eclipse.higgins.xdi4j.addressing.Addressing;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.types.Timestamps;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.dictionary.feed.FeedDictionary;
import pds.web.components.HtmlLabel;
import pds.web.components.xdi.XdiPanel;
import pds.web.ui.MessageDialog;
import pds.xdi.XdiContext;
import pds.xdi.XdiException;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;

public class EntryPanel extends Panel implements XdiGraphListener {

	private static final long serialVersionUID = -6674403250232180782L;

	private static final DateFormat DATEFORMAT = DateFormat.getDateTimeInstance();

	protected ResourceBundle resourceBundle;

	private XdiContext context;
	private XRI3 address;

	private XdiPanel xdiPanel;
	private Button replyButton;
	private EntryPanelDelegate entryPanelDelegate;
	private Label titleLabel;
	private Label nameLabel;
	private Label timestampLabel;
	private HtmlLabel contentHtmlLabel;

	/**
	 * Creates a new <code>AccountPersonaPanel</code>.
	 */
	public EntryPanel() {
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

			String name = this.getName();
			Date timestamp = this.getTimestamp();
			String title = this.getTitle();
			String content = this.getContent();

			if (name != null) this.nameLabel.setText(name);
			if (timestamp != null) this.timestampLabel.setText(DATEFORMAT.format(timestamp));
			if (title != null) this.titleLabel.setText(title);
			if (content != null) this.contentHtmlLabel.setHtml(content);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
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

	public XRI3[] xdiSetAddresses() {

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

			if (xdiGraphEvent instanceof XdiGraphDelEvent) {

				this.getParent().remove(this);
				return;
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	public void setContextAndAddress(XdiContext context, XRI3 address) {

		// remove us as listener

		if (this.context != null) this.context.removeXdiGraphListener(this);

		// refresh

		this.context = context;
		this.address = address;

		this.xdiPanel.setContextAndMainAddressAndGetAddresses(this.context, this.address, this.xdiGetAddresses());

		this.refresh();

		// add us as listener

		this.context.addXdiGraphListener(this);
	}

	public void setEntryPanelDelegate(EntryPanelDelegate entryPanelDelegate) {

		this.entryPanelDelegate = entryPanelDelegate;
	}

	public EntryPanelDelegate getEntryPanelDelegate() {

		return this.entryPanelDelegate;
	}

	private void onReplyActionPerformed(ActionEvent e) {

		if (this.entryPanelDelegate != null) {

			this.entryPanelDelegate.onReplyActionPerformed(e);
		}
	}

	public static interface EntryPanelDelegate {

		public void onReplyActionPerformed(ActionEvent e);
	}

	private Date getTimestamp() throws XdiException {

		// $get

		XRI3 address = new XRI3("" + this.address + "/" + FeedDictionary.XRI_PUBLISHED_DATE);
		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, address);
		MessageResult messageResult = this.context.send(operation);

		XRI3Segment referenceXri = Addressing.findReferenceXri(messageResult.getGraph(), address);

		try {

			return referenceXri == null ? null : Timestamps.xriToDate(referenceXri);
		} catch (ParseException ex) {

			throw new XdiException("Cannot parse timestamp: " + ex.getMessage(), ex);
		}
	}

	private String getName() throws XdiException {

		// $get

		XRI3 addressAuthorName = new XRI3("" + this.address + "/" + FeedDictionary.XRI_AUTHOR_NAME);
		XRI3 addressActivityActorDisplayName = new XRI3("" + this.address + "/" + FeedDictionary.XRI_ACTIVITY_ACTOR_DISPLAY_NAME);
		XRI3 addressActivityActorGivenName = new XRI3("" + this.address + "/" + FeedDictionary.XRI_ACTIVITY_ACTOR_GIVEN_NAME);
		XRI3 addressActivityActorFamilyName = new XRI3("" + this.address + "/" + FeedDictionary.XRI_ACTIVITY_ACTOR_FAMILY_NAME);
		XRI3[] addresses = new XRI3[] { addressAuthorName, addressActivityActorDisplayName, addressActivityActorGivenName, addressActivityActorFamilyName };
		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, addresses);
		MessageResult messageResult = this.context.send(operation);

		String authorName = Addressing.findLiteralData(messageResult.getGraph(), addressAuthorName);
		if (authorName != null) return authorName;

		String activityActorDisplayName = Addressing.findLiteralData(messageResult.getGraph(), addressActivityActorDisplayName);
		if (activityActorDisplayName != null) return activityActorDisplayName;

		String activityActorGivenName = Addressing.findLiteralData(messageResult.getGraph(), addressActivityActorGivenName);
		String activityActorFamilyName = Addressing.findLiteralData(messageResult.getGraph(), addressActivityActorFamilyName);
		if (activityActorGivenName != null && activityActorFamilyName != null) return activityActorGivenName + activityActorFamilyName;
		if (activityActorFamilyName != null) return activityActorFamilyName;
		if (activityActorGivenName != null) return activityActorGivenName;

		return null;
	}

	private String getTitle() throws XdiException {

		// $get

		XRI3 address = new XRI3("" + this.address + "/" + FeedDictionary.XRI_TITLE);
		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, address);
		MessageResult messageResult = this.context.send(operation);

		return Addressing.findLiteralData(messageResult.getGraph(), address);
	}

	private String getContent() throws XdiException {

		// $get

		XRI3 address = new XRI3("" + this.address + "/" + FeedDictionary.XRI_CONTENT);
		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, address);
		MessageResult messageResult = this.context.send(operation);

		return Addressing.findLiteralData(messageResult.getGraph(), address);
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setInsets(new Insets(new Extent(5, Extent.PX)));
		this.setBorder(new Border(new Border.Side[] {
				new Border.Side(new Extent(1, Extent.PX), Color.BLACK,
						Border.STYLE_NONE),
				new Border.Side(new Extent(1, Extent.PX), Color.BLACK,
						Border.STYLE_NONE),
				new Border.Side(new Extent(2, Extent.PX), new Color(0xbabdb6),
						Border.STYLE_DASHED),
				new Border.Side(new Extent(1, Extent.PX), Color.BLACK,
						Border.STYLE_NONE) }));
		Row row1 = new Row();
		row1.setCellSpacing(new Extent(10, Extent.PX));
		add(row1);
		xdiPanel = new XdiPanel();
		RowLayoutData xdiPanelLayoutData = new RowLayoutData();
		xdiPanelLayoutData.setAlignment(new Alignment(Alignment.DEFAULT,
				Alignment.CENTER));
		xdiPanel.setLayoutData(xdiPanelLayoutData);
		row1.add(xdiPanel);
		Column column1 = new Column();
		column1.setCellSpacing(new Extent(5, Extent.PX));
		row1.add(column1);
		Row row2 = new Row();
		row2.setCellSpacing(new Extent(10, Extent.PX));
		column1.add(row2);
		nameLabel = new Label();
		nameLabel.setStyleName("Default");
		nameLabel.setText("...");
		row2.add(nameLabel);
		timestampLabel = new Label();
		timestampLabel.setStyleName("Default");
		timestampLabel.setText("...");
		row2.add(timestampLabel);
		titleLabel = new Label();
		titleLabel.setStyleName("Bold");
		titleLabel.setText("...");
		column1.add(titleLabel);
		contentHtmlLabel = new HtmlLabel();
		contentHtmlLabel.setHtml("    ");
		column1.add(contentHtmlLabel);
		replyButton = new Button();
		replyButton.setStyleName("Plain");
		replyButton.setText("Reply");
		replyButton.setVisible(false);
		replyButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onReplyActionPerformed(e);
			}
		});
		row1.add(replyButton);
	}
}
