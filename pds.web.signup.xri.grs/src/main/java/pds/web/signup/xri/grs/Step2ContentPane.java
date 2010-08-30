package pds.web.signup.xri.grs;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.javainetlocator.InetAddressLocator;
import nextapp.echo.app.Button;
import nextapp.echo.app.CheckBox;
import nextapp.echo.app.Column;
import nextapp.echo.app.Command;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.FillImage;
import nextapp.echo.app.Font;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.PasswordField;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.SelectField;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.TaskQueueHandle;
import nextapp.echo.app.TextField;
import nextapp.echo.app.WindowPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.ColumnLayoutData;
import nextapp.echo.app.layout.SplitPaneLayoutData;
import nextapp.echo.webcontainer.WebContainerServlet;
import nextapp.echo.webcontainer.command.BrowserRedirectCommand;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pds.store.user.StoreUtil;
import pds.store.user.User;
import pds.store.xri.Xri;
import pds.store.xri.XriStore;
import pds.store.xri.grs.GrsXriData;
import pds.web.PDSApplication;
import pds.web.servlet.external.ExternalCallReceiver;
import pds.web.signup.xri.grs.components.mpay.MpayPanel;
import pds.web.signup.xri.grs.models.CountriesModel;
import pds.web.signup.xri.grs.models.CurrenciesModel;
import pds.web.signup.xri.grs.models.YearsModel;
import pds.web.signup.xri.util.XriWizard;
import pds.web.ui.MainWindow;
import pds.web.ui.MessageDialog;

import com.fullxri.mpay4java.MpayResponse;
import com.fullxri.mpay4java.MpayTools;

public class Step2ContentPane extends ContentPane {

	private static final long serialVersionUID = 1088900544682956073L;

	private static final Log log = LogFactory.getLog(Step2ContentPane.class.getName());

	protected ResourceBundle resourceBundle;

	private GrsXriSignUpMethod grsXriSignUpMethod;

	private boolean completed;
	private String iname;
	private String usdprice;

	private Label feedbackLabel;
	private CheckBox acceptCheckBox;
	private TextField nameTextField;
	private TextField primaryVoiceTextField;
	private TextField organizationTextField;
	private TextField secondaryVoiceTextField;
	private TextField street1TextField;
	private TextField faxTextField;
	private TextField street2TextField;
	private TextField primaryEmailTextField;
	private TextField postalCodeTextField;
	private TextField secondaryEmailTextField;
	private TextField cityTextField;
	private TextField pagerTextField;
	private TextField stateTextField;
	private SelectField countrySelectField;
	private MpayPanel mpayPanel;
	private PasswordField passwordField;
	private Button registerButton;
	private Label priceCurrencyLabel;
	private Label priceLabel;
	private SelectField yearsSelectField;
	private SelectField currencySelectField;
	private Row externalCallReceiverRow;

	public Step2ContentPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void init() {

		super.init();

		this.yearsSelectField.setModel(new YearsModel());
		this.currencySelectField.setModel(new CurrenciesModel());
		this.countrySelectField.setModel(new CountriesModel());

		this.yearsSelectField.setSelectedItem(YearsModel.DEFAULT_YEARS);
		this.currencySelectField.setSelectedItem(CurrenciesModel.DEFAULT_CURRENCY);

		try {

			InetAddress inetAddress = InetAddress.getByName(WebContainerServlet.getActiveConnection().getRequest().getRemoteAddr());
			Locale locale = InetAddressLocator.getLocale(inetAddress);
			log.info(locale);
			log.info(locale.getCountry());
			this.countrySelectField.setSelectedItem(locale.getCountry());
		} catch (Exception ex) {

			log.warn("Can not detect locale: " + ex.getMessage(), ex);
			this.countrySelectField.setSelectedItem(CountriesModel.DEFAULT_COUNTRY);
		}

		this.yearsSelectField.addActionListener(new ActionListener() {

			private static final long serialVersionUID = 5650921245614373954L;

			@Override
			public void actionPerformed(ActionEvent e) {

				Step2ContentPane.this.refresh();
			}
		});

		this.currencySelectField.addActionListener(new ActionListener() {

			private static final long serialVersionUID = 5650921245614373954L;

			@Override
			public void actionPerformed(ActionEvent e) {

				Step2ContentPane.this.refresh();
			}
		});

		this.externalCallReceiverRow.add(new CallbackMpayConfirmationComponent());
		this.externalCallReceiverRow.add(new CallbackMpaySuccessComponent());
		this.externalCallReceiverRow.add(new CallbackMpayErrorComponent());
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

		this.usdprice = this.iname.charAt(0) == '=' ? "12.00" : "55.00";
		this.registerButton.setText("Register " + this.iname);

		String currency = this.currencySelectField.getSelectedItem() != null ? (String) this.currencySelectField.getSelectedItem() : CurrenciesModel.DEFAULT_CURRENCY;
		Integer years = this.yearsSelectField.getSelectedItem() != null ? (Integer) this.yearsSelectField.getSelectedItem() : YearsModel.DEFAULT_YEARS;

		this.priceCurrencyLabel.setText(currency);
		this.priceLabel.setText(CurrenciesModel.formatPrice(CurrenciesModel.priceForUsdprice(currency, Double.parseDouble(this.usdprice) * years.intValue())));
	}

