package pds.web.ui.app.photos;

import java.io.DataInputStream;
import java.util.ArrayList;
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
import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Reference;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.util.iterators.IteratorListMaker;
import org.eclipse.higgins.xdi4j.util.iterators.MappingReferenceXrisIterator;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.PDSApplication;
import pds.web.components.xdi.XdiPanel;
import pds.web.ui.MainWindow;
import pds.web.ui.MessageDialog;
import pds.web.ui.shared.FriendPanel;
import pds.web.ui.shared.FriendPanel.FriendPanelDelegate;
import pds.web.ui.shared.PhotosColumn;
import pds.web.util.MimeTypeUtil;
import pds.web.xdi.Xdi;
import pds.web.xdi.XdiException;
import pds.web.xdi.events.XdiGraphAddEvent;
import pds.web.xdi.events.XdiGraphDelEvent;
import pds.web.xdi.events.XdiGraphEvent;
import pds.web.xdi.events.XdiGraphListener;
import pds.web.xdi.events.XdiGraphModEvent;
import pds.web.xdi.objects.XdiContext;
import echopoint.ImageIcon;

public class PhotosContentPane extends ContentPane implements XdiGraphListener {

	private static final long serialVersionUID = -8925773333194027452L;

	protected ResourceBundle resourceBundle;

	private XdiContext context;
	private XRI3Segment subjectXri;
	private XRI3 address;
	private XRI3 friendAddress;
	private XRI3 photosAddress;

	private XdiPanel xdiPanel;
	private TextField addTextField;
	private Button addButton;
	private Button cancelButton;
	private Column friendsColumn;
	private PhotosColumn photosColumn;
	private TextField titleTextField;

	private byte[] tempBytes;
	private String tempMimeType;

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

		// remove us as listener

		PDSApplication.getApp().getOpenContext().removeXdiGraphListener(this);
	}

	private void refresh() {

		try {

			this.xdiPanel.setContextAndMainAddressAndGetAddresses(this.context, this.address, this.xdiGetAddresses());

			// get list of friend XRIs

			List<XRI3Segment> friendXris;

			friendXris = this.getFriendXris();

			// add them

			this.friendsColumn.removeAll();
			for (XRI3Segment friendXri : friendXris) {

				this.addFriendPanel(friendXri);
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your personal data: " + ex.getMessage(), ex);
			return;
		}
	}

	private void addFriendPanel(final XRI3Segment friendXri) {

		FriendPanel friendPanel = new FriendPanel();
		friendPanel.setContextAndSubjectXriAndReferenceXri(this.context, this.subjectXri, friendXri);
		friendPanel.setFriendPanelDelegate(new FriendPanelDelegate() {

			@Override
			public void onFriendActionPerformed(ActionEvent e) {

				XdiContext context;

				try {

					Xdi xdi = PDSApplication.getApp().getXdi();
					context = xdi.resolveContext(friendXri.toString(), null);
				} catch (Exception ex) {

					MessageDialog.problem("Sorry, we could not open the Personal Data Store: " + ex.getMessage(), ex);
					return;
				}

				PhotosContentPane.this.photosColumn.setContextAndSubjectXri(context, new XRI3Segment(context.getInumber()));
			}
		});

		this.friendsColumn.add(friendPanel);
	}

	public XRI3[] xdiGetAddresses() {

		return new XRI3[] {
				this.friendAddress,
				this.photosAddress
		};
	}

	public XRI3[] xdiAddAddresses() {

		return new XRI3[] {
				new XRI3("" + this.friendAddress + "/$$"),
				new XRI3("" + this.photosAddress + "/$$")
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
		this.friendAddress = new XRI3("" + this.subjectXri + "/+friend");
		this.photosAddress = new XRI3("" + this.subjectXri + "/+photos");

		this.refresh();

		// add us as listener

		PDSApplication.getApp().getOpenContext().addXdiGraphListener(this);
	}

	private void onAddActionPerformed(ActionEvent e) {

		if (this.addTextField.isVisible()) {

			String friend = this.addTextField.getText();
			if (friend == null || friend.trim().equals("")) return;

			try {

				this.addFriendXri(new XRI3Segment(friend));
			} catch (Exception ex) {

				MessageDialog.problem("Sorry, a problem occurred while storing your personal data: " + ex.getMessage(), ex);
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

			MessageDialog.problem("Sorry, a problem occurred while storing your personal data: " + ex.getMessage(), ex);
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

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, this.friendAddress);
		MessageResult messageResult = this.context.send(operation);

		Subject subject = messageResult.getGraph().getSubject(this.subjectXri);
		if (subject == null) return new ArrayList<XRI3Segment> ();

		Predicate predicate = subject.getPredicate(new XRI3Segment("+friend"));
		if (predicate == null) return new ArrayList<XRI3Segment> ();
		if (! predicate.containsReferences()) return new ArrayList<XRI3Segment> ();

		Iterator<Reference> references = predicate.getReferences();
		Iterator<XRI3Segment> referenceXris = new MappingReferenceXrisIterator(references);

		return new IteratorListMaker<XRI3Segment> (referenceXris).list();
	}

	private void addFriendXri(XRI3Segment friendXri) throws XdiException {

		// $add

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_ADD);
		Graph operationGraph = operation.createOperationGraph(null);
		operationGraph.createStatement(this.subjectXri, new XRI3Segment("+friend"), friendXri);
		this.context.send(operation);
	}

	private void addPhoto(byte[] bytes, String title, String mimeType) throws XdiException {

		String imageGuid = UUID.randomUUID().toString();

		// $add

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_ADD);
		Graph operationGraph = operation.createOperationGraph(null);
		operationGraph.createStatement(this.subjectXri, new XRI3Segment("+photos"), (Graph) null);
		Graph photosGraph = operationGraph.getSubject(this.subjectXri).getPredicate(new XRI3Segment("+photos")).getInnerGraph();
		photosGraph.createStatement(new XRI3Segment("$" + imageGuid), new XRI3Segment("$a$mime"), mimeType);
		photosGraph.createStatement(new XRI3Segment("$" + imageGuid), new XRI3Segment("$a$bin"), new String(Base64.encodeBase64(bytes)));
		photosGraph.createStatement(new XRI3Segment("$" + imageGuid), new XRI3Segment("$a$xsd$string"), title);
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
		"/pds/web/resource/image/app-photos.png");
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
		photosColumn = new PhotosColumn();
		photosColumn.setInsets(new Insets(new Extent(10, Extent.PX),
				new Extent(0, Extent.PX), new Extent(0, Extent.PX), new Extent(
						0, Extent.PX)));
		splitPane2.add(photosColumn);
	}
}
