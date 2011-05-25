package pds.web.signin.predefined;


import java.util.Map;
import java.util.ResourceBundle;

import nextapp.echo.app.Button;
import nextapp.echo.app.Column;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.Panel;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import pds.web.PDSApplication;
import pds.web.ui.MessageDialog;
import pds.xdi.Xdi;
import pds.xdi.XdiContext;

public class PredefinedSignInPanel extends Panel {

	private static final long serialVersionUID = 46284183174314347L;

	protected ResourceBundle resourceBundle;

	private PredefinedSignInMethod predefinedSignInMethod;

	private Column contextsColumn;

	/**
	 * Creates a new <code>PredefinedSignInPanel</code>.
	 */
	public PredefinedSignInPanel() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void init() {

		super.init();

		// contexts

		this.contextsColumn.removeAll();
		for (Map.Entry<String, String> entry : this.predefinedSignInMethod.getContexts().entrySet()) {

			String identifier = entry.getKey();
			String endpoint = entry.getValue();

			Button contextButton = new Button();
			contextButton.setStyleName("Default");
			contextButton.setText("Sign in as " + identifier);
			contextButton.set("identifier", identifier);
			contextButton.set("endpoint", endpoint);
			contextButton.addActionListener(new ActionListener() {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					onOpenActionPerformed(e);
				}
			});

			this.contextsColumn.add(contextButton);
		}
	}

	public void setPredefinedSignInMethod(PredefinedSignInMethod predefinedSignInMethod) {

		this.predefinedSignInMethod = predefinedSignInMethod;
	}

	private void onOpenActionPerformed(ActionEvent e) {

		Button contextButton = (Button) e.getSource();
		
		Xdi xdi = PDSApplication.getApp().getXdi();

		String endpoint = (String) contextButton.get("endpoint");
		if (endpoint == null || endpoint.trim().equals("")) return;
		if (! endpoint.endsWith("/")) endpoint += "/";
		if ((! endpoint.toLowerCase().startsWith("http://")) && (! endpoint.toLowerCase().startsWith("https://"))) endpoint = "http://" + endpoint;

		// try to open the context

		try {

			XdiContext context = xdi.resolveContextByEndpoint(endpoint, null);

			PDSApplication.getApp().openContext(context);
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
		this.setInsets(new Insets(new Extent(10, Extent.PX)));
		Column column2 = new Column();
		column2.setCellSpacing(new Extent(10, Extent.PX));
		add(column2);
		Label label2 = new Label();
		label2.setStyleName("Header");
		label2.setText("Predefined Sign-In");
		column2.add(label2);
		Label label4 = new Label();
		label4.setStyleName("Default");
		label4.setText("Welcome. This is a \"manual\" way of opening a Personal Data Store by selecting a predefined user.");
		column2.add(label4);
		contextsColumn = new Column();
		column2.add(contextsColumn);
	}
}
