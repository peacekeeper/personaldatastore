
package pds.web.ui.dataimport;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ResourceBundle;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.Button;
import nextapp.echo.app.Column;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.Panel;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.RowLayoutData;
import nextapp.echo.app.layout.SplitPaneLayoutData;
import nextapp.echo.filetransfer.app.UploadSelect;
import nextapp.echo.filetransfer.app.event.UploadEvent;
import nextapp.echo.filetransfer.app.event.UploadListener;
import nextapp.echo.filetransfer.model.Upload;
import pds.web.PDSApplication;
import pds.web.components.xdi.XdiPanel;
import pds.web.logger.Logger;
import pds.web.ui.MessageDialog;
import pds.xdi.XdiEndpoint;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.remoteroots.RemoteRoots;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.CopyUtil.CopyStrategy;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.constants.XDIMessagingConstants;
import echopoint.ImageIcon;

public class DataImportContentPane extends ContentPane {

	private static final long serialVersionUID = 5781883512857770059L;

	protected ResourceBundle resourceBundle;

	private XdiEndpoint endpoint;

	private Label canonicalLabel;
	private Panel uploadSelectPanel;
	private UploadSelect uploadSelect;
	private Row statsRow;
	private Label bytesLabel;
	private Label statementsLabel;
	private Button importButton;

	private Graph graph;

	private XdiPanel xdiPanel;

	/**
	 * Creates a new <code>ConfigureAPIsContentPane</code>.
	 */
	public DataImportContentPane() {
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
	}

