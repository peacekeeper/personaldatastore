package pds.web.ui.html;

import java.util.ResourceBundle;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.Button;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Row;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.WindowPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.SplitPaneLayoutData;
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

	private void onCloseActionPerformed(ActionEvent e) {
		
		WindowPane windowPane = (WindowPane) MainWindow.findParentComponentByClass(this, WindowPane.class);
		windowPane.getParent().remove(windowPane);
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		SplitPane splitPane1 = new SplitPane();
		splitPane1.setStyleName("Default");
		splitPane1.setOrientation(SplitPane.ORIENTATION_VERTICAL_TOP_BOTTOM);
		splitPane1.setSeparatorVisible(false);
		add(splitPane1);
		HttpPane httpPane1 = new HttpPane();
		httpPane1.setId("httpPane");
		httpPane1.setHeight(new Extent(100, Extent.PERCENT));
		httpPane1.setWidth(new Extent(100, Extent.PERCENT));
		splitPane1.add(httpPane1);
		Row row1 = new Row();
		row1.setAlignment(new Alignment(Alignment.RIGHT, Alignment.DEFAULT));
		row1.setInsets(new Insets(new Extent(10, Extent.PX)));
		SplitPaneLayoutData row1LayoutData = new SplitPaneLayoutData();
		row1LayoutData.setMinimumSize(new Extent(60, Extent.PX));
		row1LayoutData.setMaximumSize(new Extent(60, Extent.PX));
		row1.setLayoutData(row1LayoutData);
		splitPane1.add(row1);
		Button button1 = new Button();
		button1.setStyleName("Default");
		button1.setText("Close Window");
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onCloseActionPerformed(e);
			}
		});
		row1.add(button1);
	}
}
