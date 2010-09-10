package pds.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.app.TaskQueueHandle;
import nextapp.echo.app.Window;
import nextapp.echo.webcontainer.WebContainerServlet;
import pds.web.events.ApplicationContextClosedEvent;
import pds.web.events.ApplicationContextOpenedEvent;
import pds.web.events.ApplicationEvent;
import pds.web.events.ApplicationListener;
import pds.web.logger.Logger;
import pds.web.resource.style.Styles;
import pds.web.ui.MainContentPane;
import pds.web.ui.MainWindow;
import pds.xdi.Xdi;
import pds.xdi.XdiContext;

/**
 * Application instance implementation.
 */
public class PDSApplication extends ApplicationInstance {

	private static final long serialVersionUID = -8129396233829232511L;

	private PDSServlet servlet;
	private MainWindow mainWindow;
	private TaskQueueHandle taskQueueHandle;
	private Map<String, Object> attributes;
	private XdiContext openedContext;

	private transient Logger logger;
	private transient Xdi xdi;

	private List<ApplicationListener> applicationListeners;

	public PDSApplication(PDSServlet servlet) {

		this.servlet = servlet;

		this.initLogger();
		this.initXdi();

		this.applicationListeners = new ArrayList<ApplicationListener> ();
	}

	public static PDSApplication getApp() {

		return (PDSApplication) getActive();
	}

	public static PDSApplication getAppFromSession(HttpSession session) {

		return (PDSApplication) session.getAttribute("__echoapp");
	}

	@Override
	public Window init() {

		HttpSession session = WebContainerServlet.getActiveConnection().getRequest().getSession();
		session.setAttribute("__echoapp", this);
		this.setStyleSheet(Styles.DEFAULT_STYLE_SHEET);

		this.mainWindow = new MainWindow();
		this.mainWindow.setTitle("Personal Data Store");
		this.mainWindow.setContent(new MainContentPane());

		this.taskQueueHandle = this.createTaskQueue();

		this.attributes = new HashMap<String, Object> ();

		return this.mainWindow;
	}

	@Override
	public void dispose() {

		super.dispose();

		this.removeTaskQueue(this.taskQueueHandle);

		if (this.isContextOpen()) this.closeContext();
	}

	public PDSServlet getServlet() {

		return this.servlet;
	}

	public MainWindow getMainWindow() {

		return this.mainWindow;
	}

	public TaskQueueHandle getTaskQueueHandle() {

		return this.taskQueueHandle;
	}

	public void setAttribute(String name, Object value) {

		this.attributes.put(name, value);
	}

	public Object getAttribute(String name) {

		return this.attributes.get(name);
	}

	public void openContext(XdiContext context) throws Exception {

		try {

			if (this.openedContext != null) this.closeContext();

			String remoteAddr = WebContainerServlet.getActiveConnection().getRequest().getRemoteAddr();

			this.openedContext = context;

			this.fireApplicationEvent(new ApplicationContextOpenedEvent(this, this.openedContext));

			this.logger.info("Your Personal Data Store has been opened from " + remoteAddr + ".", null);
		} catch (Exception ex) {

			if (this.isContextOpen()) this.closeContext();
			throw ex;
		}
	}

	public void closeContext() {

		try {

			if (this.openedContext == null) return;

			this.fireApplicationEvent(new ApplicationContextClosedEvent(this, this.openedContext));
		} catch (Exception ex) {

		} finally {

			this.logger.info("Your Personal Data Store has been closed.", null);

			this.openedContext = null;
		}
	}

	public XdiContext getOpenContext() {

		return this.openedContext;
	}

	public boolean isContextOpen() {

		return this.openedContext != null;
	}

	/*
	 * Logger and Xdi
	 */

	private void initLogger() {

		try {

			this.logger = new Logger();
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize Logger component: " + ex.getMessage(), ex);
		}
	}

	private void initXdi() {

		try {

			this.xdi = new Xdi(this.getServlet().getResolver());
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize Xdi component: " + ex.getMessage(), ex);
		}
	}

	public Logger getLogger() {

		if (this.logger == null) this.initLogger();

		return this.logger;
	}

	public Xdi getXdi() {

		if (this.xdi == null) this.initXdi();

		return this.xdi;
	}

	/*
	 * Events
	 */

	public void addApplicationListener(ApplicationListener applicationListener) {

		if (this.applicationListeners.contains(applicationListener)) return;
		this.applicationListeners.add(applicationListener);
	}

	public void removeApplicationListener(ApplicationListener applicationListener) {

		this.applicationListeners.remove(applicationListener);
	}

	public void fireApplicationEvent(ApplicationEvent applicationEvent) {

		for (ApplicationListener applicationListener : this.applicationListeners) {

			applicationListener.onApplicationEvent(applicationEvent);
		}
	}
}
