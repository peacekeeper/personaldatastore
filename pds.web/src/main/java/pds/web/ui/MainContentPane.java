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
import pds.web.ui.app.addressbook.AddressBookWindowPane;
import pds.web.ui.app.directxdi.DirectXdiWindowPane;
import pds.web.ui.app.feed.FeedWindowPane;
import pds.web.ui.app.photos.PhotosWindowPane;
import pds.web.ui.context.ContextWindowPane;
import pds.web.ui.log.LogWindowPane;
import pds.web.xdi.objects.XdiContext;
import echopoint.ImageIcon;
import pds.web.ui.AccountRootGrid;

public class MainContentPane extends ContentPane implements ApplicationListener {

	private static final long serialVersionUID = 3164240822381021756L;

	protected ResourceBundle resourceBundle;

	private XdiContext context;
	private XRI3Segment subjectXri;

	private Column pdsColumn;
	private AccountRootGrid accountRootGrid;
	private CheckBox logWindowCheckBox;
	private CheckBox developerModeCheckBox;

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

			this.accountRootGrid.setContextAndSubjectXri(this.context, this.subjectXri);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your personal data: " + ex.getMessage(), ex);
			return;
		}
	}

	public boolean isDeveloperModeSelected() {

		return this.developerModeCheckBox.isSelected();
	}

	public void onApplicationEvent(ApplicationEvent applicationEvent) {

		if (applicationEvent instanceof ApplicationContextOpenedEvent) {

			this.pdsColumn.setVisible(true);

			this.context = PDSApplication.getApp().getOpenContext();
			this.subjectXri = new XRI3Segment(this.context.getInumber());

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

		AccountRootWindowPane accountRootWindowPane = new AccountRootWindowPane();
		accountRootWindowPane.setContextAndSubjectXri(this.context, this.subjectXri);

		this.add(accountRootWindowPane);
	}

	private void onLinkContractsActionPerformed(ActionEvent e) {

		MessageDialog.notImplemented();
	}

	private void onApp1ActionPerformed(ActionEvent e) {

		FeedWindowPane feedWindowPane = new FeedWindowPane();
		feedWindowPane.setContextAndSubjectXri(this.context, this.subjectXri);

		this.add(feedWindowPane);
	}

	private void onApp2ActionPerformed(ActionEvent e) {

		AddressBookWindowPane addressBookWindowPane = new AddressBookWindowPane();
		addressBookWindowPane.setContextAndSubjectXri(this.context, this.subjectXri);

		this.add(addressBookWindowPane);
	}

	private void onApp3ActionPerformed(ActionEvent e) {

		PhotosWindowPane photosWindowPane = new PhotosWindowPane();
		photosWindowPane.setContextAndSubjectXri(this.context, this.subjectXri);

		this.add(photosWindowPane);
	}

	private void onApp4ActionPerformed(ActionEvent e) {

		DirectXdiWindowPane directXdiWindowPane = new DirectXdiWindowPane();

		this.add(directXdiWindowPane);
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
		Row row3 = new Row();
		row3.setCellSpacing(new Extent(20, Extent.PX));
		pdsColumn.add(row3);
		Button button3 = new Button();
		button3.setStyleName("PlainWhite");
		ResourceImageReference imageReference4 = new ResourceImageReference(
				"/pds/web/resource/image/app-feed.png");
		button3.setIcon(imageReference4);
		button3.setText("Feed");
		button3.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onApp1ActionPerformed(e);
			}
		});
		row3.add(button3);
		Button button4 = new Button();
		button4.setStyleName("PlainWhite");
		ResourceImageReference imageReference5 = new ResourceImageReference(
				"/pds/web/resource/image/app-addressbook.png");
		button4.setIcon(imageReference5);
		button4.setText("Address Book");
		button4.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onApp2ActionPerformed(e);
			}
		});
		row3.add(button4);
		Button button11 = new Button();
		button11.setStyleName("PlainWhite");
		ResourceImageReference imageReference6 = new ResourceImageReference(
				"/pds/web/resource/image/app-photos.png");
		button11.setIcon(imageReference6);
		button11.setText("Photos");
		button11.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onApp3ActionPerformed(e);
			}
		});
		row3.add(button11);
		Button button5 = new Button();
		button5.setStyleName("PlainWhite");
		ResourceImageReference imageReference7 = new ResourceImageReference(
				"/pds/web/resource/image/app-xdi.png");
		button5.setIcon(imageReference7);
		button5.setText("Direct XDI");
		button5.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onApp4ActionPerformed(e);
			}
		});
		row3.add(button5);
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
		ResourceImageReference imageReference8 = new ResourceImageReference(
				"/pds/web/resource/image/pds-logo.png");
		imageIcon1.setIcon(imageReference8);
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
		ResourceImageReference imageReference9 = new ResourceImageReference(
				"/pds/web/resource/image/database.png");
		imageIcon2.setIcon(imageReference9);
		imageIcon2.setHeight(new Extent(68, Extent.PX));
		imageIcon2.setWidth(new Extent(68, Extent.PX));
		row2.add(imageIcon2);
		Column column3 = new Column();
		column3.setCellSpacing(new Extent(10, Extent.PX));
		row2.add(column3);
		logWindowCheckBox = new CheckBox();
		logWindowCheckBox.setText("Show Log Window");
		logWindowCheckBox.setSelected(false);
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
		ContextWindowPane accountWindowPane1 = new ContextWindowPane();
		add(accountWindowPane1);
		LogWindowPane logWindowPane1 = new LogWindowPane();
		add(logWindowPane1);
	}
}
