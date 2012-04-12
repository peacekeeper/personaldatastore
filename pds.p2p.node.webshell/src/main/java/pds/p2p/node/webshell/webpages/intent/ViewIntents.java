package pds.p2p.node.webshell.webpages.intent;

import java.util.ArrayList;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.objects.Intent;
import pds.p2p.node.webshell.webpages.BasePage;

public class ViewIntents extends BasePage {

	private static final long serialVersionUID = -4428962776875356738L;

	private static Logger log = LoggerFactory.getLogger(ViewIntents.class.getName());

	public ViewIntents() {

		this.setTitle(this.getString("title"));

		String[] rawpackets;

		try {

			String inumber = DanubeApiClient.orionObject.inumber();
			rawpackets = DanubeApiClient.polarisObject.getLiterals(inumber + "+intent");
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		ArrayList<Intent> intents = new ArrayList<Intent> ();

		for (String rawpacket : rawpackets) {

			try {

				Intent intent = new Intent();
				intent.fromPacket(rawpacket);

				intents.add(intent);
			} catch (Exception ex) {

				throw new RuntimeException(ex.getMessage(), ex);
			}
		}

		Model<ArrayList<Intent>> model = new Model<ArrayList<Intent>> (intents);

		log.debug("Loaded " + intents.size() + " intents.");

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
