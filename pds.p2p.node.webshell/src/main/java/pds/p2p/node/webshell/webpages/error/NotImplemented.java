package pds.p2p.node.webshell.webpages.error;

import pds.p2p.node.webshell.webpages.BasePage;

public class NotImplemented extends BasePage {

	private static final long serialVersionUID = -8742800773604795312L;

	public NotImplemented() {
		
		this.setTitle(this.getString("title"));
	}
	
	@Override
	public boolean isErrorPage() {
		
		return true;
	}
}
