package pds.web.signup.xri.grs.components.mpay;

import java.util.ResourceBundle;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.Border;
import nextapp.echo.app.Color;
import nextapp.echo.app.Column;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.Panel;
import nextapp.echo.app.RadioButton;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.SelectField;
import nextapp.echo.app.TextField;
import nextapp.echo.app.button.ButtonGroup;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import com.fullxri.mpay4java.MpayTools;

import echopoint.ImageIcon;

public class MpayPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected ResourceBundle resourceBundle;

	private RadioButton ptypepaypalRadioButton;
	private RadioButton ptypeccRadioButton;
	private SelectField brandSelectField;
	private SelectField expirymonthSelectField;
	private SelectField expiryyearSelectField;
	private TextField identifierTextField;
	private TextField cvcTextField;
	private Panel ptypeccPanel;
	private Panel ptypepaypalPanel;
	/**
	 * Creates a new <code>MpayPanel</code>.
	 */
	public MpayPanel() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void init() {

		super.init();

		this.brandSelectField.setModel(new BrandsModel());
		this.expirymonthSelectField.setModel(new MonthsModel());
		this.expiryyearSelectField.setModel(new YearsModel());
	}

	public String getMpaycvc() {

		if (this.ptypeccRadioButton.isSelected())
			return this.cvcTextField.getText();

		return null;
	}

	public String getMpayidentifier() {

		if (this.ptypeccRadioButton.isSelected())
			return this.identifierTextField.getText();

		return null;
	}

	public String getMpayexpirymonth() {

		if (this.ptypeccRadioButton.isSelected())
			return (String) this.expirymonthSelectField.getSelectedItem();

		return null;
	}

	public String getMpayexpiryyear() {

		if (this.ptypeccRadioButton.isSelected())
			return YearsModel.yearToMpayexpiryyear((String) this.expiryyearSelectField.getSelectedItem());

		return null;
	}

	public String getMpaybrand() {

		if (this.ptypeccRadioButton.isSelected())
			return (String) this.brandSelectField.getSelectedItem();

		return null;
	}

	public String getMpayptype() {

		if (this.ptypeccRadioButton.isSelected()) 
			return MpayTools.PTYPE_CC;
		else if (this.ptypepaypalRadioButton.isSelected()) 
			return MpayTools.PTYPE_PAYPAL;

		return null;
	}

	private void doPanelVisibility() {

		this.ptypeccPanel.setVisible(this.ptypeccRadioButton.isSelected());
		this.ptypepaypalPanel.setVisible(this.ptypepaypalRadioButton.isSelected());
	}

	private void onPtypeccActionPerformed(ActionEvent e) {

		this.doPanelVisibility();
	}

	private void onPtypepaypalActionPerformed(ActionEvent e) {

		this.doPanelVisibility();
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setInsets(new Insets(new Extent(5, Extent.PX)));
		this.setBorder(new Border(new Extent(3, Extent.PX),
				new Color(0x808080), Border.STYLE_OUTSET));
		Column column1 = new Column();
		column1.setCellSpacing(new Extent(5, Extent.PX));
		add(column1);
		Row row1 = new Row();
		row1.setAlignment(new Alignment(Alignment.DEFAULT, Alignment.CENTER));
		row1.setCellSpacing(new Extent(10, Extent.PX));
		column1.add(row1);
		ptypeccRadioButton = new RadioButton();
		ptypeccRadioButton.setStyleName("Default");
		ptypeccRadioButton.setSelected(true);
		ptypeccRadioButton.setText("Credit Card");
		ButtonGroup ptypeGroup = new ButtonGroup();
		ptypeccRadioButton.setGroup(ptypeGroup);
		ptypeccRadioButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onPtypeccActionPerformed(e);
			}
		});
		row1.add(ptypeccRadioButton);
		ptypepaypalRadioButton = new RadioButton();
		ptypepaypalRadioButton.setStyleName("Default");
		ptypepaypalRadioButton.setText("PayPal");
		ptypepaypalRadioButton.setGroup(ptypeGroup);
		ptypepaypalRadioButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onPtypepaypalActionPerformed(e);
			}
		});
		row1.add(ptypepaypalRadioButton);
		Panel panel3 = new Panel();
		panel3.setVisible(true);
		panel3.setInsets(new Insets(new Extent(10, Extent.PX), new Extent(0,
				Extent.PX), new Extent(0, Extent.PX), new Extent(0, Extent.PX)));
		row1.add(panel3);
		Row row3 = new Row();
		row3.setCellSpacing(new Extent(5, Extent.PX));
		panel3.add(row3);
		ImageIcon imageIcon1 = new ImageIcon();
		ResourceImageReference imageReference1 = new ResourceImageReference(
				"/pds/web/signup/xri/grs/components/mpay/mpay1.gif");
		imageIcon1.setIcon(imageReference1);
		row3.add(imageIcon1);
		ImageIcon imageIcon2 = new ImageIcon();
		ResourceImageReference imageReference2 = new ResourceImageReference(
				"/pds/web/signup/xri/grs/components/mpay/mpay2.gif");
		imageIcon2.setIcon(imageReference2);
		row3.add(imageIcon2);
		ImageIcon imageIcon3 = new ImageIcon();
		ResourceImageReference imageReference3 = new ResourceImageReference(
				"/pds/web/signup/xri/grs/components/mpay/mpay3.gif");
		imageIcon3.setIcon(imageReference3);
		row3.add(imageIcon3);
		ImageIcon imageIcon4 = new ImageIcon();
		ResourceImageReference imageReference4 = new ResourceImageReference(
				"/pds/web/signup/xri/grs/components/mpay/mpay4.gif");
		imageIcon4.setIcon(imageReference4);
		row3.add(imageIcon4);
		ImageIcon imageIcon5 = new ImageIcon();
		ResourceImageReference imageReference5 = new ResourceImageReference(
				"/pds/web/signup/xri/grs/components/mpay/mpay5.gif");
		imageIcon5.setIcon(imageReference5);
		row3.add(imageIcon5);
		ImageIcon imageIcon6 = new ImageIcon();
		ResourceImageReference imageReference6 = new ResourceImageReference(
				"/pds/web/signup/xri/grs/components/mpay/mpay6.gif");
		imageIcon6.setIcon(imageReference6);
		row3.add(imageIcon6);
		ImageIcon imageIcon7 = new ImageIcon();
		ResourceImageReference imageReference7 = new ResourceImageReference(
				"/pds/web/signup/xri/grs/components/mpay/mpay7.gif");
		imageIcon7.setIcon(imageReference7);
		row3.add(imageIcon7);
		ptypeccPanel = new Panel();
		column1.add(ptypeccPanel);
		Grid grid1 = new Grid();
		grid1.setOrientation(Grid.ORIENTATION_HORIZONTAL);
		grid1.setInsets(new Insets(new Extent(5, Extent.PX)));
		grid1.setSize(4);
		ptypeccPanel.add(grid1);
		Label label1 = new Label();
		label1.setStyleName("Default");
		label1.setText("Brand:");
		grid1.add(label1);
		brandSelectField = new SelectField();
		brandSelectField.setStyleName("Default");
		grid1.add(brandSelectField);
		Label label2 = new Label();
		label2.setStyleName("Default");
		label2.setText("Expiration:");
		grid1.add(label2);
		Panel panel2 = new Panel();
		grid1.add(panel2);
		Row row2 = new Row();
		row2.setCellSpacing(new Extent(5, Extent.PX));
		panel2.add(row2);
		expirymonthSelectField = new SelectField();
		expirymonthSelectField.setId("monthSelectField");
		expirymonthSelectField.setStyleName("Default");
		row2.add(expirymonthSelectField);
		Label label3 = new Label();
		label3.setStyleName("Default");
		label3.setText("/");
		row2.add(label3);
		expiryyearSelectField = new SelectField();
		expiryyearSelectField.setStyleName("Default");
		row2.add(expiryyearSelectField);
		Label label4 = new Label();
		label4.setStyleName("Default");
		label4.setText("Credit Card #:");
		grid1.add(label4);
		identifierTextField = new TextField();
		identifierTextField.setStyleName("Default");
		grid1.add(identifierTextField);
		Label label5 = new Label();
		label5.setStyleName("Default");
		label5.setText("CVC Code:");
		grid1.add(label5);
		cvcTextField = new TextField();
		cvcTextField.setStyleName("Default");
		cvcTextField.setWidth(new Extent(80, Extent.PX));
		grid1.add(cvcTextField);
		ptypepaypalPanel = new Panel();
		ptypepaypalPanel.setVisible(false);
		ptypepaypalPanel.setInsets(new Insets(new Extent(5, Extent.PX)));
		column1.add(ptypepaypalPanel);
		Label label6 = new Label();
		label6.setStyleName("Default");
		label6.setText("In the next step, you will be asked to authorize a PayPal transaction.");
		ptypepaypalPanel.add(label6);
	}
}
