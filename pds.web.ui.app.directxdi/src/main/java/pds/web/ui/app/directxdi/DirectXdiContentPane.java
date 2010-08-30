package pds.web.ui.app.directxdi;

import java.io.StringReader;
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

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.Literal;
import org.eclipse.higgins.xdi4j.Statement;
import org.eclipse.higgins.xdi4j.impl.memory.MemoryGraphFactory;
import org.eclipse.higgins.xdi4j.io.XDIReaderRegistry;
import org.eclipse.higgins.xdi4j.messaging.MessageEnvelope;
import org.eclipse.higgins.xdi4j.util.CopyUtil;
import org.eclipse.higgins.xdi4j.util.CopyUtil.CopyStatementStrategy;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.PDSApplication;
import pds.web.components.xdi.TransactionEventWindowPane;
import pds.web.ui.MainWindow;
import pds.web.ui.MessageDialog;
import pds.web.xdi.XdiContext;
import pds.web.xdi.events.XdiTransactionEvent;

public class DirectXdiContentPane extends ContentPane {

	private static final long serialVersionUID = 5190449130454460991L;

	protected ResourceBundle resourceBundle;

	private XdiContext context;

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

		StringBuffer buffer = new StringBuffer();
		buffer.append("" + this.context.getCanonical() + "\n");

		if (context.getPassword() != null) {

			buffer.append("\t$password\n");
			buffer.append("\t\t\"********\"\n");
		}

		buffer.append("\t$get\n");
		buffer.append("\t\t/\n");
		buffer.append("\t\t\t" + this.context.getCanonical() + "\n");
		this.xdiTextArea.setText(buffer.toString());
	}

	private void refresh() {

	}

	public void setContext(XdiContext context) {

		this.context = context;

		this.refresh();
	}

	private void onSendXdiActionPerformed(ActionEvent e) {

		XdiContext context = PDSApplication.getApp().getOpenContext();

		MessageEnvelope messageEnvelope = MessageEnvelope.newInstance();
		XdiTransactionEvent transactionEvent = null;

		try {

			Graph tempGraph = MemoryGraphFactory.getInstance().openGraph();
			XDIReaderRegistry.getAuto().read(tempGraph, new StringReader(this.xdiTextArea.getText()), null);
			CopyUtil.copyStatements(tempGraph, messageEnvelope.getGraph(), PASSWORDINSERTINGCOPYSTATEMENTSTRATEGY);
			transactionEvent = context.directXdi(messageEnvelope);
		} catch (Exception ex) {

			MessageDialog.problem("Problem while sending direct XDI: " + ex.getMessage(), ex);
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

	private static final XRI3Segment XRI_PASSWORD = new XRI3Segment("$password");

	private static CopyStatementStrategy PASSWORDINSERTINGCOPYSTATEMENTSTRATEGY = new CopyStatementStrategy() {

		@Override
		public boolean doCopy(Statement statement, Graph target) {

			return true;
		}

		@Override
		public String replaceLiteralData(Literal literal) {

			if (literal.getPredicate().getPredicateXri().equals(XRI_PASSWORD)) {

				XdiContext context = PDSApplication.getApp().getOpenContext();
				String password = context.getPassword();

				return password != null ? password : literal.getData();
			} else {

				return literal.getData();
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
		.setText("Here you can send arbitrary XDI transactions to your Personal Data Store.");
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
