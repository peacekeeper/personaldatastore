package pds.web.signup.xri.openxri;

import java.util.ResourceBundle;

import nextapp.echo.app.Extent;
import nextapp.echo.app.WindowPane;
import pds.web.signup.xri.openxri.Step1ContentPane;

public class OpenxriXriSignUpWindowPane extends WindowPane {

	private static final long serialVersionUID = -6779255891699784581L;

	protected ResourceBundle resourceBundle;

	private Step1ContentPane step1ContentPane;

	/**
	 * Creates a new <code>AccountWindowPane</code>.
	 */
	public OpenxriXriSignUpWindowPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void init() {

	}

	public void setOpenxriXriSignUpMethod(OpenxriXriSignUpMethod openxriXriSignUpMethod) {

		this.step1ContentPane.setOpenxriXriSignUpMethod(openxriXriSignUpMethod);
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setStyleName("Gray");
		this.setTitle("Get a free I-Name");
		this.setHeight(new Extent(400, Extent.PX));
		this.setMaximizeEnabled(true);
		this.setMinimizeEnabled(false);
		this.setClosable(true);
		this.setWidth(new Extent(600, Extent.PX));
		step1ContentPane = new Step1ContentPane();
		add(step1ContentPane);
	}
}