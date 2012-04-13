package pds.p2p.node.webshell.webpages.intent;

import java.util.ArrayList;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;

import pds.p2p.node.webshell.objects.Intent;
import pds.p2p.node.webshell.webapplication.WebShellAuthorizationStrategy.NeedConnectedPage;
import pds.p2p.node.webshell.webapplication.WebShellAuthorizationStrategy.NeedLoggedInPage;
import pds.p2p.node.webshell.webapplication.models.IntentsListModel;
import pds.p2p.node.webshell.webpages.BasePage;

public class ViewIntents extends BasePage implements NeedLoggedInPage, NeedConnectedPage {

	private static final long serialVersionUID = -4428962776875356738L;

	public ViewIntents() {

		this.setTitle(this.getString("title"));

		IModel<ArrayList<Intent>> model = new IntentsListModel();

		// create and add components

		ListView<Intent> intentsListView = new ListView<Intent> ("intents", model) {

			private static final long serialVersionUID = -3384033578428885085L;

			@Override
			protected void populateItem(ListItem<Intent> item) {

				item.add(new IntentPanel("intent", item.getModelObject()));
			}
		};

		this.add(intentsListView);
	}
}
