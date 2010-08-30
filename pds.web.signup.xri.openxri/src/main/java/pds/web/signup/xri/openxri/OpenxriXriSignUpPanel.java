package pds.web.signup.xri.openxri;

import java.util.ResourceBundle;

import nextapp.echo.app.Button;
import nextapp.echo.app.Panel;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import pds.web.ui.MainWindow;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Row;
import nextapp.echo.app.Label;
import nextapp.echo.app.ResourceImageReference;

public class OpenxriXriSignUpPanel extends Panel {

	private static final long serialVersionUID = 877610718171778041L;

	protected ResourceBundle resourceBundle;

	private OpenxriXriSignUpMethod openxriXriSignUpMethod;

	public OpenxriXriSignUpPanel() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void init() {

		super.init();
	}

	public void setOpenxriXriSignUpMethod(OpenxriXriSignUpMethod openxriXriSignUpMethod) {

		this.openxriXriSignUpMethod = openxriXriSignUpMethod;
	}

	private void onActionPerformed(ActionEvent e) {

		OpenxriXriSignUpWindowPane openxriXriSignUpWindowPane = new OpenxriXriSignUpWindowPane();
		openxriXriSignUpWindowPane.setOpenxriXriSignUpMethod(this.openxriXriSignUpMethod);

		MainWindow.findMainContentPane(this).add(openxriXriSignUpWindowPane);
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setInsets(new Insets(new Extent(0, Extent.PX), new Extent(10,
				Extent.PX), new Extent(0, Extent.PX), new Extent(0, Extent.PX)));
		Row row1 = new Row();
		row1.setCellSpacing(new Extent(10, Extent.PX));
		add(row1);
		Button button1 = new Button();
		button1.setStyleName("Plain");
		ResourceImageReference imageReference1 = new ResourceImageReference(
				"/pds/web/resource/image/star.png");
		button1.setIcon(imageReference1);
		button1.setText("Get a free I-Name");
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onActionPerformed(e);
			}
		});
		row1.add(button1);
		Label label1 = new Label();
		label1.setStyleName("Default");
		label1.setText("Example: =web*yourname");
		row1.add(label1);
	}
}
