package pds.web.signin.rpxnow;


import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nextapp.echo.app.Extent;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Panel;
import nextapp.echo.app.TaskQueueHandle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.higgins.XDI2.xri3.impl.XRI3Segment;
import org.w3c.dom.Element;

import pds.web.PDSApplication;
import pds.web.components.HtmlLabel;
import pds.web.servlet.external.ExternalCallReceiver;
import pds.web.signin.rpxnow.rpx.Rpx;
import pds.web.tools.util.XmlUtils;
import pds.web.ui.MessageDialog;
import pds.xdi.Xdi;
import pds.xdi.XdiContext;

public class RpxnowSignInPanel extends Panel implements ExternalCallReceiver {

	private static final long serialVersionUID = 46284183174314347L;

	private static final Logger log = LoggerFactory.getLogger(RpxnowSignInPanel.class.getName());

	protected ResourceBundle resourceBundle;

	private RpxnowSignInMethod rpxnowSignUpMethod;

	private HtmlLabel rpxnowHtmlLabel;

	/**
	 * Creates a new <code>RpxnowSignInPanel</code>.
	 */
	public RpxnowSignInPanel() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void init() {

		super.init();

		// set RPXnow html

		this.rpxnowHtmlLabel.setHtml(this.rpxnowSignUpMethod.getHtmlCode());
	}

	public void setRpxnowSignInMethod(RpxnowSignInMethod rpxnowSignUpMethod) {

		this.rpxnowSignUpMethod = rpxnowSignUpMethod;
	}

	@Override
	public void onExternalCall(PDSApplication pdsApplication, HttpServletRequest request, HttpServletResponse response) throws IOException {

		TaskQueueHandle taskQueueHandle = pdsApplication.getTaskQueueHandle();

		// read token

		String token = request.getParameter("token");
		log.debug("token: " + token);

		// make RPXnow call and parse response

		Rpx rpx = new Rpx(this.rpxnowSignUpMethod.getApiKey(), this.rpxnowSignUpMethod.getBaseUrl());

		Element rspElement = rpx.authInfo(token);
		Element profileElement = (Element) rspElement.getElementsByTagName("profile").item(0);
		Element identifierElement = (Element) profileElement.getElementsByTagName("identifier").item(0);
		Element displayNameElement = (Element) profileElement.getElementsByTagName("displayName").item(0);

		String rpxnowIdentifier = XmlUtils.getTextContent(identifierElement);
		String rpxnowDisplayName = displayNameElement != null ? XmlUtils.getTextContent(displayNameElement) : null;

		log.debug("RPXnow identifier: " + rpxnowIdentifier);
		log.debug("RPXnow displayName: " + rpxnowDisplayName);

		String identifier = rpxnowDisplayName != null ? rpxnowDisplayName : rpxnowIdentifier;
		XRI3Segment canonical = null;
		if (rpxnowIdentifier.startsWith("xri://")) canonical = new XRI3Segment(rpxnowIdentifier.substring(6));
		if (canonical == null && (rpxnowIdentifier.startsWith("=!") || rpxnowIdentifier.startsWith("@!"))) canonical = new XRI3Segment(rpxnowIdentifier);
		if (canonical == null) canonical = new XRI3Segment("=(" + rpxnowIdentifier + ")");

		String endpoint = this.rpxnowSignUpMethod.getEndpoint();
		if (! endpoint.endsWith("/")) endpoint += "/";
		endpoint += canonical.toString() + "/";

		log.debug("identifier: " + identifier);
		log.debug("canonical: " + canonical);
		log.debug("endpoint: " + endpoint);

		// try to open the context

		Xdi xdi = pdsApplication.getXdi();

		try {

			final XdiContext context = xdi.resolveContextManually(endpoint, identifier, canonical, null);

			pdsApplication.enqueueTask(taskQueueHandle, new Runnable() {

				public void run() {

					try {

						PDSApplication.getApp().openContext(context);
					} catch (Exception ex) {

						MessageDialog.problem("Sorry, we could not open your Personal Data Store: " + ex.getMessage(), ex);
						return;
					}
				}
			});
			response.sendRedirect("/");
		} catch (final Exception ex) {

			pdsApplication.enqueueTask(taskQueueHandle, new Runnable() {

				public void run() {

					MessageDialog.problem("Sorry, we could not open your Personal Data Store: " + ex.getMessage(), ex);
					return;
				}
			});
			response.sendRedirect("/");
			return;
		}
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setInsets(new Insets(new Extent(10, Extent.PX)));
		rpxnowHtmlLabel = new HtmlLabel();
		rpxnowHtmlLabel.setHtml("    ");
		add(rpxnowHtmlLabel);
	}
}
