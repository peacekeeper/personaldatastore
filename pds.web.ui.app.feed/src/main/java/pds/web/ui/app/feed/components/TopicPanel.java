package pds.web.ui.app.feed.components;

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

public class TopicPanel extends Panel implements XdiGraphListener {

	private static final long serialVersionUID = -6674403250232180782L;

	protected ResourceBundle resourceBundle;

	private XdiContext context;
	private XRI3Segment subjectXri;
	private XRI3Segment topicXri;
	private XRI3 address;
	private XRI3 hubAddress;
	private XRI3 subscribedAddress;
	private XRI3 verifyTokenAddress;

	private XdiPanel xdiPanel;
	private TopicPanelDelegate topicPanelDelegate;
	private Label hubLabel;
	private Label subscribedLabel;

	private Label verifyTokenLabel;

	/**
	 * Creates a new <code>AccountPersonaPanel</code>.
	 */
	public TopicPanel() {
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

			String hub = this.getHub();
			String subscribed = this.getSubscribed();
			String verifyToken = this.getVerifyToken();

			this.hubLabel.setText(hub);
			this.subscribedLabel.setText(subscribed);
			this.verifyTokenLabel.setText(verifyToken);
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

	public void setContextAndSubjectXriAndTopicXri(XdiContext context, XRI3Segment subjectXri, XRI3Segment topicXri) {

		// remove us as listener

		if (this.context != null) this.context.removeXdiGraphListener(this);

		// refresh

		this.context = context;
		this.subjectXri = subjectXri;
		this.topicXri = topicXri;
		this.address = new XRI3("" + this.subjectXri + "/+ostatus+topics//" + this.topicXri);
		this.hubAddress = new XRI3("" + this.address + "/+push+hub");
		this.subscribedAddress = new XRI3("" + this.address + "/+push+subscribed");
		this.verifyTokenAddress = new XRI3("" + this.address + "/+push+verify.token");

		this.xdiPanel.setContextAndMainAddressAndGetAddresses(this.context, this.address, this.xdiGetAddresses());

		this.refresh();

		// add us as listener

		this.context.addXdiGraphListener(this);
	}

	public void setTopicPanelDelegate(TopicPanelDelegate topicPanelDelegate) {

		this.topicPanelDelegate = topicPanelDelegate;
	}

	public TopicPanelDelegate getTopicPanelDelegate() {

		return this.topicPanelDelegate;
	}

	private void onResubscribeActionPerformed(ActionEvent e) {

		if (this.topicPanelDelegate != null) {

			this.topicPanelDelegate.onResubscribeActionPerformed(e);
		}
	}

	private void onUnsubscribeButton(ActionEvent e) {

		if (this.topicPanelDelegate != null) {

			this.topicPanelDelegate.onUnsubscribeActionPerformed(e);
		}
	}

	public static interface TopicPanelDelegate {

		public void onResubscribeActionPerformed(ActionEvent e);
		public void onUnsubscribeActionPerformed(ActionEvent e);
	}

	private String getHub() throws XdiException {

		// $get

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, this.hubAddress);
		MessageResult messageResult = this.context.send(operation);

		String data = Addressing.findLiteralData(messageResult.getGraph(), this.hubAddress);
		if (data == null) throw new XdiNotExistentException();

		return data;
	}

	private String getSubscribed() throws XdiException {

		// $get

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, this.subscribedAddress);
		MessageResult messageResult = this.context.send(operation);

		String data = Addressing.findLiteralData(messageResult.getGraph(), this.subscribedAddress);
		if (data == null) throw new XdiNotExistentException();

		return data;
	}

	private String getVerifyToken() throws XdiException {

		// $get

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, this.verifyTokenAddress);
		MessageResult messageResult = this.context.send(operation);

		String data = Addressing.findLiteralData(messageResult.getGraph(), this.verifyTokenAddress);
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
		Row row2 = new Row();
		row2.setCellSpacing(new Extent(10, Extent.PX));
		column1.add(row2);
		Label label1 = new Label();
		label1.setStyleName("Default");
		label1.setText("Hub: ");
		row2.add(label1);
		hubLabel = new Label();
		hubLabel.setStyleName("Default");
		hubLabel.setText("...");
		row2.add(hubLabel);
		Row row3 = new Row();
		row3.setCellSpacing(new Extent(10, Extent.PX));
		column1.add(row3);
		Label label2 = new Label();
		label2.setStyleName("Default");
		label2.setText("Subscribed:");
		row3.add(label2);
		subscribedLabel = new Label();
		subscribedLabel.setStyleName("Default");
		subscribedLabel.setText("...");
		row3.add(subscribedLabel);
		Row row5 = new Row();
		row5.setCellSpacing(new Extent(10, Extent.PX));
		column1.add(row5);
		Label label3 = new Label();
		label3.setStyleName("Default");
		label3.setText("Verify Token:");
		row5.add(label3);
		verifyTokenLabel = new Label();
		verifyTokenLabel.setStyleName("Default");
		verifyTokenLabel.setText("...");
		row5.add(verifyTokenLabel);
		Row row4 = new Row();
		row4.setCellSpacing(new Extent(10, Extent.PX));
		column1.add(row4);
		Button button2 = new Button();
		button2.setStyleName("Default");
		button2.setText("Resubscribe");
		button2.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onResubscribeActionPerformed(e);
			}
		});
		row4.add(button2);
		Button button1 = new Button();
		button1.setStyleName("Plain");
		button1.setText("Unsubscribe");
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onUnsubscribeButton(e);
			}
		});
		row4.add(button1);
	}
}
