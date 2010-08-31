package pds.web.ui;



import java.util.EventObject;
import java.util.ResourceBundle;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.Button;
import nextapp.echo.app.CheckBox;
import nextapp.echo.app.Column;
import nextapp.echo.app.Component;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.FillImage;
import nextapp.echo.app.Grid;
import nextapp.echo.app.IllegalChildException;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.WindowPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.RowLayoutData;
import nextapp.echo.app.layout.SplitPaneLayoutData;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.PDSApplication;
import pds.web.events.ApplicationContextClosedEvent;
import pds.web.events.ApplicationContextOpenedEvent;
import pds.web.events.ApplicationEvent;
import pds.web.events.ApplicationListener;
import pds.web.ui.accountroot.AccountRootWindowPane;
import pds.web.ui.app.PdsWebApp;
import pds.web.ui.context.ContextWindowPane;
import pds.web.ui.log.LogWindowPane;
import pds.web.xdi.XdiContext;
import echopoint.ImageIcon;

public class MainContentPane extends ContentPane implements ApplicationListener {

	private static final long serialVersionUID = 3164240822381021756L;

	protected ResourceBundle resourceBundle;

	private XdiContext context;

	private Column pdsColumn;
	private AccountRootGrid accountRootGrid;
	private CheckBox logWindowCheckBox;
	private CheckBox developerModeCheckBox;
	private Grid pdsWebAppGrid;

	/**
	 * Creates a new <code>MainContentPane</code>.
	 */
	public MainContentPane() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void init() {

		super.init();

		// Add PdsWebApps

		for (final PdsWebApp pdsWebApp : PDSApplication.getApp().getServlet().getPdsWebApps()) {

			Button pdsWebAppButton = new Button();
			pdsWebAppButton.setStyleName("PlainWhite");
			ResourceImageReference imageReference = pdsWebApp.getResourceImageReference();
			pdsWebAppButton.setIcon(imageReference);
			pdsWebAppButton.setText(pdsWebApp.getName());
			pdsWebAppButton.setInsets(new Insets(new Extent(10, Extent.PX)));

			pdsWebAppButton.addActionListener(new ActionListener() {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					XRI3Segment subjectXri = new XRI3Segment(MainContentPane.this.getContext().getCanonical());
					WindowPane windowPane = pdsWebApp.newWindowPane(MainContentPane.this.getContext(), subjectXri);
					MainContentPane.this.add(windowPane);
				}
			});
			MainContentPane.this.pdsWebAppGrid.add(pdsWebAppButton);
		}

		// add us as listener

