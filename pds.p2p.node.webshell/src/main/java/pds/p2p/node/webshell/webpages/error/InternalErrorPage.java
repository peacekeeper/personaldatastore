package pds.p2p.node.webshell.webpages.error;

import pds.p2p.node.webshell.webpages.BasePage;

public class InternalErrorPage extends BasePage {

	private static final long serialVersionUID = -2025103990399589635L;

	public InternalErrorPage() {
		
		this.setTitle(this.getString("title"));
	}
	
	@Override
	public boolean isErrorPage() {
		
		return true;
	}
}
