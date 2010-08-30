package pds.web.signup.xri.openxri;

import java.util.ResourceBundle;

import nextapp.echo.app.Button;
import nextapp.echo.app.Column;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.PasswordField;
import nextapp.echo.app.Row;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.TextField;
import nextapp.echo.app.WindowPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.ColumnLayoutData;
import nextapp.echo.app.layout.GridLayoutData;
import nextapp.echo.app.layout.SplitPaneLayoutData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pds.store.user.StoreUtil;
import pds.store.user.User;
import pds.store.xri.Xri;
import pds.store.xri.XriStore;
import pds.store.xri.openxri.OpenxriXriData;
import pds.web.signup.xri.util.XriWizard;
import pds.web.ui.MainWindow;
import pds.web.ui.MessageDialog;

public class Step2ContentPane extends ContentPane {

	private static final long serialVersionUID = 14516983150098161L;

	private static final Log log = LogFactory.getLog(Step2ContentPane.class);

	protected ResourceBundle resourceBundle;

	private OpenxriXriSignUpMethod openxriXriSignUpMethod;

	private String parentName;
	private String localName;

	private Label feedbackLabel;
	private TextField emailTextField;
	private PasswordField passwordField;
	private PasswordField password2Field;
	private Button registerButton;

	public Step2ContentPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	private void feedback(String string) {

		if (string != null) {

			this.feedbackLabel.setText(string);
			this.feedbackLabel.setVisible(true);
		} else {

			this.feedbackLabel.setVisible(false);
		}
	}

	private void refresh() {

		this.registerButton.setText("Register " + this.parentName + "*" + this.localName);
	}

	public void setOpenxriXriSignUpMethod(OpenxriXriSignUpMethod openxriXriSignUpMethod) {

		this.openxriXriSignUpMethod = openxriXriSignUpMethod;
	}

	public void setParentNameAndLocalName(String parentName, String localName) {

		this.parentName = parentName;
		this.localName = localName;

		this.refresh();
	}

	private void onRegisterActionPerformed(ActionEvent e) {

		XriStore xriStore = this.openxriXriSignUpMethod.getXriStore();
		pds.store.user.Store userStore = this.openxriXriSignUpMethod.getUserStore();

		String userIdentifier = this.parentName.toString() + "*" + this.localName;
		String password = this.passwordField.getText();
		String password2 = this.password2Field.getText();
		String email = this.emailTextField.getText();

		if (password == null || password.trim().isEmpty() ||
				password2 == null || password2.trim().isEmpty() ||
				email == null || email.trim().isEmpty()) {

			this.feedback("Please complete the form.");
			return;
		}

		// check passwords

		if (! password.equals(password2)) {

			this.feedback("Passwords do not match.");
			return;
		}

		// create user

		User user;

		try {

			user = userStore.createOrUpdateUser(
					userIdentifier, 
					StoreUtil.hashPass(password), 
					null, 
					userIdentifier, 
					email, 
					Boolean.FALSE);
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			this.feedback("Can not create account: " + ex.getMessage());
			return;
		}

		// register i-name

		OpenxriXriData xriData = new OpenxriXriData();
		xriData.setUserIdentifier(user.getIdentifier());

		Xri xri;

		try {

			Xri parentXri = xriStore.findXri(this.parentName);
			xri = xriStore.registerXri(parentXri, '*' + this.localName, xriData, 1);
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			this.feedback("Can not create I-Name: " + ex.getMessage());
			return;
		}

		// set up SEPs and i-services

		try {

			XriWizard.configure(this.openxriXriSignUpMethod, xri);
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			this.feedback("Can not configure I-Name: " + ex.getMessage());
			return;
		}

		// send e-mail

		String to = this.emailTextField.getText();

		this.openxriXriSignUpMethod.sendEmail(xri.getFullName(), to);

		// close us

		MessageDialog.info(xri.getFullName() + " has been registered.");
		
		WindowPane windowPane = (WindowPane) MainWindow.findParentComponentByClass(this, WindowPane.class);
		windowPane.getParent().remove(windowPane);
	}

