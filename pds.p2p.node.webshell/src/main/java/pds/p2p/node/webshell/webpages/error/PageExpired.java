package pds.p2p.node.webshell.webpages.error;

import pds.p2p.node.webshell.webpages.BasePage;

public class PageExpired extends BasePage {

	private static final long serialVersionUID = 3157720231241893951L;

	public PageExpired() {
		
		this.setTitle(this.getString("title"));
	}
	
	@Override
	public boolean isErrorPage() {
		
		return true;
	}
}
