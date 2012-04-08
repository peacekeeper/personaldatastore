package pds.p2p.node.webshell.webpages;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

import pds.p2p.node.webshell.webpages.user.Login;

public class LoginPanel extends Panel {

	private static final long serialVersionUID = -5045712358557314269L;

	public LoginPanel(String id) {

		super(id);

		// create and add components

		this.add(new BookmarkablePageLink<Page> ("loginLink", Login.class));
	}
}
