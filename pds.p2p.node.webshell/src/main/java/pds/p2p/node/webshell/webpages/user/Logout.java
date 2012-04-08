package pds.p2p.node.webshell.webpages.user;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.link.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.webpages.BasePage;

public class Logout extends BasePage {

	private static final long serialVersionUID = -4101890723769407353L;

	protected static Logger log = LoggerFactory.getLogger(Logout.class.getName());

	public Logout() {

		this.setTitle(this.getString("title"));

		// create and add components

		this.add(new LogoutLink("logoutLink"));
	}

	private class LogoutLink extends Link<Object> {

		private static final long serialVersionUID = 3416258156206380750L;

		public LogoutLink(String id) {

			super(id);
		}

		@Override
		public void onClick() {

			// logout from orion

			Logout.log.debug("Logging out.");

			try {

				DanubeApiClient.orionObject.logout();
			} catch (Exception ex) {

				Logout.log.warn(ex.getMessage(), ex);
				error(Logout.this.getString("fail") + ex.getMessage());
				return;
			}

			// send user to home page

			this.setResponsePage(Application.get().getHomePage());
		}
	}
}
