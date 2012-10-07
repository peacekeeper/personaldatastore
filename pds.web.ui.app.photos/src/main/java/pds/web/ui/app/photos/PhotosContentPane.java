package pds.web.ui.app.photos;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
import nextapp.echo.app.Panel;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.TextField;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.ColumnLayoutData;
import nextapp.echo.app.layout.SplitPaneLayoutData;
import nextapp.echo.filetransfer.app.UploadSelect;
import nextapp.echo.filetransfer.app.event.UploadEvent;
import nextapp.echo.filetransfer.app.event.UploadListener;
import nextapp.echo.filetransfer.model.Upload;

import org.apache.commons.codec.binary.Base64;

import pds.web.PDSApplication;
import pds.web.components.xdi.XdiPanel;
import pds.web.ui.MainWindow;
import pds.web.ui.MessageDialog;
import pds.web.ui.app.photos.components.PhotosColumn;
import pds.web.ui.shared.FriendPanel;
import pds.web.ui.shared.FriendPanel.FriendPanelDelegate;
import pds.web.util.MimeTypeUtil;
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
import xdi2.core.Statement;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.XDIUtil;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingRelationArcXriIterator;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.constants.XDIMessagingConstants;
import echopoint.ImageIcon;

public class PhotosContentPane extends ContentPane implements XdiGraphListener {

	private static final long serialVersionUID = -8925773333194027452L;

	protected ResourceBundle resourceBundle;

	private XdiEndpoint endpoint;
	private XRI3Segment contextNodeXri;
	private XRI3Segment address;
	private XRI3Segment friendAddress;
	private XRI3Segment photosAddress;

	private XdiPanel xdiPanel;
	private TextField addTextField;
	private Button addButton;
	private Column friendsColumn;
	private PhotosColumn photosColumn;
	private TextField titleTextField;

	private byte[] tempBytes;
	private String tempMimeType;

	private Button cancelButton;

	/**
	 * Creates a new <code>AddressBookContentPane</code>.
	 */
	public PhotosContentPane() {
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

		if (this.endpoint != null) this.endpoint.removeXdiGraphListener(this);
	}

