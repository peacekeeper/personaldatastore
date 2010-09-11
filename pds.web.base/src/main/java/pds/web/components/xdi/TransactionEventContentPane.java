package pds.web.components.xdi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.EventObject;
import java.util.ResourceBundle;

import nextapp.echo.app.Border;
import nextapp.echo.app.Color;
import nextapp.echo.app.Column;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.layout.RowLayoutData;
import nextapp.echo.app.layout.SplitPaneLayoutData;
import nextapp.echo.extras.app.TabPane;
import nextapp.echo.extras.app.layout.TabPaneLayoutData;
import pds.xdi.events.XdiTransactionEvent;
import pds.xdi.events.XdiTransactionFailureEvent;
import pds.xdi.events.XdiTransactionSuccessEvent;

public class TransactionEventContentPane extends ContentPane  {

	private static final long serialVersionUID = 5781883512857770059L;

	private static final DateFormat DATEFORMAT = new SimpleDateFormat("HH:mm:ss.SSS");

	protected ResourceBundle resourceBundle;

	private XdiTransactionEvent transactionEvent;

	private ContentPane messageEnvelopeTab;
	private GraphContentPane messageEnvelopeGraphContentPane;
	private ContentPane messageResultTab;
	private GraphContentPane messageResultGraphContentPane;
	private ContentPane exceptionTab;
	private Label exceptionLabel;
	private Label beginTimestampLabel;
	private Label endTimestampLabel;
	private Label durationLabel;

	/**
	 * Creates a new <code>ConfigureAPIsContentPane</code>.
	 */
	public TransactionEventContentPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void init() {

		super.init();
	}

	private void refresh(EventObject event) {

		System.err.println("ME: " + this.transactionEvent.getMessageEnvelope());
		this.messageEnvelopeGraphContentPane.setGraph(this.transactionEvent.getMessageEnvelope().getGraph());
		this.beginTimestampLabel.setText(DATEFORMAT.format(this.transactionEvent.getBeginTimestamp()));
		this.endTimestampLabel.setText(DATEFORMAT.format(this.transactionEvent.getEndTimestamp()));
		this.durationLabel.setText(Long.toString(this.transactionEvent.getEndTimestamp().getTime() - this.transactionEvent.getBeginTimestamp().getTime()) + " ms");

		if (this.transactionEvent instanceof XdiTransactionSuccessEvent) {

			this.messageResultTab.setVisible(true);
			this.exceptionTab.setVisible(false);
			this.messageResultGraphContentPane.setGraph(((XdiTransactionSuccessEvent) this.transactionEvent).getMessageResult().getGraph()); 
		} else if (this.transactionEvent instanceof XdiTransactionFailureEvent) {

			this.messageResultTab.setVisible(false);
			this.exceptionTab.setVisible(true);
			this.exceptionLabel.setText(((XdiTransactionFailureEvent) this.transactionEvent).getException().getMessage());
		}
	}

	public void setTransactionEvent(XdiTransactionEvent transactionEvent) {

		this.transactionEvent = transactionEvent;

		this.refresh(null);
	}

