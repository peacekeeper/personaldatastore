package pds.p2p.node.webshell.webpages.terminals;

import org.apache.wicket.markup.html.IHeaderResponse;

import pds.p2p.node.webshell.webpages.BasePage;

public class XDITerminal extends BasePage {

	private static final long serialVersionUID = 7513611229379274933L;

	public XDITerminal() {

		this.setTitle(this.getString("title"));
	}

	@Override
	public void renderHead(IHeaderResponse response) {

		super.renderHead(response);

		// add javascript

		response.renderJavaScriptReference("lib/termlib/compacted/termlib_min.js");
		response.renderJavaScriptReference("lib/script-xdi-polaris.js");
		response.renderJavaScriptReference("lib/script-xdi-sirius.js");
	}
}