	public void setGrsXriSignUpMethod(GrsXriSignUpMethod grsXriSignUpMethod) {

		this.grsXriSignUpMethod = grsXriSignUpMethod;
	}

	public void setIname(String iname) {

		this.iname = iname;

		this.refresh();
	}

	public String getIname() {

		return this.iname;
	}

	private void onRegisterActionPerformed(ActionEvent e) {

		if (this.completed) {

			this.feedback("The operation has already been completed.");
			return;
		}

		// form validation

		if (this.nameTextField.getText().trim().isEmpty() ||
				this.street1TextField.getText().trim().isEmpty() ||
				this.postalCodeTextField.getText().trim().isEmpty() ||
				this.cityTextField.getText().trim().isEmpty() ||
				this.primaryVoiceTextField.getText().trim().isEmpty() ||
				this.primaryEmailTextField.getText().trim().isEmpty() ||
				this.countrySelectField.getSelectedItem() == null ||
				this.passwordField.getText().trim().isEmpty()) {

			this.feedback("Please complete the required form fields.");
			return;
		}

		if (! this.acceptCheckBox.isSelected()) {

			this.feedback("Agreement to the terms of service is required.");
			return;
		}

		// fix phones

		if (this.primaryVoiceTextField.getText() != null && this.primaryVoiceTextField.getText().trim().length() > 0) this.primaryVoiceTextField.setText(fixPhone(this.primaryVoiceTextField.getText(), (String) this.countrySelectField.getSelectedItem()));
		if (this.secondaryVoiceTextField.getText() != null && this.secondaryVoiceTextField.getText().trim().length() > 0) this.secondaryVoiceTextField.setText(fixPhone(this.secondaryVoiceTextField.getText(), (String) this.countrySelectField.getSelectedItem()));
		if (this.faxTextField.getText() != null && this.faxTextField.getText().trim().length() > 0) this.faxTextField.setText(fixPhone(this.faxTextField.getText(), (String) this.countrySelectField.getSelectedItem()));
		if (this.pagerTextField.getText() != null && this.pagerTextField.getText().trim().length() > 0) this.pagerTextField.setText(fixPhone(this.pagerTextField.getText(), (String) this.countrySelectField.getSelectedItem()));

		// charge user

		if (Double.parseDouble(this.priceLabel.getText()) == 0) {

			this.goSuccess(PDSApplication.getApp());
			return;
		}

		MpayTools mpayTools = this.grsXriSignUpMethod.getMpayTools();
		MpayResponse mpayResponse;

		String ip = WebContainerServlet.getActiveConnection().getRequest().getRemoteAddr();
		String name = "" + this.nameTextField.getText();
		String address = "" + this.street1TextField.getText() + " / " + this.street2TextField.getText() + " / " + this.postalCodeTextField.getText() + " / " + this.cityTextField.getText() + " / " + this.stateTextField.getText() + " / " + CountriesModel.nameToCode2((String) this.countrySelectField.getSelectedItem());

		String price = this.priceLabel.getText().replace(".", "");

		try {

			if (this.mpayPanel.getMpayptype().equals(MpayTools.PTYPE_CC)) {

				mpayResponse = mpayTools.acceptPayment(
						MpayTools.PTYPE_CC,
						this.mpayPanel.getMpaybrand(),
						price,
						(String) this.currencySelectField.getSelectedItem(),
						this.mpayPanel.getMpayidentifier(), 
						this.mpayPanel.getMpayexpiryyear() + this.mpayPanel.getMpayexpirymonth(), 
						this.mpayPanel.getMpaycvc(), 
						MpayTools.CLEARING_IMMEDIATELY,
						null,
						MpayTools.AUTH3DS_YES, 
						MpayTools.PROFILE_NO, 
						null, 
						null, 
						null, 
						null, 
						"REGISTER " + this.iname, 
						null,
						ip,
						name,
						address);
			} else if (this.mpayPanel.getMpayptype().equals(MpayTools.PTYPE_PAYPAL)) {

				mpayResponse = mpayTools.acceptPayment(
						MpayTools.PTYPE_PAYPAL,
						null,
						price,
						(String) this.currencySelectField.getSelectedItem(),
						null,
						null,
						null,
						MpayTools.CLEARING_IMMEDIATELY, 
						MpayTools.COMMIT_YES, 
						null, 
						MpayTools.PROFILE_NO, 
						null, 
						null, 
						null, 
						null, 
						"REGISTER " + this.iname, 
						null,
						ip,
						name,
						address);
			} else {

				throw new IllegalStateException("Invalid payment method.");
			}
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			MessageDialog.problem("Sorry, a problem occurred while processing the payment transaction: " + ex.getMessage(), ex);
			return;
		}

		if (mpayResponse.getStatus().equals(MpayResponse.STATUS_OK)) {

			if (mpayResponse.getReturnCode().equals(MpayResponse.RETURNCODE_OK)) {

				this.goSuccess(PDSApplication.getApp());
				return;
			} else if (mpayResponse.getReturnCode().equals(MpayResponse.RETURNCODE_REDIRECT)) {

				Command command = new BrowserRedirectCommand(mpayResponse.getLocation());
				PDSApplication.getApp().enqueueCommand(command);
				return;
			} else {

				throw new RuntimeException("Unexpected RETURNCODE");
			}
		} else if (mpayResponse.getStatus().equals(MpayResponse.STATUS_ERROR)) {

			this.goError(PDSApplication.getApp());
			return;
		} else {

			throw new RuntimeException("Unexpected STATUSCODE");
		}
	}

