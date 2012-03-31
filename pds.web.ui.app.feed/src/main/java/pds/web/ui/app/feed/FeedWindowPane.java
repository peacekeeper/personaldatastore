package pds.web.ui.app.feed;

import java.util.ResourceBundle;

import nextapp.echo.app.Extent;
import nextapp.echo.app.WindowPane;

import org.eclipse.higgins.XDI2.xri3.impl.XRI3Segment;

import pds.xdi.XdiContext;
import pds.web.ui.app.feed.FeedContentPane;

public class FeedWindowPane extends WindowPane {

	private static final long serialVersionUID = 4111493581013444404L;

	protected ResourceBundle resourceBundle;

	private FeedContentPane feedContentPane;

	/**
	 * Creates a new <code>ConfigureAPIsWindowPane</code>.
	 */
	public FeedWindowPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	public void setFeedPdsWebApp(FeedPdsWebApp feedPdsWebApp) {

		this.feedContentPane.setFeedPdsWebApp(feedPdsWebApp);
	}

	public void setContextAndSubjectXri(XdiContext context, XRI3Segment subjectXri) {

		this.feedContentPane.setContextAndSubjectXri(context, subjectXri);
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setStyleName("Blue");
		this.setTitle("Federated Social Web");
		this.setHeight(new Extent(768, Extent.PX));
		this.setMinimizeEnabled(false);
		this.setMaximizeEnabled(true);
		this.setClosable(true);
		this.setDefaultCloseOperation(WindowPane.DISPOSE_ON_CLOSE);
		this.setWidth(new Extent(1024, Extent.PX));
		feedContentPane = new FeedContentPane();
		add(feedContentPane);
	}
}
