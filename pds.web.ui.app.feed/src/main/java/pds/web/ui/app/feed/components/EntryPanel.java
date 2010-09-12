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
import nextapp.echo.app.ResourceImageReference;
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
import pds.xdi.XdiNotExistentException;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import echopoint.ImageIcon;

public class EntryPanel extends Panel implements XdiGraphListener {

	private static final long serialVersionUID = -6674403250232180782L;

	private static final XRI3Segment XRI_FEED = new XRI3Segment("+ostatus+feed");

	private static final DateFormat DATEFORMAT = new SimpleDateFormat("HH:mm:ss.SSS");

	protected ResourceBundle resourceBundle;

	private XdiContext context;
	private XRI3Segment subjectXri;
	private XRI3Segment entryXri;
	private XRI3 address;
	private XRI3 timestampAddress;
	private XRI3 contentAddress;

	private XdiPanel xdiPanel;
	private Button replyButton;
	private EntryPanelDelegate entryPanelDelegate;
	private Label timestampLabel;
	private Label contentLabel;

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
			String content = this.getContent();

			this.timestampLabel.setText(DATEFORMAT.format(timestamp));
			this.contentLabel.setText(content);
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

	public void setContextAndSubjectXriAndEntryXri(XdiContext context, XRI3Segment subjectXri, XRI3Segment entryXri) {

		// remove us as listener
		
		if (this.context != null) this.context.removeXdiGraphListener(this);

		// refresh
		
		this.context = context;
		this.subjectXri = subjectXri;
		this.entryXri = entryXri;
		this.address = new XRI3("" + this.subjectXri + "/" + XRI_FEED + "//" + this.entryXri);
		this.timestampAddress = new XRI3("" + this.address + "/$d");
		this.contentAddress = new XRI3("" + this.address + "/$a$string");

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
		if (referenceXri == null) throw new XdiNotExistentException();

		try {

			return Timestamps.xriToDate(referenceXri);
		} catch (ParseException ex) {

			throw new XdiException("Cannot parse timestamp: " + ex.getMessage(), ex);
		}
	}

	private String getContent() throws XdiException {

		// $get

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, this.contentAddress);
		MessageResult messageResult = this.context.send(operation);

		String data = Addressing.findLiteralData(messageResult.getGraph(), this.contentAddress);
		if (data == null) throw new XdiNotExistentException();

		return data;
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		Row row1 = new Row();
		row1.setCellSpacing(new Extent(10, Extent.PX));
		add(row1);
		xdiPanel = new XdiPanel();
		RowLayoutData xdiPanelLayoutData = new RowLayoutData();
		xdiPanelLayoutData.setAlignment(new Alignment(Alignment.DEFAULT,
				Alignment.CENTER));
		xdiPanel.setLayoutData(xdiPanelLayoutData);
		row1.add(xdiPanel);
		ImageIcon imageIcon1 = new ImageIcon();
		ResourceImageReference imageReference1 = new ResourceImageReference(
				"/pds/web/resource/image/app-feed.png");
		imageIcon1.setIcon(imageReference1);
		imageIcon1.setHeight(new Extent(48, Extent.PX));
		imageIcon1.setWidth(new Extent(48, Extent.PX));
		row1.add(imageIcon1);
		Column column1 = new Column();
		column1.setCellSpacing(new Extent(5, Extent.PX));
		row1.add(column1);
		timestampLabel = new Label();
		timestampLabel.setStyleName("Default");
		timestampLabel.setText("...");
		column1.add(timestampLabel);
		contentLabel = new Label();
		contentLabel.setStyleName("Default");
		contentLabel.setText("...");
		column1.add(contentLabel);
		replyButton = new Button();
		replyButton.setStyleName("Plain");
		replyButton.setText("...");
		replyButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onReplyActionPerformed(e);
			}
		});
		row1.add(replyButton);
	}
}