package pds.web.signin.xri;


import java.util.ResourceBundle;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.Button;
import nextapp.echo.app.Column;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.Panel;
import nextapp.echo.app.PasswordField;
import nextapp.echo.app.Row;
import nextapp.echo.app.TextField;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.SplitPaneLayoutData;
import pds.web.PDSApplication;
import pds.web.ui.MessageDialog;
import pds.xdi.XdiClient;
import pds.xdi.XdiEndpoint;

public class XriSignInPanel extends Panel {

	private static final long serialVersionUID = 46284183174314347L;

	protected ResourceBundle resourceBundle;

	private TextField inameTextField;
	private PasswordField secretTokenField;

	/**
	 * Creates a new <code>ManualSignInPanel</code>.
	 */
	public XriSignInPanel() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void init() {

		super.init();
	}

	private void onOpenActionPerformed(ActionEvent e) {

		XdiClient xdiClient = PDSApplication.getApp().getXdiClient();

		String iname = this.inameTextField.getText();
		String secretToken = this.secretTokenField.getText();
		if (iname == null || iname.trim().equals("")) return;
		if (secretToken == null || secretToken.trim().equals("")) return;

		// try to open the context

		try {

			XdiEndpoint endpoint = xdiClient.resolveEndpointByIname(iname, secretToken);

			PDSApplication.getApp().openEndpoint(endpoint);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, we could not open your Personal Cloud: " + ex.getMessage(), ex);
			return;
		}
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setInsets(new Insets(new Extent(10, Extent.PX)));
		Column column2 = new Column();
		column2.setCellSpacing(new Extent(10, Extent.PX));
		add(column2);
		Label label2 = new Label();
		label2.setStyleName("Header");
		label2.setText("I-Name Sign-In");
		column2.add(label2);
		Label label4 = new Label();
		label4.setStyleName("Default");
		label4.setText("Welcome. Please enter your I-Name and Secret Token.");
		column2.add(label4);
		Grid grid2 = new Grid();
		grid2.setWidth(new Extent(100, Extent.PERCENT));
		grid2.setColumnWidth(0, new Extent(150, Extent.PX));
		column2.add(grid2);
		Label label1 = new Label();
		label1.setStyleName("Default");
		label1.setText("I-Name:");
		grid2.add(label1);
		inameTextField = new TextField();
		inameTextField.setStyleName("Default");
		inameTextField.setWidth(new Extent(100, Extent.PERCENT));
		inameTextField.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onOpenActionPerformed(e);
			}
		});
		grid2.add(inameTextField);
		Label label3 = new Label();
		label3.setStyleName("Default");
		label3.setText("Secret Token:");
		grid2.add(label3);
		secretTokenField = new PasswordField();
		secretTokenField.setStyleName("Default");
		secretTokenField.setWidth(new Extent(100, Extent.PERCENT));
		grid2.add(secretTokenField);
		Row row2 = new Row();
		row2.setAlignment(new Alignment(Alignment.RIGHT, Alignment.DEFAULT));
		row2.setCellSpacing(new Extent(10, Extent.PX));
		SplitPaneLayoutData row2LayoutData = new SplitPaneLayoutData();
		row2LayoutData.setMinimumSize(new Extent(40, Extent.PX));
		row2LayoutData.setMaximumSize(new Extent(40, Extent.PX));
		row2LayoutData.setOverflow(SplitPaneLayoutData.OVERFLOW_HIDDEN);
		row2.setLayoutData(row2LayoutData);
		column2.add(row2);
		Button button2 = new Button();
		button2.setStyleName("Default");
		button2.setText("Open Personal Cloud");
		button2.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onOpenActionPerformed(e);
			}
		});
		row2.add(button2);
	}
}
