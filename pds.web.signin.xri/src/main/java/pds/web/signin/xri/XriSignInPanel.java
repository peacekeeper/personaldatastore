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
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.TextField;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.GridLayoutData;
import nextapp.echo.app.layout.SplitPaneLayoutData;
import pds.web.PDSApplication;
import pds.web.ui.MainWindow;
import pds.web.ui.MessageDialog;
import pds.web.ui.html.HtmlWindowPane;
import pds.web.xdi.Xdi;
import pds.web.xdi.XdiContext;

public class XriSignInPanel extends Panel {

	private static final long serialVersionUID = 46284183174314347L;

	protected ResourceBundle resourceBundle;

	private XriSignInMethod xriSignInMethod;

	private TextField inameTextField;
	private PasswordField passwordField;
	private Column xriSignUpPanelColumn;

	/**
	 * Creates a new <code>LoginContentPane</code>.
	 */
	public XriSignInPanel() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void init() {

		super.init();

		// XriSignUpMethods

		this.xriSignUpPanelColumn.removeAll();
		for (XriSignUpMethod xriSignUpMethod : this.xriSignInMethod.getXriSignUpMethods()) {

			this.xriSignUpPanelColumn.add(xriSignUpMethod.newPanel());
		}
	}

	public void setXriSignInMethod(XriSignInMethod xriSignInMethod) {

		this.xriSignInMethod = xriSignInMethod;
	}

	private void onOpenActionPerformed(ActionEvent e) {

		Xdi xdi = PDSApplication.getApp().getXdi();
		
		String iname = this.inameTextField.getText();
		if (iname == null || iname.trim().equals("")) return;

		String password = this.passwordField.getText();
		if (password == null || password.trim().equals("")) return;

		// resolve the i-name, instantiate context and check password

		try {

			XdiContext context = xdi.resolveContextByIname(iname, password);

			PDSApplication.getApp().openContext(context);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, we could not open your Personal Data Store: " + ex.getMessage(), ex);
			return;
		}
	}

	private void onWhatInamesActionPerformed(ActionEvent e) {

		HtmlWindowPane htmlWindowPane = new HtmlWindowPane();
		MainWindow.findMainContentPane(this).add(htmlWindowPane);

		htmlWindowPane.setTitle("About I-Names");
		htmlWindowPane.setSrc("/html/about-inames");
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
		Label label4 = new Label();
		label4.setStyleName("Header");
		label4.setText("Enter your I-Name / Password:");
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
		Label label6 = new Label();
		label6.setStyleName("Default");
		label6.setText("Password:");
		grid2.add(label6);
		passwordField = new PasswordField();
		passwordField.setStyleName("Default");
		passwordField.setWidth(new Extent(100, Extent.PERCENT));
		GridLayoutData passwordFieldLayoutData = new GridLayoutData();
		passwordFieldLayoutData.setInsets(new Insets(new Extent(0, Extent.PX),
				new Extent(10, Extent.PX), new Extent(0, Extent.PX),
				new Extent(0, Extent.PX)));
		passwordField.setLayoutData(passwordFieldLayoutData);
		passwordField.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onOpenActionPerformed(e);
			}
		});
		grid2.add(passwordField);
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
		button2.setText("Open Personal Data Store");
		button2.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onOpenActionPerformed(e);
			}
		});
		row2.add(button2);
		Button button1 = new Button();
		button1.setStyleName("Plain");
		ResourceImageReference imageReference1 = new ResourceImageReference(
				"/pds/web/resource/image/tooltip.png");
		button1.setIcon(imageReference1);
		button1.setText("What are I-Names?");
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onWhatInamesActionPerformed(e);
			}
		});
		column2.add(button1);
		xriSignUpPanelColumn = new Column();
		column2.add(xriSignUpPanelColumn);
	}
}
