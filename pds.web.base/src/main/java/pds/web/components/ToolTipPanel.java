package pds.web.components;

import java.util.ResourceBundle;

import pds.web.PDSApplication;
import echopoint.jquery.TooltipContainer;
import echopoint.ImageIcon;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Panel;
import nextapp.echo.app.Label;

public class ToolTipPanel extends TooltipContainer {

	private static final long serialVersionUID = 1L;

	protected ResourceBundle resourceBundle;

	/**
	 * Creates a new <code>TooltipPanel</code>.
	 */
	public ToolTipPanel() {
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
		ImageIcon imageIcon7 = new ImageIcon();
		ResourceImageReference imageReference1 = new ResourceImageReference(
				"/pds/web/resource/image/tooltip.png");
		imageIcon7.setIcon(imageReference1);
		imageIcon7.setHeight(new Extent(24, Extent.PX));
		imageIcon7.setWidth(new Extent(24, Extent.PX));
		add(imageIcon7);
		Panel panel9 = new Panel();
		panel9.setStyleName("Tooltip");
		add(panel9);
		Label label1 = new Label();
		label1.setStyleName("Default");
		label1.setText("TOOLTIP HERE");
		panel9.add(label1);
	}
}
