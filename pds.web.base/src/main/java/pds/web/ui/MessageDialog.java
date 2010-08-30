package pds.web.ui;

import java.util.EventListener;
import java.util.ResourceBundle;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.app.Button;
import nextapp.echo.app.Component;
import nextapp.echo.app.Extent;
import nextapp.echo.app.ImageReference;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.WindowPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.RowLayoutData;

import pds.web.PDSApplication;
import pds.web.logger.Logger;

/**
 * A generic modal dialog that displays a message.
 */
@SuppressWarnings("unqualified-field-access")
public class MessageDialog extends WindowPane {

	private static final long serialVersionUID = 1L;

	public static final ImageReference ICON_ERROR
	= new ResourceImageReference("/pds/web/resource/image/icon/message/message-problem.png");
	public static final ImageReference ICON_INFORMATION
	= new ResourceImageReference("/pds/web/resource/image/icon/message/message-info.png");
	public static final ImageReference ICON_QUESTION
	= new ResourceImageReference("/pds/web/resource/image/icon/message/message-question.png");
	public static final ImageReference ICON_WARNING
	= new ResourceImageReference("/pds/web/resource/image/icon/message/message-warning.png");

	/**
	 * Command provided in <code>ActionEvent</code>s when the user presses the 
	 * 'cancel' or 'no' button.
	 */
	public static final String COMMAND_CANCEL = "cancel";

	/**
	 * Command provided in <code>ActionEvent</code>s when the user presses the 
	 * 'ok' or 'yes' button.
	 */
	public static final String COMMAND_OK = "ok";

	/**
	 * Control configuration constant indicating that only an 'ok' button should
	 * be displayed.
	 */
	public static final int CONTROLS_OK = 1;

	/**
	 * Control configuration constant indicating that only an 'yes' and 'no' 
	 * buttons should be displayed.
	 */
	public static final int CONTROLS_YES_NO = 2;

