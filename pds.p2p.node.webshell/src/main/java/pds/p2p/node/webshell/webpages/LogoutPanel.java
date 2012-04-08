package pds.p2p.node.webshell.webpages;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

import pds.p2p.node.webshell.webpages.user.Logout;

public class LogoutPanel extends Panel {

	private static final long serialVersionUID = 1164482814813071716L;

	public LogoutPanel(String id) {

		super(id);

		// create and add components

		this.add(new BookmarkablePageLink<Page> ("logoutLink", Logout.class));
	}
}