	public XdiTransactionEvent getTransactionEvent() {

		return this.transactionEvent;
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
		splitPane1.setSeparatorHeight(new Extent(10, Extent.PX));
		splitPane1.setSeparatorVisible(false);
		add(splitPane1);
		Column column1 = new Column();
		column1.setCellSpacing(new Extent(10, Extent.PX));
		SplitPaneLayoutData column1LayoutData = new SplitPaneLayoutData();
		column1LayoutData.setMinimumSize(new Extent(75, Extent.PX));
		column1LayoutData.setMaximumSize(new Extent(75, Extent.PX));
		column1LayoutData.setOverflow(SplitPaneLayoutData.OVERFLOW_HIDDEN);
		column1.setLayoutData(column1LayoutData);
		splitPane1.add(column1);
		Row row1 = new Row();
		row1.setCellSpacing(new Extent(10, Extent.PX));
		row1.setBorder(new Border(new Extent(3, Extent.PX), Color.BLACK,
				Border.STYLE_SOLID));
		column1.add(row1);
		Label label1 = new Label();
		label1.setStyleName("Default");
		label1
				.setText("This window displays a raw XDI transaction with your Personal Data Store.");
		RowLayoutData label1LayoutData = new RowLayoutData();
		label1LayoutData.setInsets(new Insets(new Extent(10, Extent.PX)));
		label1.setLayoutData(label1LayoutData);
		row1.add(label1);
		Row row2 = new Row();
		row2.setCellSpacing(new Extent(10, Extent.PX));
		column1.add(row2);
		Label label2 = new Label();
		label2.setStyleName("Default");
		label2.setText("Start:");
		row2.add(label2);
		beginTimestampLabel = new Label();
		beginTimestampLabel.setStyleName("Bold");
		beginTimestampLabel.setText("...");
		row2.add(beginTimestampLabel);
		Label label3 = new Label();
		label3.setStyleName("Default");
		label3.setText("End:");
		row2.add(label3);
		endTimestampLabel = new Label();
		endTimestampLabel.setStyleName("Bold");
		endTimestampLabel.setText("...");
		row2.add(endTimestampLabel);
		Label label4 = new Label();
		label4.setStyleName("Default");
		label4.setText("Duration:");
		row2.add(label4);
		durationLabel = new Label();
		durationLabel.setStyleName("Bold");
		durationLabel.setText("...");
		row2.add(durationLabel);
		TabPane tabPane1 = new TabPane();
		tabPane1.setStyleName("Default");
		splitPane1.add(tabPane1);
		messageEnvelopeTab = new ContentPane();
		TabPaneLayoutData messageEnvelopeTabLayoutData = new TabPaneLayoutData();
		ResourceImageReference imageReference1 = new ResourceImageReference(
				"/pds/web/resource/image/xdi-request.png");
		messageEnvelopeTabLayoutData.setIcon(imageReference1);
		messageEnvelopeTabLayoutData.setTitle("XDI Request");
		messageEnvelopeTab.setLayoutData(messageEnvelopeTabLayoutData);
		tabPane1.add(messageEnvelopeTab);
		messageEnvelopeGraphContentPane = new GraphContentPane();
		messageEnvelopeTab.add(messageEnvelopeGraphContentPane);
		messageResultTab = new ContentPane();
		TabPaneLayoutData messageResultTabLayoutData = new TabPaneLayoutData();
		ResourceImageReference imageReference2 = new ResourceImageReference(
				"/pds/web/resource/image/xdi-response.png");
		messageResultTabLayoutData.setIcon(imageReference2);
		messageResultTabLayoutData.setTitle("XDI Response");
		messageResultTab.setLayoutData(messageResultTabLayoutData);
		tabPane1.add(messageResultTab);
		messageResultGraphContentPane = new GraphContentPane();
		messageResultTab.add(messageResultGraphContentPane);
		exceptionTab = new ContentPane();
		exceptionTab.setInsets(new Insets(new Extent(0, Extent.PX), new Extent(
				5, Extent.PX), new Extent(0, Extent.PX), new Extent(0,
				Extent.PX)));
		TabPaneLayoutData exceptionTabLayoutData = new TabPaneLayoutData();
		ResourceImageReference imageReference3 = new ResourceImageReference(
				"/pds/web/resource/image/xdi-exception.png");
		exceptionTabLayoutData.setIcon(imageReference3);
		exceptionTabLayoutData.setTitle("XDI Error");
		exceptionTab.setLayoutData(exceptionTabLayoutData);
		tabPane1.add(exceptionTab);
		Column column2 = new Column();
		exceptionTab.add(column2);
		exceptionLabel = new Label();
		exceptionLabel.setStyleName("Bold");
		exceptionLabel.setText("...");
		column2.add(exceptionLabel);
	}
}
