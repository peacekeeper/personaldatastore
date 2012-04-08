package pds.p2p.node.webshell.webpages.vrm;

import java.util.ArrayList;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.objects.Prfp;
import pds.p2p.node.webshell.webpages.BasePage;

public class ViewPrfps extends BasePage {

	private static final long serialVersionUID = -4428962776875356738L;

	private static Logger log = LoggerFactory.getLogger(ViewPrfps.class.getName());

	public ViewPrfps() {

		this.setTitle(this.getString("title"));

		try {

			String inumber = DanubeApiClient.orionObject.inumber();
			String result = DanubeApiClient.polarisObject.get(inumber + "+prfp", "XDI/JSON");
			log.info(result);
		} catch (Exception ex) {

			ex.printStackTrace();
		}
		
		ArrayList<Prfp> prfps = new ArrayList<Prfp> ();
		prfps.add(new Prfp("chair", "USD 10"));
		prfps.add(new Prfp("food", "USD 15"));
		prfps.add(new Prfp("fun", "USD 30"));
		
		Model<ArrayList<Prfp>> model = new Model<ArrayList<Prfp>> (prfps);
		
		// create and add components

		ListView<Prfp> prfpsListView = new ListView<Prfp> ("prfps", model) {

			private static final long serialVersionUID = -3384033578428885085L;

			@Override
			protected void populateItem(ListItem<Prfp> item) {

				item.add(new PrfpPanel("prfp", item.getModelObject()));
			}
		};

		this.add(prfpsListView);
	}
}