	private void onBackActionPerformed(ActionEvent e) {

		// go to Step1ContentPane

		Step1ContentPane step1ContentPane = new Step1ContentPane();
		step1ContentPane.setGrsXriSignUpMethod(this.grsXriSignUpMethod);

		WindowPane windowPane = (WindowPane) MainWindow.findParentComponentByClass(this, WindowPane.class);
		windowPane.remove(this);
		windowPane.setWidth(new Extent(600, Extent.PX));
		windowPane.setHeight(new Extent(400, Extent.PX));
		windowPane.add(step1ContentPane);
	}

	private class CallbackMpayConfirmationComponent extends Label implements ExternalCallReceiver {

		private static final long serialVersionUID = -3344243417023828102L;

		@Override
		public void init() {

			super.init();

			this.setText("callbackMpayConfirmationComponent");
			this.setId("callbackMpayConfirmationComponent");
		}

		@Override
		@SuppressWarnings("unchecked")
		public void onExternalCall(PDSApplication pdsApplication, HttpServletRequest request, HttpServletResponse response) throws IOException {

			try {

				MpayTools mpayTools = Step2ContentPane.this.grsXriSignUpMethod.getMpayTools();

				Map<String, ?> parameters = (Map<String, ?>) request.getParameterMap();
				String query = request.getQueryString();

				mpayTools.callbackConfirmation(parameters, query);

				response.getOutputStream().println("Confirmation received.");
			} catch (Exception ex) {

				log.error(ex.getMessage(), ex);
				response.sendRedirect("/");
			}
		}
	}

	private class CallbackMpaySuccessComponent extends Label implements ExternalCallReceiver {

		private static final long serialVersionUID = 3357969828392651248L;

		@Override
		public void init() {

			super.init();

			this.setText("callbackMpaySuccessComponent");
			this.setId("callbackMpaySuccessComponent");
		}

		@Override
		@SuppressWarnings("unchecked")
		public void onExternalCall(PDSApplication pdsApplication, HttpServletRequest request, HttpServletResponse response) throws IOException {

			try {

				MpayTools mpayTools = Step2ContentPane.this.grsXriSignUpMethod.getMpayTools();

				Map<String, ?> parameters = (Map<String, ?>) request.getParameterMap();
				String query = request.getQueryString();

				mpayTools.callbackSuccess(parameters, query);

				Step2ContentPane.this.goSuccess(pdsApplication);
				response.sendRedirect("/");
			} catch (Exception ex) {

				log.error(ex.getMessage(), ex);
				response.sendRedirect("/");
			}
		}
	}

