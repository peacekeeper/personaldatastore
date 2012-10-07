package pds.web.ui.app.addressbook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import pds.web.PDSApplication;
import pds.web.components.xdi.XdiPanel;
import pds.web.ui.MessageDialog;
import pds.web.ui.shared.DataAttributeColumn;
import pds.web.ui.shared.FriendPanel;
import pds.web.ui.shared.FriendPanel.FriendPanelDelegate;
import pds.xdi.XdiClient;
import pds.xdi.XdiEndpoint;
import pds.xdi.XdiException;
import pds.xdi.events.XdiGraphAddEvent;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import pds.xdi.events.XdiGraphModEvent;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingRelationArcXriIterator;
import xdi2.core.xri3.impl.XRI3;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.constants.XDIMessagingConstants;
import echopoint.ImageIcon;

public class AddressBookContentPane extends ContentPane implements XdiGraphListener {

	private static final long serialVersionUID = -8925773333194027452L;

	protected ResourceBundle resourceBundle;

	private XdiEndpoint endpoint;
	private XRI3Segment contextNodeXri;
	private XRI3Segment address;

	private XdiPanel xdiPanel;
	private TextField addTextField;
	private Button addButton;
	private Button cancelButton;
	private Column friendsColumn;
	private DataAttributeColumn dataAttributeColumn;

	/**
	 * Creates a new <code>AddressBookContentPane</code>.
	 */
	public AddressBookContentPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void init() {

		super.init();

		this.dataAttributeColumn.setReadOnly(true);
	}

	@Override
	public void dispose() {

		super.dispose();

		// remove us as listener
		
		if (this.endpoint != null) this.endpoint.removeXdiGraphListener(this);
	}

