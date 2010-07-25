package pds.web.ui.html;

import java.util.ResourceBundle;

import nextapp.echo.app.WindowPane;

import pds.web.ui.MainWindow;
import pds.web.ui.html.HtmlContentPane;
import nextapp.echo.app.Extent;

public class HtmlWindowPane extends WindowPane {

	private static final long serialVersionUID = -3963961923407555912L;

	protected ResourceBundle resourceBundle;

	/**
	 * Creates a new <code>HtmlWindowPane</code>.
	 */
	public HtmlWindowPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	public void setSrc(String src) {

		HtmlContentPane htmlContentPane = (HtmlContentPane) MainWindow.findChildComponentByClass(this, HtmlContentPane.class);
		htmlContentPane.setSrc(src);
	}

	public String getSrc() {

		HtmlContentPane htmlContentPane = (HtmlContentPane) MainWindow.findChildComponentByClass(this, HtmlContentPane.class);
		return htmlContentPane.getSrc();
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setStyleName("Gray");
		this.setHeight(new Extent(600, Extent.PX));
		this.setMinimizeEnabled(false);
		this.setMaximizeEnabled(true);
		this.setClosable(true);
		this.setWidth(new Extent(800, Extent.PX));
		HtmlContentPane htmlContentPane1 = new HtmlContentPane();
		add(htmlContentPane1);
	}
}
