package pds.web.ui.app.directxdi;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ResourceBundle;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.Border;
import nextapp.echo.app.Button;
import nextapp.echo.app.Color;
import nextapp.echo.app.Column;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.Row;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.TextArea;
import nextapp.echo.app.WindowPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.RowLayoutData;
import nextapp.echo.app.layout.SplitPaneLayoutData;
import pds.web.PDSApplication;
import pds.web.components.xdi.TransactionEventWindowPane;
import pds.web.ui.MainWindow;
import pds.web.ui.MessageDialog;
import pds.xdi.XdiEndpoint;
import pds.xdi.events.XdiTransactionEvent;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.BasicLiteral;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.CopyUtil.CopyStrategy;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.constants.XDIMessagingConstants;

public class DirectXdiContentPane extends ContentPane {

	private static final long serialVersionUID = 5190449130454460991L;

	protected ResourceBundle resourceBundle;

	private XdiEndpoint endpoint;

	private TextArea xdiTextArea;

	/**
	 * Creates a new <code>ConfigureAPIsContentPane</code>.
	 */
	public DirectXdiContentPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void init() {

		super.init();

		Message message = this.endpoint.prepareMessage();
		message.createGetOperation(XDIConstants.XRI_S_CONTEXT);
		StringWriter writer = new StringWriter();

		try {

			XDIWriterRegistry.forFormat("XDI DISPLAY" , null).write(message.getMessageEnvelope().getGraph(), writer);
		} catch (IOException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		this.xdiTextArea.setText(writer.toString());
	}

	private void refresh() {

	}

	public void setEndpoint(XdiEndpoint endpoint) {

		this.endpoint = endpoint;

		this.refresh();
	}

	private void onSendXdiActionPerformed(ActionEvent e) {

		XdiEndpoint endpoint = PDSApplication.getApp().getOpenEndpoint();

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		XdiTransactionEvent transactionEvent = null;

		try {

			Graph tempGraph = MemoryGraphFactory.getInstance().openGraph();
			XDIReaderRegistry.getAuto().read(tempGraph, new StringReader(this.xdiTextArea.getText()));
			CopyUtil.copyGraph(tempGraph, messageEnvelope.getGraph(), secretTokenInsertingCopyStrategy);
			transactionEvent = endpoint.directXdi(messageEnvelope);
		} catch (Exception ex) {

			MessageDialog.problem("Problem while sending direct XDI: " + ex.getMessage(), ex);
			return;
		}

		TransactionEventWindowPane transactionEventWindowPane = new TransactionEventWindowPane();
		transactionEventWindowPane.setTransactionEvent(transactionEvent);

		MainWindow.findMainContentPane(this).add(transactionEventWindowPane);
	}

	private void onCloseActionPerformed(ActionEvent e) {

		// close the window

		WindowPane windowPane = (WindowPane) this.getParent();
		windowPane.getParent().remove(windowPane);
	}

	private static CopyStrategy secretTokenInsertingCopyStrategy = new CopyStrategy() {

		@Override
		public Literal replaceLiteral(Literal literal) {

			if (literal.getContextNode().getXri().toString().contains(XDIMessagingConstants.XRI_S_SECRET_TOKEN.toString())) {

				XdiEndpoint endpoint = PDSApplication.getApp().getOpenEndpoint();
				String secretToken = endpoint.getSecretToken();

				return secretToken == null ? null : new BasicLiteral(secretToken);
			} else {

				return literal;
			}
		};
	};

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
		splitPane1.setSeparatorColor(Color.WHITE);
		splitPane1.setSeparatorHeight(new Extent(10, Extent.PX));
		splitPane1.setSeparatorVisible(true);
		add(splitPane1);
		Column column1 = new Column();
		column1.setCellSpacing(new Extent(10, Extent.PX));
		splitPane1.add(column1);
		Row row2 = new Row();
		row2.setCellSpacing(new Extent(10, Extent.PX));
		row2.setBorder(new Border(new Extent(3, Extent.PX), Color.BLACK,
				Border.STYLE_SOLID));
		column1.add(row2);
		Label label2 = new Label();
		label2.setStyleName("Default");
		label2
		.setText("Here you can send arbitrary XDI transactions to your Personal Cloud.");
		RowLayoutData label2LayoutData = new RowLayoutData();
		label2LayoutData.setInsets(new Insets(new Extent(10, Extent.PX)));
		label2.setLayoutData(label2LayoutData);
		row2.add(label2);
		Column column2 = new Column();
		column1.add(column2);
		xdiTextArea = new TextArea();
		xdiTextArea.setStyleName("Default");
		xdiTextArea.setHeight(new Extent(300, Extent.PX));
		xdiTextArea.setWidth(new Extent(95, Extent.PERCENT));
		column2.add(xdiTextArea);
		Row row4 = new Row();
		row4.setAlignment(new Alignment(Alignment.RIGHT, Alignment.DEFAULT));
		row4.setCellSpacing(new Extent(10, Extent.PX));
		SplitPaneLayoutData row4LayoutData = new SplitPaneLayoutData();
		row4LayoutData.setMinimumSize(new Extent(30, Extent.PX));
		row4LayoutData.setMaximumSize(new Extent(30, Extent.PX));
		row4LayoutData.setOverflow(SplitPaneLayoutData.OVERFLOW_HIDDEN);
		row4.setLayoutData(row4LayoutData);
		splitPane1.add(row4);
		Button button1 = new Button();
		button1.setStyleName("Default");
		button1.setText("Send!");
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onSendXdiActionPerformed(e);
			}
		});
		row4.add(button1);
		Button button2 = new Button();
		button2.setStyleName("Default");
		button2.setText("Close");
		button2.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onCloseActionPerformed(e);
			}
		});
		row4.add(button2);
	}
}