	private void refresh() {

		try {

			this.canonicalLabel.setText(this.endpoint.getCanonical().toString());
			this.xdiPanel.setEndpointAndMainAddressAndGetAddresses(this.endpoint, null, null);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	public void setEndpoint(XdiEndpoint endpoint) {

		// refresh

		this.endpoint = endpoint;

		this.refresh();
	}

	private void onUploadComplete(UploadEvent e) {

		// read the uploaded file

		Upload upload = e.getUpload();
		byte[] bytes;

		try {

			DataInputStream stream = new DataInputStream(upload.getInputStream());
			bytes = new byte[stream.available()];
			stream.readFully(bytes);
			stream.close();
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, we could not receive the upload: " + ex.getMessage(), ex);
			return;
		}

		// reset the UploadSelect

		this.uploadSelectPanel.removeAll();
		this.uploadSelect = new UploadSelect();
		this.uploadSelect.addUploadListener(new UploadListener() {
			private static final long serialVersionUID = 1L;

			public void uploadComplete(UploadEvent e2) {
				onUploadComplete(e2);
			}
		});
		this.uploadSelectPanel.add(this.uploadSelect);

		// try to parse the XDI data

		try {

			this.graph = MemoryGraphFactory.getInstance().openGraph();
			XDIReaderRegistry.getAuto().read(this.graph, new ByteArrayInputStream(bytes));
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, there appears to be a problem with the XDI data: " + ex.getMessage(), ex);
			return;
		}

		// show the stats row

		this.bytesLabel.setText(Integer.toString(bytes.length));
		this.statementsLabel.setText(Integer.toString(this.graph.getRootContextNode().getAllStatementCount()));
		this.statsRow.setVisible(true);

		// enable the import button

		this.importButton.setEnabled(true);
	}

	private void onResetActionPerformed(ActionEvent e) {

		// reset the UploadSelect

		this.uploadSelectPanel.removeAll();
		this.uploadSelect = new UploadSelect();
		this.uploadSelect.addUploadListener(new UploadListener() {
			private static final long serialVersionUID = 1L;

			public void uploadComplete(UploadEvent e2) {
				onUploadComplete(e2);
			}
		});
		this.uploadSelectPanel.add(this.uploadSelect);

		// hide the stats row

		this.statsRow.setVisible(false);

		// disable the import button

		this.importButton.setEnabled(false);
	}

	private void onImportActionPerformed(ActionEvent e) {

		Logger logger = PDSApplication.getApp().getLogger();

		if (this.graph == null) {

			MessageDialog.problem("Please upload an XDI file first for importing Personal Data!", null);
			return;
		}

		// do a $del on everything

		try {

			Message message = this.endpoint.prepareOperation(XDIMessagingConstants.XRI_S_DEL, XDIConstants.XRI_S_ROOT);
			this.endpoint.send(message);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while deleting your Personal Data: " + ex.getMessage(), ex);
			return;
		}

		// need to make the following changes to the graph:
		// - replace the subject
		// - filter out some read-only data

		Graph importGraph = MemoryGraphFactory.getInstance().openGraph();

		final XRI3Segment oldInumber = RemoteRoots.xriOfRemoteRootXri(RemoteRoots.getSelfRemoteRootContextNode(this.graph).getXri());
		final XRI3Segment newInumber = this.endpoint.getCanonical();

		logger.info("Importing from " + oldInumber + " to " + newInumber, null);

		CopyUtil.copyGraph(this.graph, importGraph, new CopyStrategy() {

			@Override
			public ContextNode replaceContextNode(ContextNode contextNode) {

				//if (subject.getSubjectXri().equals(oldInumber)) return newInumber;	// TODO

				return super.replaceContextNode(contextNode);
			}
		});

		// do a $add with the graph

		try {

			Message message = this.endpoint.prepareOperations(XDIMessagingConstants.XRI_S_ADD, importGraph.getRootContextNode().getAllStatements());
			this.endpoint.send(message);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while storing your Personal Data: " + ex.getMessage(), ex);
			return;
		}

		// done

		MessageDialog.info("Your Personal Data has been successfully imported!");
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
		Row row2 = new Row();
		SplitPaneLayoutData row2LayoutData = new SplitPaneLayoutData();
		row2LayoutData.setMinimumSize(new Extent(70, Extent.PX));
		row2LayoutData.setMaximumSize(new Extent(70, Extent.PX));
		row2.setLayoutData(row2LayoutData);
		splitPane1.add(row2);
		Row row3 = new Row();
		row3.setCellSpacing(new Extent(10, Extent.PX));
		RowLayoutData row3LayoutData = new RowLayoutData();
		row3LayoutData.setWidth(new Extent(50, Extent.PERCENT));
		row3.setLayoutData(row3LayoutData);
		row2.add(row3);
		ImageIcon imageIcon2 = new ImageIcon();
		ResourceImageReference imageReference1 = new ResourceImageReference(
		"/pds/web/resource/image/data-import.png");
		imageIcon2.setIcon(imageReference1);
		imageIcon2.setHeight(new Extent(48, Extent.PX));
		imageIcon2.setWidth(new Extent(48, Extent.PX));
		row3.add(imageIcon2);
		Label label2 = new Label();
		label2.setStyleName("Header");
		label2.setText("Personal Data Import Tool");
		row3.add(label2);
		Row row1 = new Row();
		row1.setAlignment(new Alignment(Alignment.RIGHT, Alignment.DEFAULT));
		row1.setCellSpacing(new Extent(10, Extent.PX));
		RowLayoutData row1LayoutData = new RowLayoutData();
		row1LayoutData.setWidth(new Extent(50, Extent.PERCENT));
		row1.setLayoutData(row1LayoutData);
		row2.add(row1);
		Label label1 = new Label();
		label1.setStyleName("Header");
		label1.setText("Identifier:");
		row1.add(label1);
		canonicalLabel = new Label();
		canonicalLabel.setStyleName("Bold");
		canonicalLabel.setText("...");
		row1.add(canonicalLabel);
		Column column1 = new Column();
		column1.setCellSpacing(new Extent(10, Extent.PX));
		splitPane1.add(column1);
		Label label6 = new Label();
		label6.setStyleName("Default");
		label6.setText("This tool can import data from an interoperable XDI file into your Personal Data Store.");
		column1.add(label6);
		Label label7 = new Label();
		label7.setStyleName("Default");
		label7.setText("Note: Right now, this replaces all existing data. In the future, it will also be possible to simply add data without overwriting or deleting anything.");
		column1.add(label7);
		Row row5 = new Row();
		row5.setCellSpacing(new Extent(10, Extent.PX));
		column1.add(row5);
		Label label3 = new Label();
		label3.setStyleName("Default");
		label3.setText("Upload XDI file:");
		row5.add(label3);
		uploadSelectPanel = new Panel();
		row5.add(uploadSelectPanel);
		uploadSelect = new UploadSelect();
		uploadSelect.setId("imageFileUploadSelect");
		uploadSelect.addUploadListener(new UploadListener() {
			private static final long serialVersionUID = 1L;

			public void uploadComplete(UploadEvent e) {
				onUploadComplete(e);
			}
		});
		uploadSelectPanel.add(uploadSelect);
		statsRow = new Row();
		statsRow.setVisible(false);
		statsRow.setCellSpacing(new Extent(10, Extent.PX));
		column1.add(statsRow);
		Label label5 = new Label();
		label5.setStyleName("Default");
		label5.setText("Data received!");
		statsRow.add(label5);
		Label label4 = new Label();
		label4.setStyleName("Bold");
		label4.setText("Bytes:");
		statsRow.add(label4);
		bytesLabel = new Label();
		bytesLabel.setStyleName("Default");
		bytesLabel.setText("...");
		statsRow.add(bytesLabel);
		Label label8 = new Label();
		label8.setStyleName("Bold");
		label8.setText("XDI statements:");
		statsRow.add(label8);
		statementsLabel = new Label();
		statementsLabel.setStyleName("Default");
		statementsLabel.setText("...");
		statsRow.add(statementsLabel);
		xdiPanel = new XdiPanel();
		statsRow.add(xdiPanel);
		Button button2 = new Button();
		button2.setStyleName("Default");
		button2.setText("Reset");
		button2.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onResetActionPerformed(e);
			}
		});
		statsRow.add(button2);
		Row row4 = new Row();
		column1.add(row4);
		importButton = new Button();
		importButton.setStyleName("Default");
		importButton.setEnabled(false);
		importButton.setText("Import my Personal Data");
		importButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onImportActionPerformed(e);
			}
		});
		row4.add(importButton);
	}
}