	private class CallbackMpayErrorComponent extends Label implements ExternalCallReceiver {

		private static final long serialVersionUID = 302022971241838925L;

		@Override
		public void init() {

			super.init();

			this.setText("callbackMpayConfirmationComponent");
			this.setId("callbackMpayConfirmationComponent");
		}

		@Override
		@SuppressWarnings("unchecked")
		public void onExternalCall(PDSApplication pdsApplication, HttpServletRequest request, HttpServletResponse response) throws IOException {

			try {

				MpayTools mpayTools = Step2ContentPane.this.grsXriSignUpMethod.getMpayTools();

				Map<String, ?> parameters = (Map<String, ?>) request.getParameterMap();
				String query = request.getQueryString();

				mpayTools.callbackError(parameters, query);

				Step2ContentPane.this.goError(pdsApplication);
				response.sendRedirect("/");
			} catch (Exception ex) {

				log.error(ex.getMessage(), ex);
				response.sendRedirect("/");
			}
		}
	}

	public void goSuccess(PDSApplication pdsApplication) {

		this.register(pdsApplication);
	}

	public void goError(PDSApplication pdsApplication) {

		TaskQueueHandle taskQueueHandle = pdsApplication.getTaskQueueHandle();

		pdsApplication.enqueueTask(taskQueueHandle, new Runnable() {

			@Override
			public void run() {

				MessageDialog.problem("Sorry, a problem occurred while processing the payment transaction.", null);
			}
		});
	}

	private void register(PDSApplication pdsApplication) {

		TaskQueueHandle taskQueueHandle = pdsApplication.getTaskQueueHandle();
		XriStore xriStore = this.grsXriSignUpMethod.getXriStore();
		pds.store.user.Store userStore = this.grsXriSignUpMethod.getUserStore();

		log.info("Registering i-name " + this.iname + " as new account.");

		// try to create the i-name

		Xri xri;
		User user;

		try {

			// create user

			user = userStore.createOrUpdateUser(
					this.iname, 
					StoreUtil.hashPass(this.passwordField.getText()), 
					null,
					this.iname, 
					this.primaryEmailTextField.getText(),
					Boolean.FALSE);

			// register i-name

			GrsXriData xriData = new GrsXriData();
			xriData.setUserIdentifier(user.getIdentifier());
			xriData.setName(this.nameTextField.getText());
			xriData.setOrganization(this.organizationTextField.getText() != null && this.organizationTextField.getText().trim().length() > 0 ? this.organizationTextField.getText() : null);
			xriData.setStreet(this.street2TextField.getText() != null && this.street2TextField.getText().trim().length() > 0 ? new String[] { this.street1TextField.getText(), this.street2TextField.getText() } : new String[] { this.street1TextField.getText() });
			xriData.setPostalCode(this.postalCodeTextField.getText());
			xriData.setCity(this.cityTextField.getText());
			xriData.setState(this.stateTextField.getText() != null && this.stateTextField.getText().trim().length() > 0 ? this.stateTextField.getText() : null);
			xriData.setCountryCode(CountriesModel.nameToCode2((String) this.countrySelectField.getSelectedItem()));
			xriData.setPrimaryVoice(this.primaryVoiceTextField.getText());
			xriData.setSecondaryVoice(this.secondaryVoiceTextField.getText() != null && this.secondaryVoiceTextField.getText().trim().length() > 0 ? this.secondaryVoiceTextField.getText() : null);
			xriData.setFax(this.faxTextField.getText() != null && this.faxTextField.getText().trim().length() > 0 ? this.faxTextField.getText() : null);
			xriData.setPrimaryEmail(this.primaryEmailTextField.getText());
			xriData.setSecondaryEmail(this.secondaryEmailTextField.getText() != null && this.secondaryEmailTextField.getText().trim().length() > 0 ? this.secondaryEmailTextField.getText() : null);
			xriData.setPager(this.pagerTextField.getText() != null && this.pagerTextField.getText().trim().length() > 0 ? this.pagerTextField.getText() : null);

			xri = xriStore.registerXri(null, this.iname, xriData, ((Integer) this.yearsSelectField.getSelectedItem()).intValue());
		} catch (final Exception ex) {

			pdsApplication.enqueueTask(taskQueueHandle, new Runnable() {

				@Override
				public void run() {

					log.error(ex.getMessage(), ex);
					MessageDialog.problem("Can not create I-Name: " + ex.getMessage(), ex);
				}
			});
			return;
		}

		// set up SEPs and i-services

		try {

			XriWizard.configure(this.grsXriSignUpMethod, xri);
		} catch (final Exception ex) {

			pdsApplication.enqueueTask(taskQueueHandle, new Runnable() {

				@Override
				public void run() {

					log.warn(ex.getMessage(), ex);
					MessageDialog.problem("Can not configure I-Name: " + ex.getMessage(), null);
				}
			});
			return;
		}

		// send e-mail

		String to = this.primaryEmailTextField.getText();

		this.grsXriSignUpMethod.sendEmail(xri.getFullName(), to);

		// close us

		this.completed = true;

		MessageDialog.info(xri.getFullName() + " has been registered.");
		
		WindowPane windowPane = (WindowPane) MainWindow.findParentComponentByClass(this, WindowPane.class);
		windowPane.getParent().remove(windowPane);
	}

