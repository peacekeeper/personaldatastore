package pds.web.ui.context;


import java.util.ResourceBundle;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.Button;
import nextapp.echo.app.Column;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.PasswordField;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.TextField;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import pds.web.PDSApplication;
import pds.web.ui.MessageDialog;
import echopoint.ImageIcon;
import nextapp.echo.app.layout.SplitPaneLayoutData;
import nextapp.echo.app.FillImage;

public class ClosedContentPane extends ContentPane {

	private static final long serialVersionUID = 46284183174314347L;

	protected ResourceBundle resourceBundle;

	private TextField inameTextField;
	private PasswordField passwordField;

	/**
	 * Creates a new <code>LoginContentPane</code>.
	 */
	public ClosedContentPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void init() {

		super.init();
	}

	private void onOpenActionPerformed(ActionEvent e) {

		String iname = this.inameTextField.getText();
		if (iname == null || iname.trim().equals("")) return;

		String password = this.passwordField.getText();
		if (password == null || password.trim().equals("")) return;

		// try to open the context

		try {

			PDSApplication.getApp().openContext(iname, password);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, we could not open your Personal Data Store: " + ex.getMessage(), ex);
			return;
		}
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setOverflow(1);
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
		Column column2 = new Column();
		column2.setCellSpacing(new Extent(10, Extent.PX));
		splitPane1.add(column2);
		Label label4 = new Label();
		label4.setStyleName("ParagraphHeader1");
		label4.setText("Enter your I-Name / Password:");
		column2.add(label4);
		Grid grid2 = new Grid();
		grid2.setWidth(new Extent(100, Extent.PERCENT));
		grid2.setInsets(new Insets(new Extent(5, Extent.PX)));
		column2.add(grid2);
		Label label1 = new Label();
		label1.setStyleName("Default");
		label1.setText("I-Name:");
		grid2.add(label1);
		inameTextField = new TextField();
		inameTextField.setStyleName("Default");
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
		splitPane1.add(row2);
		ImageIcon imageIcon2 = new ImageIcon();
		ResourceImageReference imageReference2 = new ResourceImageReference(
				"/pds/web/resource/image/lock.png");
		imageIcon2.setIcon(imageReference2);
		row2.add(imageIcon2);
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
	}
}
