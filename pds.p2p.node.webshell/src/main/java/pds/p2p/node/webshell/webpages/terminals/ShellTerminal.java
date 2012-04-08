package pds.p2p.node.webshell.webpages.terminals;

import org.apache.wicket.markup.html.IHeaderResponse;

import pds.p2p.node.webshell.webpages.BasePage;

public class ShellTerminal extends BasePage {

	private static final long serialVersionUID = 2414433617684066630L;

	public ShellTerminal() {

		this.setTitle(this.getString("title"));
	}

	@Override
	public void renderHead(IHeaderResponse response) {

		super.renderHead(response);

		// add javascript

		response.renderJavaScriptReference("lib/termlib/compacted/termlib_min.js");
		response.renderJavaScriptReference("lib/script-shell.js");
	}
}
