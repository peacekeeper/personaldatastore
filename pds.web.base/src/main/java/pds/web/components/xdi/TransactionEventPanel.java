package pds.web.components.xdi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.ResourceBundle;

import nextapp.echo.app.Button;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Font;
import nextapp.echo.app.ImageReference;
import nextapp.echo.app.Label;
import nextapp.echo.app.Panel;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import org.eclipse.higgins.xdi4j.messaging.Operation;

import pds.web.ui.MainWindow;
import pds.web.xdi.events.XdiTransactionEvent;
import pds.web.xdi.events.XdiTransactionFailureEvent;
import pds.web.xdi.events.XdiTransactionSuccessEvent;

public class TransactionEventPanel extends Panel {

	private static final long serialVersionUID = -5082464847478633075L;

	private static final ImageReference IMAGEREFERENCE_SUCCESS = new ResourceImageReference("/pds/web/resource/image/transactionsuccess.png");
	private static final ImageReference IMAGEREFERENCE_FAILURE = new ResourceImageReference("/pds/web/resource/image/transactionfailure.png");

	private static final DateFormat DATEFORMAT = new SimpleDateFormat("HH:mm:ss.SSS");

	protected ResourceBundle resourceBundle;

	private XdiTransactionEvent transactionEvent;

	private Label timestampLabel;
	private Button button;
	private Label summaryLabel;

	/**
	 * Creates a new <code>ClaimPanel</code>.
	 */
	public TransactionEventPanel() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void dispose() {

		super.dispose();
	}

	private void refresh() {

		StringBuffer buffer = new StringBuffer();
		Iterator<Operation> operations = this.transactionEvent.getMessageEnvelope().getOperations();

		while (operations.hasNext()) {

			Operation operation = operations.next();
			if (buffer.length() > 0) buffer.append(", ");
			buffer.append(operation.getOperationXri().toString());
		}

		if (this.transactionEvent instanceof XdiTransactionSuccessEvent) {

			this.button.setIcon(IMAGEREFERENCE_SUCCESS);

			if (buffer.length() > 0) buffer.append(" / ");
			buffer.append(Integer.toString(((XdiTransactionSuccessEvent) this.transactionEvent).getMessageResult().getGraph().getStatementCount()) + " result statements.");
		} else if (this.transactionEvent instanceof XdiTransactionFailureEvent) {

			this.button.setIcon(IMAGEREFERENCE_FAILURE);

			if (buffer.length() > 0) buffer.append(" / ");
			buffer.append(((XdiTransactionFailureEvent) this.transactionEvent).getException().getMessage());
		}

		this.timestampLabel.setText(DATEFORMAT.format(this.transactionEvent.getBeginTimestamp()));
		this.summaryLabel.setText(buffer.toString());
	}

	public void setTransactionEvent(XdiTransactionEvent transactionEvent) {

		this.transactionEvent = transactionEvent;

		this.refresh();
	}

	public XdiTransactionEvent getTransactionEvent() {

		return this.transactionEvent;
	}

	private void onButtonActionPerformed(ActionEvent e) {

		TransactionEventWindowPane transactionEventWindowPane = new TransactionEventWindowPane();
		transactionEventWindowPane.setTransactionEvent(this.transactionEvent);

		MainWindow.findMainContentPane(this).add(transactionEventWindowPane);
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
		timestampLabel = new Label();
		timestampLabel.setStyleName("Default");
		timestampLabel.setText("...");
		timestampLabel.setFont(new Font(new Font.Typeface("Courier New",
				new Font.Typeface(" monospace")), Font.PLAIN, new Extent(10,
				Extent.PT)));
		row1.add(timestampLabel);
		button = new Button();
		button.setStyleName("Plain");
		button.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onButtonActionPerformed(e);
			}
		});
		row1.add(button);
		summaryLabel = new Label();
		summaryLabel.setStyleName("Default");
		summaryLabel.setText("...");
		row1.add(summaryLabel);
	}
}