	private void onBackActionPerformed(ActionEvent e) {

		// go to Step1ContentPane

		Step1ContentPane step1ContentPane = new Step1ContentPane();
		step1ContentPane.setOpenxriXriSignUpMethod(this.openxriXriSignUpMethod);

		WindowPane windowPane = (WindowPane) MainWindow.findParentComponentByClass(this, WindowPane.class);
		windowPane.remove(this);
		windowPane.add(step1ContentPane);
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
		splitPane1.setSeparatorVisible(false);
		add(splitPane1);
		Column column2 = new Column();
		SplitPaneLayoutData column2LayoutData = new SplitPaneLayoutData();
		column2LayoutData.setMinimumSize(new Extent(30, Extent.PX));
		column2LayoutData.setMaximumSize(new Extent(30, Extent.PX));
		column2.setLayoutData(column2LayoutData);
		splitPane1.add(column2);
		feedbackLabel = new Label();
		feedbackLabel.setStyleName("Feedback");
		feedbackLabel.setText("...");
		feedbackLabel.setVisible(false);
		ColumnLayoutData feedbackLabelLayoutData = new ColumnLayoutData();
		feedbackLabelLayoutData.setInsets(new Insets(new Extent(0, Extent.PX),
				new Extent(3, Extent.PX), new Extent(0, Extent.PX), new Extent(
						0, Extent.PX)));
		feedbackLabel.setLayoutData(feedbackLabelLayoutData);
		column2.add(feedbackLabel);
		ContentPane contentPane2 = new ContentPane();
		splitPane1.add(contentPane2);
		Column column3 = new Column();
		column3.setCellSpacing(new Extent(10, Extent.PX));
		contentPane2.add(column3);
		Label label4 = new Label();
		label4.setStyleName("Header");
		label4.setText("Congratulations! This I-Name is available.");
		column3.add(label4);
		Grid grid1 = new Grid();
		grid1.setColumnWidth(0, new Extent(150, Extent.PX));
		column3.add(grid1);
		Label label2 = new Label();
		label2.setStyleName("Default");
		label2.setText("Your E-Mail address:");
		grid1.add(label2);
		emailTextField = new TextField();
		emailTextField.setStyleName("Default");
		emailTextField.setWidth(new Extent(400, Extent.PX));
		emailTextField.setInsets(new Insets(new Extent(5, Extent.PX)));
		emailTextField.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onRegisterActionPerformed(e);
			}
		});
		grid1.add(emailTextField);
		Label label3 = new Label();
		label3.setStyleName("Default");
		label3.setText("Choose a password:");
		grid1.add(label3);
		passwordField = new PasswordField();
		passwordField.setStyleName("Default");
		GridLayoutData passwordFieldLayoutData = new GridLayoutData();
		passwordFieldLayoutData.setInsets(new Insets(new Extent(0, Extent.PX),
				new Extent(10, Extent.PX), new Extent(0, Extent.PX),
				new Extent(0, Extent.PX)));
		passwordField.setLayoutData(passwordFieldLayoutData);
		passwordField.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onRegisterActionPerformed(e);
			}
		});
		grid1.add(passwordField);
		Label label1 = new Label();
		label1.setStyleName("Default");
		label1.setText("Repeat password:");
		grid1.add(label1);
		password2Field = new PasswordField();
		password2Field.setStyleName("Default");
		GridLayoutData password2FieldLayoutData = new GridLayoutData();
		password2FieldLayoutData.setInsets(new Insets(new Extent(0, Extent.PX),
				new Extent(10, Extent.PX), new Extent(0, Extent.PX),
				new Extent(0, Extent.PX)));
		password2Field.setLayoutData(password2FieldLayoutData);
		password2Field.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onRegisterActionPerformed(e);
			}
		});
		grid1.add(password2Field);
		Row row3 = new Row();
		row3.setCellSpacing(new Extent(10, Extent.PX));
		column3.add(row3);
		registerButton = new Button();
		registerButton.setStyleName("Default");
		registerButton.setText("Register the name");
		registerButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onRegisterActionPerformed(e);
			}
		});
		row3.add(registerButton);
		Button button1 = new Button();
		button1.setStyleName("Default");
		button1.setText("Back");
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onBackActionPerformed(e);
			}
		});
		row3.add(button1);
	}
}
