package pds.web.ui.connectors;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nextapp.echo.app.Button;
import nextapp.echo.app.Panel;
import nextapp.echo.app.TaskQueueHandle;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.webcontainer.WebContainerServlet;
import nextapp.echo.webcontainer.command.BrowserRedirectCommand;
import pds.web.PDSApplication;
import pds.web.servlet.external.ExternalCallReceiver;
import pds.web.ui.MessageDialog;
import pds.xdi.XdiEndpoint;
import pds.xdi.events.XdiGraphAddEvent;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import pds.xdi.events.XdiGraphModEvent;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.connector.personal.api.PersonalApi;
import xdi2.connector.personal.mapping.PersonalMapping;
import xdi2.core.ContextNode;
import xdi2.core.features.multiplicity.XdiAttribute;
import xdi2.core.util.StatementUtil;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;

public class PersonalConnectorPanel extends Panel implements XdiGraphListener, ExternalCallReceiver {

	private static final long serialVersionUID = 1L;

	protected ResourceBundle resourceBundle;

	private XdiEndpoint endpoint;
	private XdiAttribute xdiAttribute;
	private XRI3Segment xdiAttributeXri;

	private PersonalApi personalApi;
	private PersonalMapping personalMapping;

	/**
	 * Creates a new <code>PersonalConnectorPanel</code>.
	 */
	public PersonalConnectorPanel() {
		super();

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void init() {

		super.init();

		this.personalApi = new PersonalApi("2hmsfwb28jkmtuetxzk82x7r", "CyhuffsrBqdTfzTAsdMB9D6v");
		this.personalMapping = new PersonalMapping();
	}

	@Override
	public void dispose() {

		super.dispose();

		// remove us as listener

		if (this.endpoint != null) this.endpoint.removeXdiGraphListener(this);
	}

	private void refresh() {

		try {

			// refresh data

			if (this.xdiAttribute == null) this.xdiGet();

			// refresh UI

		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	private void xdiGet() throws Xdi2ClientException {

		// $get

		Message message = this.endpoint.prepareMessage();
		message.createGetOperation(this.xdiAttributeXri);

		MessageResult messageResult = this.endpoint.send(message);

		ContextNode contextNode = messageResult.getGraph().findContextNode(this.xdiAttributeXri, false);

		this.xdiAttribute = contextNode == null ? null : XdiAttribute.fromContextNode(contextNode);
	}

	private void xdiAdd(String value) throws Xdi2ClientException {

		// $add

		Message message = this.endpoint.prepareMessage();
		message.createAddOperation(StatementUtil.fromLiteralComponents(this.xdiAttributeXri, value));

		this.endpoint.send(message);
	}

	private void xdiDel() throws Xdi2ClientException {

		// $del

		Message message = this.endpoint.prepareMessage();
		message.createDelOperation(this.xdiAttributeXri);

		this.endpoint.send(message);
	}

	public XRI3Segment xdiMainAddress() {

		return this.xdiAttribute.getContextNode().getXri();
	}

	public XRI3Segment[] xdiGetAddresses() {

		return new XRI3Segment[0];
	}

	public XRI3Segment[] xdiAddAddresses() {

		return new XRI3Segment[0];
	}

	public XRI3Segment[] xdiModAddresses() {

		return new XRI3Segment[0];
	}

	public XRI3Segment[] xdiDelAddresses() {

		return new XRI3Segment[] {
				this.xdiAttributeXri
		};
	}

	public void onXdiGraphEvent(XdiGraphEvent xdiGraphEvent) {

		try {

			if (xdiGraphEvent instanceof XdiGraphAddEvent) {

				this.refresh();
				return;
			}

			if (xdiGraphEvent instanceof XdiGraphModEvent) {

				this.refresh();
				return;
			}

			if (xdiGraphEvent instanceof XdiGraphDelEvent) {

				this.removeAll();
				return;
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	public void setEndpointAndXdiAttribute(XdiEndpoint endpoint, XdiAttribute xdiAttribute, XRI3Segment xdiAttributeXri) {

		// remove us as listener

		if (this.endpoint != null) this.endpoint.removeXdiGraphListener(this);

		// refresh

		this.endpoint = endpoint;
		this.xdiAttribute = xdiAttribute;
		this.xdiAttributeXri = xdiAttributeXri;

		this.refresh();

		// add us as listener

		this.endpoint.addXdiGraphListener(this);
	}

	private void onConnectPersonalActionPerformed(ActionEvent e) {

		HttpServletRequest request = WebContainerServlet.getActiveConnection().getRequest();

		String redirectUri = request.getRequestURL().toString();
		redirectUri = redirectUri.substring(0, redirectUri.lastIndexOf("/pds.web"));
		if (! redirectUri.endsWith("/")) redirectUri += "/";
		redirectUri += "external/personalConnectorPanel";

		XRI3Segment userXri = this.endpoint.getCanonical();

		try {

			PDSApplication.getApp().enqueueCommand(new BrowserRedirectCommand(this.personalApi.startOAuth(request, redirectUri, userXri)));
			return;
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred: " + ex.getMessage(), ex);
			return;
		}
	}

	@Override
	public void onExternalCall(PDSApplication pdsApplication, HttpServletRequest request, HttpServletResponse response) throws IOException {

		TaskQueueHandle taskQueueHandle = pdsApplication.getTaskQueueHandle();

		// error from OAuth?

		if (request.getParameter("error") != null) {

			String errorDescription = request.getParameter("error_description");
			if (errorDescription == null) errorDescription = request.getParameter("error_reason");
			if (errorDescription == null) errorDescription = request.getParameter("error");

			request.setAttribute("error", "OAuth error: " + errorDescription);
		}

		// callback from OAuth?

		if (request.getParameter("code") != null) {

			XRI3Segment userXri = PersonalConnectorPanel.this.endpoint.getCanonical();

			try {

				PersonalConnectorPanel.this.personalApi.checkState(request, userXri);

				final String accessToken = PersonalConnectorPanel.this.personalApi.exchangeCodeForAccessToken(request);
				if (accessToken == null) throw new Exception("No access token received.");

				pdsApplication.enqueueTask(taskQueueHandle, new Runnable() {

					public void run() {

						try {

							PersonalConnectorPanel.this.xdiDel();
							PersonalConnectorPanel.this.xdiAdd(accessToken);
						} catch (Xdi2ClientException ex) {

							MessageDialog.problem("Sorry, a problem occurred: " + ex.getMessage(), ex);
							return;
						}
					}
				});

				response.sendRedirect("/");
				return;
			} catch (final Exception ex) {

				pdsApplication.enqueueTask(taskQueueHandle, new Runnable() {

					public void run() {

						MessageDialog.problem("Sorry, a problem occurred: " + ex.getMessage(), ex);
						return;
					}
				});

				response.sendRedirect("/");
				return;
			}
		}
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		Button button1 = new Button();
		button1.setStyleName("Default");
		button1.setText("Connect to Personal");
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onConnectPersonalActionPerformed(e);
			}
		});
		add(button1);
	}
}
