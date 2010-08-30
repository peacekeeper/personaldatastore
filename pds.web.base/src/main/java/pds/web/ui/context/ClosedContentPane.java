package pds.web.ui.context;


import java.util.List;
import java.util.ResourceBundle;

import nextapp.echo.app.Column;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Insets;
import pds.web.PDSApplication;

public class ClosedContentPane extends ContentPane {

	private static final long serialVersionUID = 46284183174314347L;

	protected ResourceBundle resourceBundle;

	private Column signInContentPaneColumn;

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

		// SignInMethods

		List<SignInMethod> signInFactories = PDSApplication.getApp().getServlet().getSignInMethods();

		this.signInContentPaneColumn.removeAll();
		for (SignInMethod signInFactory : signInFactories) {

			this.signInContentPaneColumn.add(signInFactory.newPanel());
		}
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setInsets(new Insets(new Extent(10, Extent.PX)));
		signInContentPaneColumn = new Column();
		add(signInContentPaneColumn);
	}
}
