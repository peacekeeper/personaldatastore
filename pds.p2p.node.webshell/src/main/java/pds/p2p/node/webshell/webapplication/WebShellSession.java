package pds.p2p.node.webshell.webapplication;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebShellSession extends WebSession {

	private static final long serialVersionUID = -3945438585847130731L;

	protected static Logger log = LoggerFactory.getLogger(WebShellSession.class.getName());

	/*
	 * The page from which we started an external call, and to which we want to return later.
	 */

	private static final String SESSION_ATTRIBUTE_CALLBACKPAGE = "__callbackpage__";

	public WebShellSession(Request request) {

		super(request);
	}

	public void setCallbackPage(Page page) {

		this.setAttribute(SESSION_ATTRIBUTE_CALLBACKPAGE, page);
	}

	public Page getCallbackPage() {

		return((Page) this.getAttribute(SESSION_ATTRIBUTE_CALLBACKPAGE));
	}
}