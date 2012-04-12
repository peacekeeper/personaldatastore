package pds.p2p.node.webshell.webpages.user;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.webpages.BasePage;

public class Relations extends BasePage {

	private static final long serialVersionUID = -6000684597170356861L;

	private static Logger log = LoggerFactory.getLogger(Relations.class.getName());

	public Relations() {

		this.setTitle(this.getString("title"));

		String[] rawrelations;

		try {

			String inumber = DanubeApiClient.orionObject.inumber();
			rawrelations = DanubeApiClient.polarisObject.getRelations(inumber + "/+friend/($)");
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		ArrayList<String> relations = new ArrayList<String> (Arrays.asList(rawrelations));

		Model<ArrayList<String>> model = new Model<ArrayList<String>> (relations);

		log.debug("Loaded " + relations.size() + " relations.");

		// create and add components

		ListView<String> relationsListView = new ListView<String> ("relations", model) {

			private static final long serialVersionUID = -3384033578428885085L;

			@Override
			protected void populateItem(ListItem<String> item) {

				item.add(new RelationPanel("relation", item.getModelObject()));
			}
		};

		this.add(relationsListView);
	}
}
