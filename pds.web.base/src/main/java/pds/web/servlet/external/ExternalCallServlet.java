package pds.web.servlet.external;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pds.web.PDSApplication;
import pds.web.ui.MainWindow;

public class ExternalCallServlet extends HttpServlet {

	private static final long serialVersionUID = -8736563564727679509L;

	private static Logger log = LoggerFactory.getLogger(ExternalCallServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		this.doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {

			String url = request.getRequestURL().toString();
			String path = url.substring(url.lastIndexOf("/") + 1);

			PDSApplication pdsApplication = PDSApplication.getAppFromSession(request.getSession());

			MainWindow mainWindow = pdsApplication.getMainWindow();
			ExternalCallReceiver externalCallReceiver;
			externalCallReceiver = (ExternalCallReceiver) MainWindow.findChildComponentByClassName(mainWindow, path);
			if (externalCallReceiver == null) externalCallReceiver = (ExternalCallReceiver) MainWindow.findChildComponentById(mainWindow, path);
			if (externalCallReceiver == null) externalCallReceiver = (ExternalCallReceiver) MainWindow.findChildComponentByRenderId(mainWindow, path);

			externalCallReceiver.onExternalCall(pdsApplication, request, response);
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			response.sendRedirect("/");
		}
	}
}
