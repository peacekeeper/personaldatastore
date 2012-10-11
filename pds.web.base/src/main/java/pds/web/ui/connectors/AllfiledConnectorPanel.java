package pds.web.ui.connectors;

import java.util.ResourceBundle;
import nextapp.echo.app.Panel;
import pds.web.PDSApplication;
import nextapp.echo.app.Button;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

public class AllfiledConnectorPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected ResourceBundle resourceBundle;

	/**
	 * Creates a new <code>AllfiledConnectorPanel</code>.
	 */
	public AllfiledConnectorPanel() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	/**
	 * Returns the user's application instance, cast to its specific type.
	 *
	 * @return The user's application instance.
	 */
	protected PDSApplication getApplication() {
		return (PDSApplication) getApplicationInstance();
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		Button button1 = new Button();
		button1.setStyleName("PlainWhite");
		ResourceImageReference imageReference1 = new ResourceImageReference(
				"/pds/web/resource/image/connect-allfiled.png");
		button1.setIcon(imageReference1);
		button1.setText("Connect to Allfiled");
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onConnectAllfiledActionPerformed(e);
			}
		});
		add(button1);
	}

	private void onConnectAllfiledActionPerformed(ActionEvent e) {
		//TODO Implement.
	}
}
