package pds.web.ui.accountpersona;

import java.util.ResourceBundle;

import nextapp.echo.app.Extent;
import nextapp.echo.app.WindowPane;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.xdi.XdiContext;

public class AccountPersonaWindowPane extends WindowPane {

	private static final long serialVersionUID = 4136493581013444404L;

	protected ResourceBundle resourceBundle;

	private AccountPersonaContentPane accountPersonaContentPane;

	/**
	 * Creates a new <code>ConfigureAPIsWindowPane</code>.
	 */
	public AccountPersonaWindowPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	public void setContextAndSubjectXri(XdiContext context, XRI3Segment subjectXri) {

		this.accountPersonaContentPane.setContextAndSubjectXri(context, subjectXri);
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setStyleName("Red");
		this.setTitle("Account Persona");
		this.setHeight(new Extent(600, Extent.PX));
		this.setMinimizeEnabled(false);
		this.setMaximizeEnabled(true);
		this.setClosable(true);
		this.setWidth(new Extent(800, Extent.PX));
		accountPersonaContentPane = new AccountPersonaContentPane();
		add(accountPersonaContentPane);
	}
}
