package pds.p2p.node.webshell.webpages.error;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import pds.p2p.node.webshell.webpages.BasePage;
import pds.p2p.node.webshell.webpages.user.Login;

public class AccessDeniedPage extends BasePage {

	private static final long serialVersionUID = 8898371818547316744L;

	public AccessDeniedPage() {

		this.setTitle(this.getString("title"));

		// create and add components

		this.add(new BookmarkablePageLink<Page> ("LoginLink", Login.class));
	}

	@Override
	public boolean isErrorPage() {

		return true;
	}
}
