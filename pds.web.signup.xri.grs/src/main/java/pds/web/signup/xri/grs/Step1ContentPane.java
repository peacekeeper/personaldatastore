package pds.web.signup.xri.grs;

import java.util.ResourceBundle;

import nextapp.echo.app.Button;
import nextapp.echo.app.Column;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.TextField;
import nextapp.echo.app.WindowPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.ColumnLayoutData;
import nextapp.echo.app.layout.RowLayoutData;
import nextapp.echo.app.layout.SplitPaneLayoutData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pds.store.xri.XriStore;
import pds.web.ui.MainWindow;
import echopoint.ImageIcon;

public class Step1ContentPane extends ContentPane {

	private static final long serialVersionUID = 2642429472693382313L;

	private static final Log log = LogFactory.getLog(Step1ContentPane.class);

	protected ResourceBundle resourceBundle;

	private GrsXriSignUpMethod grsXriSignUpMethod;

	private Label feedbackLabel;
	private TextField inameTextField;

	public Step1ContentPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void init() {

		super.init();
	}

	private void feedback(String string) {

		if (string != null) {

			this.feedbackLabel.setText(string);
			this.feedbackLabel.setVisible(true);
		} else {

			this.feedbackLabel.setVisible(false);
		}
	}

	public void setGrsXriSignUpMethod(GrsXriSignUpMethod grsXriSignUpMethod) {

		this.grsXriSignUpMethod = grsXriSignUpMethod;
	}

	private void onRegisterTopLevelActionPerformed(ActionEvent e) {

		XriStore xriStore = this.grsXriSignUpMethod.getXriStore();

		String iname = this.inameTextField.getText();

		try {

			if (xriStore.existsXri(null, iname)) {

				this.feedback("I-Name is not available.");
				return;
			}
		} catch (Exception ex) {

			log.warn(ex.getMessage(), ex);
			this.feedback(ex.getMessage());
			return;
		}

		// go to Step2ContentPane

		Step2ContentPane step2ContentPane = new Step2ContentPane();
		step2ContentPane.setGrsXriSignUpMethod(this.grsXriSignUpMethod);
		step2ContentPane.setIname(iname);

		WindowPane windowPane = (WindowPane) MainWindow.findParentComponentByClass(this, WindowPane.class);
		windowPane.remove(this);
		windowPane.setWidth(new Extent(800, Extent.PX));
		windowPane.setHeight(new Extent(Integer.MAX_VALUE));
		windowPane.add(step2ContentPane);
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
		ContentPane contentPane2 = new ContentPane();
		splitPane1.add(contentPane2);
		Column column2 = new Column();
		column2.setInsets(new Insets(new Extent(10, Extent.PX)));
		column2.setCellSpacing(new Extent(10, Extent.PX));
		contentPane2.add(column2);
		Label label1 = new Label();
		label1.setStyleName("Header");
		label1.setText("Check for availability:");
		column2.add(label1);
		Row row1 = new Row();
		row1.setCellSpacing(new Extent(10, Extent.PX));
		column2.add(row1);
		inameTextField = new TextField();
		inameTextField.setStyleName("Default");
		inameTextField.setText("=yourname");
		inameTextField.setInsets(new Insets(new Extent(5, Extent.PX)));
		inameTextField.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onRegisterTopLevelActionPerformed(e);
			}
		});
		row1.add(inameTextField);
		Button button1 = new Button();
		button1.setStyleName("Default");
		button1.setText("Go");
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onRegisterTopLevelActionPerformed(e);
			}
		});
		row1.add(button1);
		ImageIcon imageIcon1 = new ImageIcon();
		ResourceImageReference imageReference1 = new ResourceImageReference(
				"/pds/web/signup/xri/grs/logo-inames.png");
		imageIcon1.setIcon(imageReference1);
		RowLayoutData imageIcon1LayoutData = new RowLayoutData();
		imageIcon1LayoutData.setInsets(new Insets(new Extent(20, Extent.PX),
				new Extent(0, Extent.PX), new Extent(0, Extent.PX), new Extent(
						0, Extent.PX)));
		imageIcon1.setLayoutData(imageIcon1LayoutData);
		row1.add(imageIcon1);
		Label label2 = new Label();
		label2.setStyleName("Default");
		label2.setText("An I-Name is a single, unified identifier on the Internet that can point to different targets and provide multiple services at the same time. It acts as the primary identifier for your Personal Data Store.");
		column2.add(label2);
		Label label4 = new Label();
		label4.setStyleName("Default");
		label4.setText("Every I-Name comes with a corresponding I-Number, which is like a Swiss bank account name for your online identity.");
		column2.add(label4);
		Grid grid1 = new Grid();
		grid1.setColumnWidth(0, new Extent(100, Extent.PX));
		column2.add(grid1);
		Label label3 = new Label();
		label3.setStyleName("Default");
		label3.setText("Examples:");
		grid1.add(label3);
		Label label5 = new Label();
		label5.setStyleName("Default");
		label5.setText("=mary (I-Name)");
		grid1.add(label5);
		Label label6 = new Label();
		label6.setStyleName("Default");
		grid1.add(label6);
		Label label7 = new Label();
		label7.setStyleName("Default");
		label7.setText("=john.smith (I-Name)");
		grid1.add(label7);
		Label label10 = new Label();
		label10.setStyleName("Default");
		grid1.add(label10);
		Label label11 = new Label();
		label11.setStyleName("Default");
		label11.setText("@mycompany (I-Name)");
		grid1.add(label11);
		Label label8 = new Label();
		label8.setStyleName("Default");
		grid1.add(label8);
		Label label9 = new Label();
		label9.setStyleName("Default");
		label9.setText("=!D18C.522F.0B25.1EF9 (I-Number)");
		grid1.add(label9);
	}
}
