package pds.web.ui.html;

import java.util.ResourceBundle;

import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;

import pds.web.ui.MainWindow;

import echopoint.HttpPane;

public class HtmlContentPane extends ContentPane {

	private static final long serialVersionUID = -9213300629566793886L;

	public static final String IFRAME_HTML_START = "<iframe style=\"border: 0; padding:0; margin:0\" marginwidth=\"0\" marginheight=\"0\" scrolling=\"no\" frameborder=\"0\" border=\"0\" width=\"100%\" height=\"100%\" style=\"width: 100%; height: 100%;\" src=\"";
	public static final String IFRAME_HTML_END = "\"></iframe>";

	protected ResourceBundle resourceBundle;

	/**
	 * Creates a new <code>HtmlContentPane</code>.
	 */
	public HtmlContentPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	public void setSrc(String src) {

		HttpPane httpPane = (HttpPane) MainWindow.findChildComponentById(this, "httpPane");

		httpPane.setUri(src);
	}

	public String getSrc() {

		HttpPane httpPane = (HttpPane) MainWindow.findChildComponentById(this, "httpPane");

		return httpPane.getUri();
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		HttpPane httpPane1 = new HttpPane();
		httpPane1.setId("httpPane");
		httpPane1.setHeight(new Extent(100, Extent.PERCENT));
		httpPane1.setWidth(new Extent(100, Extent.PERCENT));
		add(httpPane1);
	}
}
