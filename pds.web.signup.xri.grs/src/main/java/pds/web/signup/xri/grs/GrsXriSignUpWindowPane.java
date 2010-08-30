package pds.web.signup.xri.grs;

import java.util.ResourceBundle;

import nextapp.echo.app.Extent;
import nextapp.echo.app.WindowPane;
import pds.web.signup.xri.grs.Step1ContentPane;

public class GrsXriSignUpWindowPane extends WindowPane {

	private static final long serialVersionUID = -4095355604112229565L;

	protected ResourceBundle resourceBundle;

	private Step1ContentPane step1ContentPane;

	/**
	 * Creates a new <code>AccountWindowPane</code>.
	 */
	public GrsXriSignUpWindowPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void init() {

	}

	public void setGrsXriSignUpMethod(GrsXriSignUpMethod grsXriSignUpMethod) {
		
		this.step1ContentPane.setGrsXriSignUpMethod(grsXriSignUpMethod);
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setStyleName("Gray");
		this.setTitle("Buy an I-Name");
		this.setHeight(new Extent(400, Extent.PX));
		this.setMaximizeEnabled(true);
		this.setMinimizeEnabled(false);
		this.setClosable(true);
		this.setWidth(new Extent(600, Extent.PX));
		step1ContentPane = new Step1ContentPane();
		add(step1ContentPane);
	}
}