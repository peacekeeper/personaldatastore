package pds.web.components.xdi;

import java.util.ResourceBundle;

import nextapp.echo.app.Extent;
import nextapp.echo.app.WindowPane;
import pds.web.components.xdi.TransactionEventContentPane;
import pds.web.xdi.events.XdiTransactionEvent;

public class TransactionEventWindowPane extends WindowPane {

	private static final long serialVersionUID = 4136493581013444404L;

	protected ResourceBundle resourceBundle;

	private TransactionEventContentPane transactionEventContentPane;

	/**
	 * Creates a new <code>ConfigureAPIsWindowPane</code>.
	 */
	public TransactionEventWindowPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	public void setTransactionEvent(XdiTransactionEvent transactionEvent) {

		this.transactionEventContentPane.setTransactionEvent(transactionEvent);
	}

	public XdiTransactionEvent getTransactionEvent() {

		return this.transactionEventContentPane.getTransactionEvent();
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setStyleName("Gray");
		this.setTitle("XDI Transaction");
		this.setHeight(new Extent(600, Extent.PX));
		this.setMinimizeEnabled(false);
		this.setMaximizeEnabled(true);
		this.setClosable(true);
		this.setWidth(new Extent(800, Extent.PX));
		transactionEventContentPane = new TransactionEventContentPane();
		add(transactionEventContentPane);
	}
}
