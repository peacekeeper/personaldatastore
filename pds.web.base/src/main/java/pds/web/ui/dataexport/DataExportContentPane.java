package pds.web.ui.dataexport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ResourceBundle;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.Button;
import nextapp.echo.app.Column;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.RowLayoutData;
import nextapp.echo.app.layout.SplitPaneLayoutData;
import nextapp.echo.filetransfer.app.DownloadCommand;
import nextapp.echo.filetransfer.app.DownloadProvider;

import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.io.XDIWriter;
import org.eclipse.higgins.xdi4j.io.XDIWriterRegistry;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;

import pds.web.PDSApplication;
import pds.web.ui.MessageDialog;
import pds.xdi.XdiContext;
import echopoint.ImageIcon;

public class DataExportContentPane extends ContentPane {

	private static final long serialVersionUID = 5781883512857770059L;

	protected ResourceBundle resourceBundle;

	private XdiContext context;

	private Label canonicalLabel;

	/**
	 * Creates a new <code>ConfigureAPIsContentPane</code>.
	 */
	public DataExportContentPane() {
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

			this.canonicalLabel.setText(this.context.getCanonical().toString());
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	public void setContext(XdiContext context) {

		// refresh

		this.context = context;

		this.refresh();
	}

	private void onExportActionPerformed(ActionEvent e) {
	
			// do a $get on everything
	
			MessageResult messageResult;
	
			try {
	
				Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET);
				messageResult = this.context.send(operation);
			} catch (Exception ex) {
	
				MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
				return;
			}
	
			// save the XDI data in a byte array
	
			final ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			final XDIWriter xdiWriter;
	
			try {
	
				xdiWriter = XDIWriterRegistry.forFormat("X3 Standard");
				xdiWriter.write(messageResult.getGraph(), byteArray, null);
			} catch (Exception ex) {
	
				MessageDialog.problem("Sorry, a problem occurred while exporting your Personal Data: " + ex.getMessage(), ex);
				return;
			}
	
			// download it
	
			DownloadProvider downloadProvider = new DownloadProvider() {
	
				@Override
				public String getContentType() {
	
					return xdiWriter.getMimeTypes()[0];
				}
	
				@Override
				public String getFileName() {
	
					return DataExportContentPane.this.context.getCanonical().toString() + "." + xdiWriter.getDefaultFileExtension();
				}
	
				@Override
				public long getSize() {
	
					return byteArray.size();
				}
	
				@Override
				public void writeFile(OutputStream outputStream) throws IOException {
	
					outputStream.write(byteArray.toByteArray());
				}
			};
	
			// download
	
			PDSApplication.getApp().enqueueCommand(new DownloadCommand(downloadProvider));
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
				"/pds/web/resource/image/data-export.png");
		imageIcon2.setIcon(imageReference1);
		imageIcon2.setHeight(new Extent(48, Extent.PX));
		imageIcon2.setWidth(new Extent(48, Extent.PX));
		row3.add(imageIcon2);
		Label label2 = new Label();
		label2.setStyleName("Header");
		label2.setText("Personal Data Export Tool");
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
		Label label4 = new Label();
		label4.setStyleName("Default");
		label4.setText("Here you can download all your Personal Data in the form of an interoperable XDI file.");
		column1.add(label4);
		Label label6 = new Label();
		label6.setStyleName("Default");
		label6.setText("You can do this for backup purposes, or to transfer your data from one PDS provider to another.");
		column1.add(label6);
		Label label7 = new Label();
		label7.setStyleName("Default");
		label7.setText("Note: Right now, this tool only works for your entire set of data. In the future, it will also be possible to export just parts of your data, e.g. just one persona.");
		column1.add(label7);
		Row row4 = new Row();
		column1.add(row4);
		Button button1 = new Button();
		button1.setStyleName("Default");
		button1.setText("Export my Personal Data");
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onExportActionPerformed(e);
			}
		});
		row4.add(button1);
	}
}