	private ActionListener actionProcessor = new ActionListener() {

		private static final long serialVersionUID = 1L;

		/**
		 * @see nextapp.echo.app.event.ActionListener#actionPerformed(nextapp.echo.app.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			getParent().remove(MessageDialog.this);
			EventListener[] listeners = getEventListenerList().getListeners(ActionListener.class);
			ActionEvent outgoingEvent = new ActionEvent(this, e.getActionCommand());
			for (int i = 0; i < listeners.length; ++i) {
				((ActionListener) listeners[i]).actionPerformed(outgoingEvent);
			}
		}
	};

	private int controlConfiguration;
	private Row controlsRow;
	private ResourceBundle resourceBundle;

	private Label contentLabel;

	private Label iconLabel;

	/**
	 * Creates a new <code>MessageDialog</code>.
	 */
	private MessageDialog() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	/**
	 * Creates a new <code>MessageDialog</code>.
	 * Icon is automatically set based on control configuration.
	 * 
	 * @param title the dialog title
	 * @param message the message to display
	 * @param controlConfiguration the control configuration, one of the 
	 *        following values:
	 *        <ul>
	 *         <li><code>CONTROLS_OK</code></li>
	 *         <li><code>CONTROLS_YES_NO</code></li>
	 *        </ul>
	 */
	public MessageDialog(String title, String message, int controlConfiguration) {
		this();
		setTitle(title);
		setMessage(message);
		switch (controlConfiguration) {
		case CONTROLS_OK:
			setMessageIcon(ICON_INFORMATION);
			break;
		case CONTROLS_YES_NO:
			setMessageIcon(ICON_QUESTION);
			break;
		}
		setControlConfiguration(controlConfiguration);
	}

	/**
	 * Creates a new <code>MessageDialog</code>.
	 * 
	 * @param title the dialog title
	 * @param message the message to display
	 * @param controlConfiguration the control configuration, one of the 
	 *        following values:
	 *        <ul>
	 *         <li><code>CONTROLS_OK</code></li>
	 *         <li><code>CONTROLS_YES_NO</code></li>
	 *        </ul>
	 */
	public MessageDialog(String title, String message, ImageReference messageIcon, int controlConfiguration) {
		this();
		setTitle(title);
		setMessageIcon(messageIcon);
		setMessage(message);
		setControlConfiguration(controlConfiguration);
	}

	public void show() {

		Component mainContentPane = PDSApplication.getApp().getDefaultWindow().getContent();
		mainContentPane.add(this);
		this.setZIndex(Integer.MAX_VALUE);
	}

	public static void info(String message) {

		Logger logger = PDSApplication.getApp().getLogger();
		logger.info(message, null);

		MessageDialog dialog = new MessageDialog("Information", message, MessageDialog.CONTROLS_OK);
		dialog.show();
	}

	public static void warning(String message) {

		Logger logger = PDSApplication.getApp().getLogger();
		logger.warning(message, null);

		MessageDialog dialog = new MessageDialog("Warning", message, MessageDialog.ICON_WARNING, MessageDialog.CONTROLS_OK);
		dialog.show();
	}

	public static void problem(String message, Throwable ex) {

		Logger logger = PDSApplication.getApp().getLogger();
		logger.problem(message, null, ex);

		MessageDialog dialog = new MessageDialog("Problem", message, MessageDialog.ICON_ERROR, MessageDialog.CONTROLS_OK);
		dialog.show();
	}

	public static void yesNo(String message, ActionListener actionListener) {

		MessageDialog dialog = new MessageDialog("Yes / No", message, MessageDialog.ICON_QUESTION, MessageDialog.CONTROLS_YES_NO);
		dialog.addActionListener(actionListener);
		dialog.show();
	}

	public static void notImplemented() {

		info("Sorry, this feature is not currently implemented. Please contact us for support.");
	}

	/**
	 * Adds an <code>ActionListener</code> to receive notification when the
	 * user selects a choice.  The fired <code>command</code> of the fired 
	 * <code>ActionEvent</code> will contain be one of the 
	 * <code>COMMAND_XXX</code> constants.
	 * 
	 * @param l the <code>ActionListener</code> to add
	 */
	public void addActionListener(ActionListener l) {
		getEventListenerList().addListener(ActionListener.class, l);
	}

	/**
	 * Returns the control configuration.
	 * 
	 * @return the control configuration, one of the following values:
	 *         <ul>
	 *          <li><code>CONTROLS_OK</code></li>
	 *          <li><code>CONTROLS_YES_NO</code></li>
	 *         </ul>
	 */
	public int getControlConfiguration() {
		return controlConfiguration;
	}

	/**
	 * Returns the displayed message.
	 * 
	 * @return the displayed message
	 */
	public String getMessage() {
		return contentLabel.getText();
	}

	/**
	 * Returns the displayed message icon.
	 * 
	 * @return the displayed message icon
	 */
	public ImageReference getMessageIcon() {
		return iconLabel.getIcon();
	}

	/**
	 * Removes an <code>ActionListener</code> from receiving notification 
	 * when the user selects a choice.
	 * 
	 * @param l the <code>ActionListener</code> to remove
	 */
	public void removeActionListener(ActionListener l) {
		getEventListenerList().removeListener(ActionListener.class, l);
	}

	/**
	 * Sets the control configuration.
	 * 
	 * @param newValue the new configuration, one of the following values:
	 *        <ul>
	 *         <li><code>CONTROLS_OK</code></li>
	 *         <li><code>CONTROLS_YES_NO</code></li>
	 *        </ul>
	 */
	public void setControlConfiguration(int newValue) {
		controlConfiguration = newValue;
		controlsRow.removeAll();
		Button button;
		switch (controlConfiguration) {
		case CONTROLS_OK:
			button = new Button(resourceBundle.getString("Generic.Ok"));
			button.setStyleName("Default");
			button.setActionCommand(COMMAND_OK);
			button.addActionListener(actionProcessor);
			controlsRow.add(button);
			break;
		case CONTROLS_YES_NO:
			button = new Button(resourceBundle.getString("Generic.Yes"));
			button.setStyleName("Default");
			button.setActionCommand(COMMAND_OK);
			button.addActionListener(actionProcessor);
			controlsRow.add(button);
			button = new Button(resourceBundle.getString("Generic.No"));
			button.setStyleName("Default");
			button.setActionCommand(COMMAND_CANCEL);
			button.addActionListener(actionProcessor);
			controlsRow.add(button);
			break;
		}
	}

	/**
	 * Sets the displayed message.
	 * 
	 * @param message the displayed message text.
	 */
	public void setMessage(String message) {
		contentLabel.setText(message);
	}

	/**
	 * Sets the displayed message icon.
	 * 
	 * @param message the displayed message icon.
	 */
	public void setMessageIcon(ImageReference messageIcon) {
		iconLabel.setIcon(messageIcon);
		iconLabel.setVisible(messageIcon != null);
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		resourceBundle = ResourceBundle.getBundle(
				"pds.web.resource.localization.Messages",
				ApplicationInstance.getActive().getLocale());
		this.setStyleName("Gray");
		this.setHeight(new Extent(13, Extent.EM));
		this.setClosable(false);
		this.setMinimumWidth(new Extent(30, Extent.EM));
		this.setWidth(new Extent(30, Extent.EM));
		this.setMinimumHeight(new Extent(15, Extent.EM));
		this.setModal(true);
		SplitPane splitPane1 = new SplitPane();
		splitPane1.setOrientation(SplitPane.ORIENTATION_VERTICAL_BOTTOM_TOP);
		splitPane1.setAutoPositioned(true);
		add(splitPane1);
		controlsRow = new Row();
		controlsRow.setStyleName("ControlPane");
		controlsRow.setAlignment(new Alignment(Alignment.RIGHT,
				Alignment.DEFAULT));
		splitPane1.add(controlsRow);
		Row row1 = new Row();
		row1.setInsets(new Insets(new Extent(1, Extent.EM)));
		row1.setCellSpacing(new Extent(1, Extent.EM));
		splitPane1.add(row1);
		iconLabel = new Label();
		iconLabel.setVisible(false);
		RowLayoutData iconLabelLayoutData = new RowLayoutData();
		iconLabelLayoutData.setAlignment(new Alignment(Alignment.DEFAULT,
				Alignment.TOP));
		iconLabel.setLayoutData(iconLabelLayoutData);
		row1.add(iconLabel);
		contentLabel = new Label();
		contentLabel.setStyleName("MessageDialog.ContentLabel");
		row1.add(contentLabel);
	}
}
