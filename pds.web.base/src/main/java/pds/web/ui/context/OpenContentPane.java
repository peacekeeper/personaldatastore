package pds.web.ui.context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.Button;
import nextapp.echo.app.Column;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.FillImage;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.SplitPaneLayoutData;

import org.eclipse.higgins.xdi4j.addressing.Addressing;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;

import pds.web.PDSApplication;
import pds.web.components.xdi.XdiPanel;
import pds.xdi.XdiContext;
import pds.xdi.XdiException;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;

public class OpenContentPane extends ContentPane implements XdiGraphListener {

	private static final long serialVersionUID = -8974342563665273260L;

	private static final SimpleDateFormat DATEFORMAT;

	static {

		DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		DATEFORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	protected ResourceBundle resourceBundle;

	private XdiContext context;
	private XRI3 address = new XRI3("$");
	private XRI3 addressFirstAccess = new XRI3("$/$d$first");
	private XRI3 addressLastAccess = new XRI3("$/$d$last");

	private Label identifierTextField;
	private Label canonicalTextField;
	private Label endpointTextField;
	private XdiPanel xdiPanel;

	/**
	 * Creates a new <code>LoggedInContentPane</code>.
	 */
	public OpenContentPane() {
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
		
		if (this.context != null) this.context.removeXdiGraphListener(this);
	}

	private void refresh() {

		this.identifierTextField.setText(this.context.getIdentifier());
		this.canonicalTextField.setText(this.context.getCanonical().toString());
		this.endpointTextField.setText(this.context.getEndpoint());

		this.xdiPanel.setContextAndMainAddressAndGetAddresses(this.context, this.address, this.xdiGetAddresses());
	}

	public XRI3[] xdiGetAddresses() {

		return new XRI3[] {
				this.address
		};
	}

	public XRI3[] xdiAddAddresses() {

		return new XRI3[] {
				new XRI3("" + this.address + "/$$")
		};
	}

	public XRI3[] xdiModAddresses() {

		return new XRI3[] {
				new XRI3("" + this.address + "/$$")
		};
	}

	public XRI3[] xdiDelAddresses() {

		return new XRI3[] {
				this.address
		};
	}

	@Override
	public void onXdiGraphEvent(XdiGraphEvent xdiGraphEvent) {

		this.refresh();
	}

	public void setContext(XdiContext context) {

		// remove us as listener
		
		if (this.context != null) this.context.removeXdiGraphListener(this);

		// refresh
		
		this.context = context;

		this.refresh();

		// add us as listener

		this.context.addXdiGraphListener(this);
	}

	private Date getFirstAccess() throws XdiException {

		// $get

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, this.addressFirstAccess);
		MessageResult messageResult = this.context.send(operation);

		String data = Addressing.findLiteralData(messageResult.getGraph(), this.addressFirstAccess);
		if (data == null) return null;

		try {

			return DATEFORMAT.parse(data);
		} catch (ParseException ex) {

			return null;
		}
	}

	private Date getLastAccess() throws XdiException {

		// $get

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, this.addressLastAccess);
		MessageResult messageResult = this.context.send(operation);

		String data = Addressing.findLiteralData(messageResult.getGraph(), this.addressLastAccess);
		if (data == null) return null;

		try {

			return DATEFORMAT.parse(data);
		} catch (ParseException ex) {

			return null;
		}
	}

	private void onCloseActionPerformed(ActionEvent e) {

		PDSApplication.getApp().closeContext();
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
		ResourceImageReference imageReference1 = new ResourceImageReference(
				"/pds/web/resource/image/separator.png");
		splitPane1.setSeparatorVerticalImage(new FillImage(imageReference1));
		splitPane1.setSeparatorHeight(new Extent(10, Extent.PX));
		splitPane1.setSeparatorVisible(true);
		add(splitPane1);
		Column column1 = new Column();
		column1.setCellSpacing(new Extent(10, Extent.PX));
		splitPane1.add(column1);
		Grid grid1 = new Grid();
		grid1.setWidth(new Extent(100, Extent.PERCENT));
		grid1.setInsets(new Insets(new Extent(5, Extent.PX)));
		column1.add(grid1);
		Label label2 = new Label();
		label2.setStyleName("Default");
		label2.setText("Identifier:");
		grid1.add(label2);
		identifierTextField = new Label();
		identifierTextField.setStyleName("Bold");
		identifierTextField.setText("...");
		grid1.add(identifierTextField);
		Label label3 = new Label();
		label3.setStyleName("Default");
		label3.setText("Canonical:");
		grid1.add(label3);
		canonicalTextField = new Label();
		canonicalTextField.setStyleName("Bold");
		canonicalTextField.setText("...");
		grid1.add(canonicalTextField);
		Label label5 = new Label();
		label5.setStyleName("Default");
		label5.setText("Endpoint:");
		grid1.add(label5);
		endpointTextField = new Label();
		endpointTextField.setStyleName("Bold");
		endpointTextField.setText("...");
		grid1.add(endpointTextField);
		Row row1 = new Row();
		row1.setAlignment(new Alignment(Alignment.RIGHT, Alignment.DEFAULT));
		row1.setCellSpacing(new Extent(10, Extent.PX));
		SplitPaneLayoutData row1LayoutData = new SplitPaneLayoutData();
		row1LayoutData.setMinimumSize(new Extent(40, Extent.PX));
		row1LayoutData.setMaximumSize(new Extent(40, Extent.PX));
		row1LayoutData.setOverflow(SplitPaneLayoutData.OVERFLOW_HIDDEN);
		row1.setLayoutData(row1LayoutData);
		splitPane1.add(row1);
		xdiPanel = new XdiPanel();
		row1.add(xdiPanel);
		Button button1 = new Button();
		button1.setStyleName("Default");
		button1.setText("Close Personal Data Store");
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onCloseActionPerformed(e);
			}
		});
		row1.add(button1);
	}
}
