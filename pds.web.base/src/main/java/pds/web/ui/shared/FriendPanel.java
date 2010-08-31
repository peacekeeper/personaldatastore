package pds.web.ui.shared;

import java.util.ResourceBundle;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.Button;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Panel;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.RowLayoutData;

import org.eclipse.higgins.xdi4j.addressing.Addressing;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.components.xdi.XdiPanel;
import pds.web.ui.MessageDialog;
import pds.web.xdi.XdiContext;
import pds.web.xdi.XdiException;
import pds.web.xdi.events.XdiGraphDelEvent;
import pds.web.xdi.events.XdiGraphEvent;
import pds.web.xdi.events.XdiGraphListener;
import echopoint.ImageIcon;

public class FriendPanel extends Panel implements XdiGraphListener {

	private static final long serialVersionUID = -6674403250232180782L;

	protected ResourceBundle resourceBundle;

	private XdiContext context;
	private XRI3Segment subjectXri;
	private XRI3Segment referenceXri;
	private XRI3 address;

	private XdiPanel xdiPanel;
	private Button friendButton;
	private Button deleteButton;

	private FriendPanelDelegate friendPanelDelegate;

	/**
	 * Creates a new <code>AccountPersonaPanel</code>.
	 */
	public FriendPanel() {
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

			String friend = this.referenceXri.toString();

			this.xdiPanel.setContextAndMainAddressAndGetAddresses(this.context, this.address, this.xdiGetAddresses());
			this.friendButton.setText(friend);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your personal data: " + ex.getMessage(), ex);
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

			MessageDialog.problem("Sorry, a problem occurred while retrieving your personal data: " + ex.getMessage(), ex);
			return;
		}
	}

	public void setContextAndSubjectXriAndReferenceXri(XdiContext context, XRI3Segment subjectXri, XRI3Segment referenceXri) {

		// remove us as listener
		
		if (this.context != null) this.context.removeXdiGraphListener(this);

		// refresh
		
		this.context = context;
		this.subjectXri = subjectXri;
		this.referenceXri = referenceXri;
		this.address = new XRI3("" + this.subjectXri + "/+friend/" + this.referenceXri);

		this.refresh();

		// add us as listener

		this.context.addXdiGraphListener(this);
	}

	public void setFriendPanelDelegate(FriendPanelDelegate friendPanelDelegate) {

		this.friendPanelDelegate = friendPanelDelegate;
	}

	public FriendPanelDelegate getFriendPanelDelegate() {

		return this.friendPanelDelegate;
	}

	private void onFriendActionPerformed(ActionEvent e) {

		if (this.friendPanelDelegate != null) {

			this.friendPanelDelegate.onFriendActionPerformed(e);
		}
	}

	private void onDeleteActionPerformed(ActionEvent e) {

		try {

			// $del

			Operation operation = this.context.prepareOperation(MessagingConstants.XRI_DEL);
			operation.createOperationGraph(Addressing.convertAddressToGraph(this.address));

			this.context.send(operation);
		} catch (XdiException ex) {

			MessageDialog.problem("Sorry, a problem occurred while storing your personal data: " + ex.getMessage(), ex);
			return;
		}
	}

	public static interface FriendPanelDelegate {

		public void onFriendActionPerformed(ActionEvent e);
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
		"/pds/web/resource/image/friend.png");
		imageIcon1.setIcon(imageReference1);
		imageIcon1.setHeight(new Extent(48, Extent.PX));
		imageIcon1.setWidth(new Extent(48, Extent.PX));
		row1.add(imageIcon1);
		friendButton = new Button();
		friendButton.setStyleName("Plain");
		friendButton.setText("...");
		friendButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onFriendActionPerformed(e);
			}
		});
		row1.add(friendButton);
		deleteButton = new Button();
		deleteButton.setStyleName("Plain");
		ResourceImageReference imageReference2 = new ResourceImageReference(
		"/pds/web/resource/image/op-cancel.png");
		deleteButton.setIcon(imageReference2);
		RowLayoutData deleteButtonLayoutData = new RowLayoutData();
		deleteButtonLayoutData.setAlignment(new Alignment(Alignment.DEFAULT,
				Alignment.CENTER));
		deleteButton.setLayoutData(deleteButtonLayoutData);
		deleteButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onDeleteActionPerformed(e);
			}
		});
		row1.add(deleteButton);
	}
}
