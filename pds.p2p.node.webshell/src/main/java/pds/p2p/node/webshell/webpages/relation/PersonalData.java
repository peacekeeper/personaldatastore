package pds.p2p.node.webshell.webpages.relation;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.webapplication.WebShellAuthorizationStrategy.NeedConnectedPage;
import pds.p2p.node.webshell.webapplication.WebShellAuthorizationStrategy.NeedLoggedInPage;
import pds.p2p.node.webshell.webpages.BasePage;

public class PersonalData extends BasePage implements NeedLoggedInPage, NeedConnectedPage {

	private static final long serialVersionUID = -7620101858531404897L;

	public PersonalData() {

		this.setTitle(this.getString("title"));

		// create and add components

		String inumber;

		try {

			inumber = DanubeApiClient.orionObject.inumber();
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}

		this.add(new PersonalDataPanel("personalData", inumber, null, false));
	}
}