	private void refresh() {

		try {

			this.xdiPanel.setEndpointAndMainAddressAndGetAddresses(this.endpoint, this.address, this.xdiGetAddresses());

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
		friendPanel.setEndpointAndContextNodeXriAndRelationXri(this.endpoint, this.contextNodeXri, friendXri);
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

				PhotosContentPane.this.photosColumn.setEndpointAndContextNodeXri(endpoint, endpoint.getCanonical());
			}
		});

		this.friendsColumn.add(friendPanel);
	}

	public XRI3Segment[] xdiGetAddresses() {

		return new XRI3Segment[] {
				this.friendAddress,
				this.photosAddress
		};
	}

	public XRI3Segment[] xdiAddAddresses() {

		return new XRI3Segment[] {
				new XRI3Segment("" + this.friendAddress + "/$$"),
				new XRI3Segment("" + this.photosAddress + "/$$")
		};
	}

	public XRI3Segment[] xdiModAddresses() {

		return new XRI3Segment[0];
	}

	public XRI3Segment[] xdiSetAddresses() {

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

	public void setContextAndSubjectXri(XdiEndpoint endpoint, XRI3Segment contextNodeXri) {

		// remove us as listener

		if (this.endpoint != null) this.endpoint.removeXdiGraphListener(this);

		// refresh

		this.endpoint = endpoint;
		this.contextNodeXri = contextNodeXri;
		this.address = new XRI3Segment("" + this.contextNodeXri);
		this.friendAddress = new XRI3Segment("" + this.contextNodeXri + "/+friend");
		this.photosAddress = new XRI3Segment("" + this.contextNodeXri + "/+photos");

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

	private void onImageFileUploadComplete(UploadEvent e) {

		// read the uploaded file

		Upload upload = e.getUpload();
		byte[] bytes;
		String mimeType;

		try {

			DataInputStream stream = new DataInputStream(upload.getInputStream());
			bytes = new byte[stream.available()];
			stream.readFully(bytes);
			stream.close();
			mimeType = MimeTypeUtil.guessMimeTypeFromFilename(upload.getFileName());
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, we could not receive the upload: " + ex.getMessage(), ex);
			return;
		}

		this.tempBytes = bytes;
		this.tempMimeType = mimeType;
	}

	private void onPostActionPerformed(ActionEvent e) {

		try {

			this.addPhoto(this.tempBytes, this.titleTextField.getText(), this.tempMimeType);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while storing your Personal Data: " + ex.getMessage(), ex);
			return;
		}

		// reset

		this.titleTextField.setText("");

		Panel imageFileUploadPanel = (Panel) MainWindow.findChildComponentById(this, "imageFileUploadPanel");
		imageFileUploadPanel.removeAll();
		UploadSelect imageFileUploadSelect = new UploadSelect();
		imageFileUploadSelect.setId("imageFileUploadSelect");
		imageFileUploadSelect.addUploadListener(new UploadListener() {
			private static final long serialVersionUID = 1L;

			public void uploadComplete(UploadEvent e2) {
				onImageFileUploadComplete(e2);
			}
		});
		imageFileUploadPanel.add(imageFileUploadSelect);
	}

	private List<XRI3Segment> getFriendXris() throws XdiException {

		Message message = this.endpoint.prepareOperation(XDIMessagingConstants.XRI_S_GET, this.friendAddress);
		MessageResult messageResult = this.endpoint.send(message);

		ContextNode contextNode = messageResult.getGraph().findContextNode(this.contextNodeXri, false);
		if (contextNode == null) return new ArrayList<XRI3Segment> ();

		Iterator<Relation> relations = contextNode.getRelations(new XRI3Segment("+friend"));
		Iterator<XRI3Segment> relationArcXris = new MappingRelationArcXriIterator(relations);

		return new IteratorListMaker<XRI3Segment> (relationArcXris).list();
	}

	private void addFriendXri(XRI3Segment friendXri) throws XdiException {

		// $add

		Statement targetStatement = StatementUtil.fromComponents(this.contextNodeXri, new XRI3Segment("+friend"), friendXri);

		Message message = this.endpoint.prepareOperation(XDIMessagingConstants.XRI_S_ADD, targetStatement);
		this.endpoint.send(message);
	}

	private void addPhoto(byte[] bytes, String title, String mimeType) throws XdiException {

		String imageGuid = UUID.randomUUID().toString();

		// $add

		Statement[] targetStatements = new Statement[] {
				StatementUtil.fromComponents(new XRI3Segment("+photo$" + imageGuid), new XRI3Segment("$a$mime"), XDIUtil.stringToDataXriSegment(mimeType)),
				StatementUtil.fromComponents(new XRI3Segment("+photo$" + imageGuid), new XRI3Segment("$a$bin"), XDIUtil.stringToDataXriSegment(new String(Base64.encodeBase64(bytes)))),
				StatementUtil.fromComponents(new XRI3Segment("+photo$" + imageGuid), new XRI3Segment("$a$xsd$string"), XDIUtil.stringToDataXriSegment(title))
		};

		Message message = this.endpoint.prepareOperations(XDIMessagingConstants.XRI_S_ADD, Arrays.asList(targetStatements).iterator());

		this.endpoint.send(message);
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
				"/pds/web/ui/app/photos/app.png");
		imageIcon2.setIcon(imageReference1);
		imageIcon2.setHeight(new Extent(48, Extent.PX));
		imageIcon2.setWidth(new Extent(48, Extent.PX));
		row2.add(imageIcon2);
		Label label1 = new Label();
		label1.setStyleName("Header");
		label1.setText("Photos");
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
		label3.setText("Upload an image:");
		column6.add(label3);
		Row row5 = new Row();
		row5.setCellSpacing(new Extent(10, Extent.PX));
		column6.add(row5);
		Label label4 = new Label();
		label4.setStyleName("Default");
		label4.setText("Title:");
		row5.add(label4);
		titleTextField = new TextField();
		titleTextField.setStyleName("Default");
		row5.add(titleTextField);
		Row row6 = new Row();
		row6.setCellSpacing(new Extent(10, Extent.PX));
		column6.add(row6);
		Panel panel9 = new Panel();
		panel9.setId("imageFileUploadPanel");
		ColumnLayoutData panel9LayoutData = new ColumnLayoutData();
		panel9LayoutData.setInsets(new Insets(new Extent(0, Extent.PX),
				new Extent(0, Extent.PX), new Extent(0, Extent.PX), new Extent(
						5, Extent.PX)));
		panel9.setLayoutData(panel9LayoutData);
		row6.add(panel9);
		UploadSelect uploadSelect1 = new UploadSelect();
		uploadSelect1.setId("imageFileUploadSelect");
		uploadSelect1.addUploadListener(new UploadListener() {
			private static final long serialVersionUID = 1L;

			public void uploadComplete(UploadEvent e) {
				onImageFileUploadComplete(e);
			}
		});
		panel9.add(uploadSelect1);
		Button button1 = new Button();
		button1.setStyleName("Default");
		button1.setText("Post!");
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onPostActionPerformed(e);
			}
		});
		row6.add(button1);
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
		photosColumn = new pds.web.ui.app.photos.components.PhotosColumn();
		photosColumn.setInsets(new Insets(new Extent(10, Extent.PX),
				new Extent(0, Extent.PX), new Extent(0, Extent.PX), new Extent(
						0, Extent.PX)));
		splitPane2.add(photosColumn);
	}
}
