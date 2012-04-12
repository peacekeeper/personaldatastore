package pds.p2p.node.webshell.webpages;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

import pds.p2p.node.webshell.webpages.intent.CreateIntent;
import pds.p2p.node.webshell.webpages.intent.ViewIntents;
import pds.p2p.node.webshell.webpages.terminals.ShellTerminal;
import pds.p2p.node.webshell.webpages.terminals.XDITerminal;
import pds.p2p.node.webshell.webpages.user.Connection;
import pds.p2p.node.webshell.webpages.user.Identity;
import pds.p2p.node.webshell.webpages.user.PersonalData;
import pds.p2p.node.webshell.webpages.user.Relations;

public class SidePanel extends Panel {

	private static final long serialVersionUID = -6517057009303244973L;

	public SidePanel(String id) {

		super(id);

		// create and add components

		this.add(new BookmarkablePageLink<Page> ("IdentityLink", Identity.class));
		this.add(new BookmarkablePageLink<Page> ("ConnectionLink", Connection.class));
		this.add(new BookmarkablePageLink<Page> ("PersonalDataLink", PersonalData.class));
		this.add(new BookmarkablePageLink<Page> ("RelationsLink", Relations.class));
		this.add(new BookmarkablePageLink<Page> ("CreateIntentLink", CreateIntent.class));
		this.add(new BookmarkablePageLink<Page> ("ViewIntentsLink", ViewIntents.class));
		this.add(new BookmarkablePageLink<Page> ("ShellTerminalLink", ShellTerminal.class));
		this.add(new BookmarkablePageLink<Page> ("XDITerminalLink", XDITerminal.class));
	}
}