	private static String fixPhone(String phone, String country) {

		String fixedPhone = phone.trim();
		String countryPrefix = country != null ? CountriesModel.nameToPrefix(country) : "1";

		if (fixedPhone.indexOf('+') != 0) fixedPhone = '+' + countryPrefix + ' ' + fixedPhone;

		if ((fixedPhone.indexOf(' ') < 0 || fixedPhone.indexOf(' ') > 4) &&
				(fixedPhone.indexOf('/') < 0 || fixedPhone.indexOf('/') > 4) &&
				(fixedPhone.indexOf('.') < 0 || fixedPhone.indexOf('.') > 4) &&
				(fixedPhone.indexOf(':') < 0 || fixedPhone.indexOf(':') > 4) &&
				(fixedPhone.indexOf('(') < 0 || fixedPhone.indexOf('(') > 4) &&
				(fixedPhone.indexOf(')') < 0 || fixedPhone.indexOf(')') > 4) &&
				(fixedPhone.indexOf('-') < 0 || fixedPhone.indexOf('-') > 4)) {

			fixedPhone = fixedPhone.substring(0, 3) + ' ' + fixedPhone.substring(3);
		}

		return(fixedPhone);
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
		Column column1 = new Column();
		SplitPaneLayoutData column1LayoutData = new SplitPaneLayoutData();
		column1LayoutData.setMinimumSize(new Extent(30, Extent.PX));
		column1LayoutData.setMaximumSize(new Extent(30, Extent.PX));
		column1.setLayoutData(column1LayoutData);
		splitPane1.add(column1);
		feedbackLabel = new Label();
		feedbackLabel.setStyleName("Feedback");
		feedbackLabel.setText("...");
		feedbackLabel.setVisible(false);
		ColumnLayoutData feedbackLabelLayoutData = new ColumnLayoutData();
		feedbackLabelLayoutData.setInsets(new Insets(new Extent(5, Extent.PX),
				new Extent(5, Extent.PX), new Extent(0, Extent.PX), new Extent(
						0, Extent.PX)));
		feedbackLabel.setLayoutData(feedbackLabelLayoutData);
		column1.add(feedbackLabel);
		SplitPane splitPane2 = new SplitPane();
		splitPane2.setStyleName("Default");
		splitPane2.setOrientation(SplitPane.ORIENTATION_VERTICAL_TOP_BOTTOM);
		splitPane2.setSeparatorVisible(false);
		splitPane1.add(splitPane2);
		ContentPane contentPane1 = new ContentPane();
		SplitPaneLayoutData contentPane1LayoutData = new SplitPaneLayoutData();
		ResourceImageReference imageReference1 = new ResourceImageReference(
				"/pds/web/signup/xri/grs/stripes.png");
		contentPane1LayoutData
				.setBackgroundImage(new FillImage(imageReference1));
		contentPane1.setLayoutData(contentPane1LayoutData);
		splitPane2.add(contentPane1);
		Column column2 = new Column();
		column2.setInsets(new Insets(new Extent(10, Extent.PX)));
		contentPane1.add(column2);
		Row row3 = new Row();
		row3.setCellSpacing(new Extent(10, Extent.PX));
		column2.add(row3);
		Label label1 = new Label();
		label1.setStyleName("Default");
		label1.setText("Registration Fee:");
		row3.add(label1);
		priceCurrencyLabel = new Label();
		priceCurrencyLabel.setStyleName("Bold");
		priceCurrencyLabel.setText("...");
		row3.add(priceCurrencyLabel);
		priceLabel = new Label();
		priceLabel.setStyleName("Bold");
		priceLabel.setText("...");
		row3.add(priceLabel);
		Label label4 = new Label();
		label4.setStyleName("Default");
		label4.setText("Registration Period:");
		row3.add(label4);
		yearsSelectField = new SelectField();
		yearsSelectField.setStyleName("Default");
		yearsSelectField.setFont(new Font(new Font.Typeface("Arial"),
				Font.BOLD, new Extent(12, Extent.PT)));
		row3.add(yearsSelectField);
		Label label2 = new Label();
		label2.setStyleName("Bold");
		label2.setText("years");
		row3.add(label2);
		Label label19 = new Label();
		label19.setStyleName("Default");
		label19.setText("Currency:");
		row3.add(label19);
		currencySelectField = new SelectField();
		currencySelectField.setStyleName("Default");
		currencySelectField.setFont(new Font(new Font.Typeface("Arial"),
				Font.BOLD, new Extent(12, Extent.PT)));
		row3.add(currencySelectField);
		Grid grid1 = new Grid();
		grid1.setOrientation(Grid.ORIENTATION_HORIZONTAL);
		grid1.setInsets(new Insets(new Extent(4, Extent.PX)));
		grid1.setSize(4);
		ColumnLayoutData grid1LayoutData = new ColumnLayoutData();
		grid1LayoutData.setInsets(new Insets(new Extent(0, Extent.PX),
				new Extent(10, Extent.PX), new Extent(0, Extent.PX),
				new Extent(0, Extent.PX)));
		grid1.setLayoutData(grid1LayoutData);
		column2.add(grid1);
		Label label3 = new Label();
		label3.setStyleName("Default");
		label3.setText("Name:");
		grid1.add(label3);
		nameTextField = new TextField();
		nameTextField.setStyleName("Default");
		nameTextField.setInsets(new Insets(new Extent(2, Extent.PX)));
		grid1.add(nameTextField);
		Label label5 = new Label();
		label5.setStyleName("Default");
		label5.setText("1st Phone:");
		grid1.add(label5);
		primaryVoiceTextField = new TextField();
		primaryVoiceTextField.setStyleName("Default");
		primaryVoiceTextField.setInsets(new Insets(new Extent(2, Extent.PX)));
		grid1.add(primaryVoiceTextField);
		Label label6 = new Label();
		label6.setStyleName("Optional");
		label6.setText("Organization:");
		grid1.add(label6);
		organizationTextField = new TextField();
		organizationTextField.setStyleName("Default");
		organizationTextField.setInsets(new Insets(new Extent(2, Extent.PX)));
		grid1.add(organizationTextField);
		Label label7 = new Label();
		label7.setStyleName("Optional");
		label7.setText("2nd Phone:");
		grid1.add(label7);
		secondaryVoiceTextField = new TextField();
		secondaryVoiceTextField.setStyleName("Default");
		secondaryVoiceTextField.setInsets(new Insets(new Extent(2, Extent.PX)));
		grid1.add(secondaryVoiceTextField);
		Label label8 = new Label();
		label8.setStyleName("Default");
		label8.setText("Street:");
		grid1.add(label8);
		street1TextField = new TextField();
		street1TextField.setStyleName("Default");
		street1TextField.setInsets(new Insets(new Extent(2, Extent.PX)));
		grid1.add(street1TextField);
		Label label9 = new Label();
		label9.setStyleName("Optional");
		label9.setText("Fax:");
		grid1.add(label9);
		faxTextField = new TextField();
		faxTextField.setStyleName("Default");
		faxTextField.setInsets(new Insets(new Extent(2, Extent.PX)));
		grid1.add(faxTextField);
		Label label10 = new Label();
		label10.setStyleName("Default");
		grid1.add(label10);
		street2TextField = new TextField();
		street2TextField.setStyleName("Default");
		street2TextField.setInsets(new Insets(new Extent(2, Extent.PX)));
		grid1.add(street2TextField);
		Label label11 = new Label();
		label11.setStyleName("Default");
		label11.setText("1st E-Mail:");
		grid1.add(label11);
		primaryEmailTextField = new TextField();
		primaryEmailTextField.setStyleName("Default");
		primaryEmailTextField.setInsets(new Insets(new Extent(2, Extent.PX)));
		grid1.add(primaryEmailTextField);
		Label label12 = new Label();
		label12.setStyleName("Default");
		label12.setText("Postal Code:");
		grid1.add(label12);
		postalCodeTextField = new TextField();
		postalCodeTextField.setStyleName("Default");
		postalCodeTextField.setInsets(new Insets(new Extent(2, Extent.PX)));
		grid1.add(postalCodeTextField);
		Label label13 = new Label();
		label13.setStyleName("Optional");
		label13.setText("2nd E-Mail:");
		grid1.add(label13);
		secondaryEmailTextField = new TextField();
		secondaryEmailTextField.setStyleName("Default");
		secondaryEmailTextField.setInsets(new Insets(new Extent(2, Extent.PX)));
		grid1.add(secondaryEmailTextField);
		Label label14 = new Label();
		label14.setStyleName("Default");
		label14.setText("City:");
		grid1.add(label14);
		cityTextField = new TextField();
		cityTextField.setStyleName("Default");
		cityTextField.setInsets(new Insets(new Extent(2, Extent.PX)));
		grid1.add(cityTextField);
		Label label15 = new Label();
		label15.setStyleName("Optional");
		label15.setText("Pager:");
		grid1.add(label15);
		pagerTextField = new TextField();
		pagerTextField.setStyleName("Default");
		pagerTextField.setInsets(new Insets(new Extent(2, Extent.PX)));
		grid1.add(pagerTextField);
		Label label16 = new Label();
		label16.setStyleName("Optional");
		label16.setText("State/Province:");
		grid1.add(label16);
		stateTextField = new TextField();
		stateTextField.setStyleName("Default");
		stateTextField.setInsets(new Insets(new Extent(2, Extent.PX)));
		grid1.add(stateTextField);
		Label label17 = new Label();
		label17.setStyleName("Default");
		label17.setText("Country:");
		grid1.add(label17);
		countrySelectField = new SelectField();
		countrySelectField.setStyleName("Default");
		grid1.add(countrySelectField);
		acceptCheckBox = new CheckBox();
		acceptCheckBox.setStyleName("Default");
		acceptCheckBox.setText("I accept the Terms of Service.");
		acceptCheckBox.setInsets(new Insets(new Extent(0, Extent.PX),
				new Extent(5, Extent.PX)));
		column2.add(acceptCheckBox);
		mpayPanel = new MpayPanel();
		ColumnLayoutData mpayPanelLayoutData = new ColumnLayoutData();
		mpayPanelLayoutData.setInsets(new Insets(new Extent(0, Extent.PX),
				new Extent(10, Extent.PX), new Extent(0, Extent.PX),
				new Extent(0, Extent.PX)));
		mpayPanel.setLayoutData(mpayPanelLayoutData);
		column2.add(mpayPanel);
		externalCallReceiverRow = new Row();
		externalCallReceiverRow.setVisible(false);
		column2.add(externalCallReceiverRow);
		Row row5 = new Row();
		row5.setId("southComponent");
		row5.setCellSpacing(new Extent(10, Extent.PX));
		SplitPaneLayoutData row5LayoutData = new SplitPaneLayoutData();
		row5LayoutData.setMinimumSize(new Extent(50, Extent.PX));
		row5LayoutData.setMaximumSize(new Extent(50, Extent.PX));
		row5LayoutData.setInsets(new Insets(new Extent(0, Extent.PX),
				new Extent(10, Extent.PX), new Extent(0, Extent.PX),
				new Extent(0, Extent.PX)));
		row5.setLayoutData(row5LayoutData);
		splitPane2.add(row5);
		Label label18 = new Label();
		label18.setStyleName("Default");
		label18.setText("Choose a password:");
		row5.add(label18);
		passwordField = new PasswordField();
		passwordField.setStyleName("Default");
		passwordField.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onRegisterActionPerformed(e);
			}
		});
		row5.add(passwordField);
		registerButton = new Button();
		registerButton.setStyleName("Default");
		registerButton.setText("Register the name");
		registerButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onRegisterActionPerformed(e);
			}
		});
		row5.add(registerButton);
		Button button1 = new Button();
		button1.setStyleName("Default");
		button1.setText("Back");
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onBackActionPerformed(e);
			}
		});
		row5.add(button1);
	}
}
