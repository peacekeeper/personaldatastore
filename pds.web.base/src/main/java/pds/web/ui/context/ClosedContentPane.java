package pds.web.ui.context;


import java.util.List;
import java.util.ResourceBundle;

import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Panel;
import nextapp.echo.extras.app.TabPane;
import nextapp.echo.extras.app.layout.TabPaneLayoutData;
import pds.web.PDSApplication;

public class ClosedContentPane extends ContentPane {

	private static final long serialVersionUID = 46284183174314347L;

	protected ResourceBundle resourceBundle;

	private TabPane signInPanelTabPane;

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

		List<SignInMethod> signInMethods = PDSApplication.getApp().getServlet().getSignInMethods();

		if (signInMethods.size() < 1) {

			this.remove(this.signInPanelTabPane);
		} else if (signInMethods.size() < 2) {

			SignInMethod signInMethod = signInMethods.get(0);

			Panel signInPanel = signInMethod.newPanel();

			this.remove(this.signInPanelTabPane);
			this.add(signInPanel);
		} else {

			this.signInPanelTabPane.removeAll();
			for (SignInMethod signInMethod : signInMethods) {

				Panel signInPanel = signInMethod.newPanel();

				TabPaneLayoutData tabPaneLayoutData = new TabPaneLayoutData();
				tabPaneLayoutData.setTitle(signInMethod.getMethodName());
				signInPanel.setLayoutData(tabPaneLayoutData);

				this.signInPanelTabPane.add(signInPanel);
			}
		}
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setInsets(new Insets(new Extent(10, Extent.PX)));
		signInPanelTabPane = new TabPane();
		signInPanelTabPane.setStyleName("Default");
		add(signInPanelTabPane);
	}
}
