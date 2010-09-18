package pds.web.ui.app.feed.components;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.Button;
import nextapp.echo.app.Column;
import nextapp.echo.app.Extent;
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

import pds.web.components.xdi.XdiPanel;
import pds.web.ui.MessageDialog;
import pds.xdi.XdiContext;
import pds.xdi.XdiException;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import nextapp.echo.app.Insets;

public class EntryPanel extends Panel implements XdiGraphListener {

	private static final long serialVersionUID = -6674403250232180782L;

	private static final DateFormat DATEFORMAT = new SimpleDateFormat("HH:mm:ss.SSS");

	protected ResourceBundle resourceBundle;

	private XdiContext context;
	private XRI3 address;
	private XRI3 timestampAddress;
	private XRI3 titleAddress;
	private XRI3 contentAddress;

	private XdiPanel xdiPanel;
	private Button replyButton;
	private EntryPanelDelegate entryPanelDelegate;
	private Label timestampLabel;
	private Label titleLabel;

	private Label nameLabel;

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

			Date timestamp = this.getTimestamp();
			String title = this.getTitle();
			String content = this.getContent();

			if (timestamp != null) this.timestampLabel.setText(DATEFORMAT.format(timestamp));
			if (title != null) this.titleLabel.setText(title);
			if (content != null) this.nameLabel.setText(content);
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
		this.timestampAddress = new XRI3("" + this.address + "/$d");
		this.titleAddress = new XRI3("" + this.address + "/+title");
		this.contentAddress = new XRI3("" + this.address + "/+content");

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

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, this.timestampAddress);
		MessageResult messageResult = this.context.send(operation);

		XRI3Segment referenceXri = Addressing.findReferenceXri(messageResult.getGraph(), this.timestampAddress);

		try {

			return referenceXri == null ? null : Timestamps.xriToDate(referenceXri);
		} catch (ParseException ex) {

			throw new XdiException("Cannot parse timestamp: " + ex.getMessage(), ex);
		}
	}

	private String getTitle() throws XdiException {

		// $get

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, this.titleAddress);
		MessageResult messageResult = this.context.send(operation);

		return Addressing.findLiteralData(messageResult.getGraph(), this.titleAddress);
	}

	private String getContent() throws XdiException {

		// $get

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, this.contentAddress);
		MessageResult messageResult = this.context.send(operation);

		return Addressing.findLiteralData(messageResult.getGraph(), this.contentAddress);
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setInsets(new Insets(new Extent(5, Extent.PX)));
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
		nameLabel = new Label();
		nameLabel.setStyleName("Default");
		nameLabel.setText("...");
		column1.add(nameLabel);
		timestampLabel = new Label();
		timestampLabel.setStyleName("Default");
		timestampLabel.setText("...");
		column1.add(timestampLabel);
		titleLabel = new Label();
		titleLabel.setStyleName("Bold");
		titleLabel.setText("...");
		column1.add(titleLabel);
		replyButton = new Button();
		replyButton.setStyleName("Plain");
		replyButton.setText("Reply");
		replyButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onReplyActionPerformed(e);
			}
		});
		row1.add(replyButton);
	}
}
