package pds.p2p.node.webshell.webpages;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

import pds.p2p.node.webshell.webpages.information.About;
import pds.p2p.node.webshell.webpages.terminals.ShellTerminal;
import pds.p2p.node.webshell.webpages.terminals.XDITerminal;
import pds.p2p.node.webshell.webpages.vrm.CreatePrfp;
import pds.p2p.node.webshell.webpages.vrm.ViewPrfps;

public class SidePanel extends Panel {

	private static final long serialVersionUID = -6517057009303244973L;

	public SidePanel(String id) {

		super(id);

		boolean loggedIn = false;

		// create and add components

		if (loggedIn) {

			this.add(new LogoutPanel("userPanel"));
		} else {

			this.add(new LoginPanel("userPanel"));
		}
		this.add(new BookmarkablePageLink<Page> ("AboutLink", About.class));
		this.add(new BookmarkablePageLink<Page> ("ShellTerminalLink", ShellTerminal.class));
		this.add(new BookmarkablePageLink<Page> ("XDITerminalLink", XDITerminal.class));
		this.add(new BookmarkablePageLink<Page> ("CreatePrfpLink", CreatePrfp.class));
		this.add(new BookmarkablePageLink<Page> ("ViewPrfpsLink", ViewPrfps.class));
	}
}