	private void refresh() {

		try {

			this.xdiPanel.setEndpointAndGraphListener(this.endpoint, this);

			// get list of friend XRIs

			List<XRI3Segment> friendXris;

			friendXris = this.getFriendXris();

			// add them

			this.friendsColumn.removeAll();
			for (XRI3Segment friendXri : friendXris) {

				this.addFriendPanel(friendXri);
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	private void addFriendPanel(final XRI3Segment friendXri) {

		FriendPanel friendPanel = new FriendPanel();
		friendPanel.setEndpointAndContextNodeXriAndTargetContextNodeXri(this.endpoint, this.contextNodeXri, friendXri);
		friendPanel.setFriendPanelDelegate(new FriendPanelDelegate() {

			@Override
			public void onFriendActionPerformed(ActionEvent e) {

				XdiEndpoint endpoint;

				try {

					XdiClient xdiClient = PDSApplication.getApp().getXdiClient();
					endpoint = xdiClient.resolveEndpointByIname(friendXri.toString(), null);
				} catch (Exception ex) {

					MessageDialog.problem("Sorry, we could not open the Personal Cloud: " + ex.getMessage(), ex);
					return;
				}

				AddressBookContentPane.this.dataAttributeColumn.setEndpointAndContextNodeXri(endpoint, endpoint.getCanonical());
			}
		});

		this.friendsColumn.add(friendPanel);
	}

	public XRI3Segment[] xdiGetAddresses() {

		return new XRI3Segment[] {
				this.address
		};
	}

	public XRI3Segment[] xdiAddAddresses() {

		return new XRI3Segment[] {
				this.address
		};
	}

	public XRI3Segment[] xdiModAddresses() {

		return new XRI3Segment[0];
	}

	public XRI3Segment[] xdiDelAddresses() {

		return new XRI3Segment[] {
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
		
		if (this.endpoint != null) this.endpoint.removeXdiGraphListener(this);

		// refresh
		
		this.endpoint = context;
		this.contextNodeXri = subjectXri;
		this.address = new XRI3("" + this.contextNodeXri + "/+friend");

		this.refresh();

		// add us as listener

		this.endpoint.addXdiGraphListener(this);
	}

	private void onAddActionPerformed(ActionEvent e) {

		if (this.addTextField.isVisible()) {

			String friend = this.addTextField.getText();
			if (friend == null || friend.trim().equals("")) return;

			try {

				this.addFriendXri(new XRI3Segment(friend));
			} catch (Exception ex) {

				MessageDialog.problem("Sorry, a problem occurred while storing your Personal Data: " + ex.getMessage(), ex);
				return;
			}

			this.addTextField.setVisible(false);
			this.cancelButton.setVisible(false);
		} else {

			this.addTextField.setText("");

			this.addTextField.setVisible(true);
			this.cancelButton.setVisible(true);
		}
	}

	private void onCancelActionPerformed(ActionEvent e) {

		this.addTextField.setVisible(false);
		this.cancelButton.setVisible(false);
	}

	private List<XRI3Segment> getFriendXris() throws XdiException {

		Message message = this.endpoint.prepareOperation(XDIMessagingConstants.XRI_S_GET, this.address);
		MessageResult messageResult = this.endpoint.send(message);

		ContextNode contextNode = messageResult.getGraph().findContextNode(this.contextNodeXri, false);
		if (contextNode == null) return new ArrayList<XRI3Segment> ();

		Iterator<Relation> relations = contextNode.getRelations(new XRI3Segment("+friend"));
		Iterator<XRI3Segment> relationArcXris = new MappingRelationArcXriIterator(relations);

		return new IteratorListMaker<XRI3Segment> (relationArcXris).list();
	}

	private void addFriendXri(XRI3Segment friendXri) throws XdiException {

		// $add

		Operation operation = this.endpoint.prepareOperation(MessagingConstants.XRI_ADD);
		Graph operationGraph = operation.createOperationGraph(null);
		operationGraph.createStatement(this.contextNodeXri, new XRI3Segment("+friend"), friendXri);
		this.endpoint.send(operation);
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
				"/pds/web/ui/app/addressbook/app.png");
		imageIcon2.setIcon(imageReference1);
		imageIcon2.setHeight(new Extent(48, Extent.PX));
		imageIcon2.setWidth(new Extent(48, Extent.PX));
		row2.add(imageIcon2);
		Label label1 = new Label();
		label1.setStyleName("Header");
		label1.setText("Address Book");
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
		Row row4 = new Row();
		row4.setCellSpacing(new Extent(10, Extent.PX));
		column1.add(row4);
		Label label2 = new Label();
		label2.setStyleName("Bold");
		label2.setText("Friends:");
		row4.add(label2);
		addTextField = new TextField();
		addTextField.setStyleName("Default");
		addTextField.setVisible(false);
		addTextField.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onAddActionPerformed(e);
			}
		});
		row4.add(addTextField);
		addButton = new Button();
		addButton.setStyleName("Plain");
		ResourceImageReference imageReference3 = new ResourceImageReference(
				"/pds/web/resource/image/op-add.png");
		addButton.setIcon(imageReference3);
		addButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onAddActionPerformed(e);
			}
		});
		row4.add(addButton);
		cancelButton = new Button();
		cancelButton.setStyleName("Plain");
		ResourceImageReference imageReference4 = new ResourceImageReference(
				"/pds/web/resource/image/op-cancel.png");
		cancelButton.setIcon(imageReference4);
		cancelButton.setVisible(false);
		cancelButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onCancelActionPerformed(e);
			}
		});
		row4.add(cancelButton);
		friendsColumn = new Column();
		friendsColumn.setCellSpacing(new Extent(10, Extent.PX));
		column1.add(friendsColumn);
		dataAttributeColumn = new DataPredicatesColumn();
		dataAttributeColumn.setInsets(new Insets(new Extent(10, Extent.PX),
				new Extent(0, Extent.PX), new Extent(0, Extent.PX), new Extent(
						0, Extent.PX)));
		splitPane2.add(dataAttributeColumn);
	}
}