		PDSApplication.getApp().addApplicationListener(this);
	}

	@Override
	public void dispose() {

		super.dispose();

		// remove us as listener

		PDSApplication.getApp().removeApplicationListener(this);
	}

	@Override
	public void add(Component component, int n) throws IllegalChildException {

		super.add(component, n);

		if (component instanceof MessageDialog) {

			((MessageDialog) component).setZIndex(Integer.MAX_VALUE);
		} if (component instanceof WindowPane) {

			((WindowPane) component).setZIndex(Integer.MAX_VALUE - 1);
		}
	}

	private void refresh(EventObject event) {

		try {

			XRI3Segment subjectXri = new XRI3Segment(this.context.getCanonical());
			this.accountRootGrid.setContextAndSubjectXri(this.context, subjectXri);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your personal data: " + ex.getMessage(), ex);
			return;
		}
	}

	public XdiContext getContext() {

		return this.context;
	}

	public boolean isDeveloperModeSelected() {

		return this.developerModeCheckBox.isSelected();
	}

	public void onApplicationEvent(ApplicationEvent applicationEvent) {

		if (applicationEvent instanceof ApplicationContextOpenedEvent) {

			this.pdsColumn.setVisible(true);

			this.context = ((ApplicationContextOpenedEvent) applicationEvent).getContext();

			this.refresh(applicationEvent);
		}

		if (applicationEvent instanceof ApplicationContextClosedEvent) {

			this.pdsColumn.setVisible(false);

			for (Component component : MainWindow.findChildComponentsByClass(this, WindowPane.class)) {

				if (component instanceof LogWindowPane) continue;
				if (component instanceof ContextWindowPane) continue;

				this.remove(component);
			}
		}
	}

	private void onAccountRootActionPerformed(ActionEvent e) {

		XRI3Segment subjectXri = new XRI3Segment(this.context.getCanonical());
		AccountRootWindowPane accountRootWindowPane = new AccountRootWindowPane();
		accountRootWindowPane.setContextAndSubjectXri(this.context, subjectXri);

		this.add(accountRootWindowPane);
	}

	private void onLinkContractsActionPerformed(ActionEvent e) {

		MessageDialog.notImplemented();
	}

	private void onLogWindowActionPerformed(ActionEvent e) {

		if (this.logWindowCheckBox.isSelected()) {

			MainWindow.findChildComponentByClass(this, LogWindowPane.class).setVisible(true);
		} else {

			MainWindow.findChildComponentByClass(this, LogWindowPane.class).setVisible(false);
		}
	}

	private void onDeveloperModeActionPerformed(ActionEvent e) {

		if (this.developerModeCheckBox.isSelected()) {

			MainWindow.findChildComponentById(this, "transactionEventPanelsContentPane").setVisible(true);
			for (Component component : MainWindow.findChildComponentsByClass(this, DeveloperModeComponent.class)) component.setVisible(true);
		} else {

			MainWindow.findChildComponentById(this, "transactionEventPanelsContentPane").setVisible(false);
			for (Component component : MainWindow.findChildComponentsByClass(this, DeveloperModeComponent.class)) component.setVisible(false);
		}
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		ResourceImageReference imageReference1 = new ResourceImageReference(
		"/pds/web/resource/image/mainback-gray.jpg");
		this.setBackgroundImage(new FillImage(imageReference1));
		SplitPane splitPane1 = new SplitPane();
		splitPane1.setStyleName("Default");
		splitPane1.setSeparatorVisible(false);
		add(splitPane1);
		Column column1 = new Column();
		splitPane1.add(column1);
		pdsColumn = new Column();
		pdsColumn.setVisible(false);
		pdsColumn.setInsets(new Insets(new Extent(30, Extent.PX)));
		pdsColumn.setCellSpacing(new Extent(20, Extent.PX));
		RowLayoutData pdsColumnLayoutData = new RowLayoutData();
		pdsColumnLayoutData.setAlignment(new Alignment(Alignment.LEFT,
				Alignment.DEFAULT));
		pdsColumn.setLayoutData(pdsColumnLayoutData);
		column1.add(pdsColumn);
		Row row4 = new Row();
		row4.setCellSpacing(new Extent(10, Extent.PX));
		pdsColumn.add(row4);
		Button button1 = new Button();
		button1.setStyleName("PlainWhite");
		ResourceImageReference imageReference2 = new ResourceImageReference(
		"/pds/web/resource/image/accountroot.png");
		button1.setIcon(imageReference2);
		button1.setText("Account Root");
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onAccountRootActionPerformed(e);
			}
		});
		row4.add(button1);
		Button button10 = new Button();
		button10.setStyleName("PlainWhite");
		ResourceImageReference imageReference3 = new ResourceImageReference(
		"/pds/web/resource/image/linkcontracts.png");
		button10.setIcon(imageReference3);
		button10.setText("Link Contracts");
		button10.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onLinkContractsActionPerformed(e);
			}
		});
		row4.add(button10);
		Label label1 = new Label();
		label1.setStyleName("Header");
		label1.setText("Account Personas");
		pdsColumn.add(label1);
		Row row7 = new Row();
		pdsColumn.add(row7);
		accountRootGrid = new AccountRootGrid();
		row7.add(accountRootGrid);
		Label label2 = new Label();
		label2.setStyleName("Header");
		label2.setText("Applications");
		pdsColumn.add(label2);
		pdsWebAppGrid = new Grid();
		pdsWebAppGrid.setOrientation(Grid.ORIENTATION_HORIZONTAL);
		pdsWebAppGrid.setSize(4);
		pdsColumn.add(pdsWebAppGrid);
		Column column2 = new Column();
		column2.setInsets(new Insets(new Extent(10, Extent.PX)));
		SplitPaneLayoutData column2LayoutData = new SplitPaneLayoutData();
		column2LayoutData.setAlignment(new Alignment(Alignment.RIGHT,
				Alignment.DEFAULT));
		column2LayoutData.setMinimumSize(new Extent(400, Extent.PX));
		column2LayoutData.setMaximumSize(new Extent(400, Extent.PX));
		column2.setLayoutData(column2LayoutData);
		splitPane1.add(column2);
		ImageIcon imageIcon1 = new ImageIcon();
		ResourceImageReference imageReference4 = new ResourceImageReference(
		"/pds/web/resource/image/pds-logo.png");
		imageIcon1.setIcon(imageReference4);
		imageIcon1.setHeight(new Extent(45, Extent.PX));
		imageIcon1.setWidth(new Extent(337, Extent.PX));
		imageIcon1.setInsets(new Insets(new Extent(0, Extent.PX), new Extent(
				10, Extent.PX), new Extent(0, Extent.PX), new Extent(0,
						Extent.PX)));
		column2.add(imageIcon1);
		Row row2 = new Row();
		row2.setAlignment(new Alignment(Alignment.RIGHT, Alignment.DEFAULT));
		row2.setInsets(new Insets(new Extent(0, Extent.PX), new Extent(0,
				Extent.PX), new Extent(0, Extent.PX), new Extent(10, Extent.PX)));
		row2.setCellSpacing(new Extent(10, Extent.PX));
		column2.add(row2);
		ImageIcon imageIcon2 = new ImageIcon();
		ResourceImageReference imageReference5 = new ResourceImageReference(
		"/pds/web/resource/image/database.png");
		imageIcon2.setIcon(imageReference5);
		imageIcon2.setHeight(new Extent(68, Extent.PX));
		imageIcon2.setWidth(new Extent(68, Extent.PX));
		row2.add(imageIcon2);
		Column column3 = new Column();
		column3.setCellSpacing(new Extent(10, Extent.PX));
		row2.add(column3);
		logWindowCheckBox = new CheckBox();
		logWindowCheckBox.setSelected(false);
		logWindowCheckBox.setText("Show Log Window");
		logWindowCheckBox.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onLogWindowActionPerformed(e);
			}
		});
		column3.add(logWindowCheckBox);
		developerModeCheckBox = new CheckBox();
		developerModeCheckBox.setSelected(false);
		developerModeCheckBox.setText("Enable Developer Mode");
		developerModeCheckBox.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onDeveloperModeActionPerformed(e);
			}
		});
		column3.add(developerModeCheckBox);
		LogWindowPane logWindowPane1 = new LogWindowPane();
		logWindowPane1.setVisible(false);
		add(logWindowPane1);
		ContextWindowPane accountWindowPane1 = new ContextWindowPane();
		add(accountWindowPane1);
	}
}
