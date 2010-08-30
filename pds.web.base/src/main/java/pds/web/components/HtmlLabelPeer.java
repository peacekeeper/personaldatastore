package pds.web.components;

import nextapp.echo.app.Component;
import nextapp.echo.app.util.Context;
import nextapp.echo.webcontainer.AbstractComponentSynchronizePeer;
import nextapp.echo.webcontainer.ServerMessage;
import nextapp.echo.webcontainer.WebContainerServlet;
import nextapp.echo.webcontainer.service.JavaScriptService;

public class HtmlLabelPeer extends AbstractComponentSynchronizePeer {

	private static final String JFIX_HTML_LABEL = "JFixHtmlLabel";

	static {

		WebContainerServlet.getServiceRegistry().add(
				JavaScriptService.forResource(JFIX_HTML_LABEL,
				"pds/web/components/HtmlLabel.js"));

	}

	@Override
	public void init(Context context, Component component) {

		super.init(context, component);

		ServerMessage serverMessage = (ServerMessage) context.get(ServerMessage.class);
		serverMessage.addLibrary(JFIX_HTML_LABEL);
	}

	@Override
	public Class<?> getComponentClass() {

		return HtmlLabel.class;
	}

	public String getClientComponentType(boolean shortType) {

		return JFIX_HTML_LABEL;
	}
}
