package pds.p2p.node.webshell.webpages.intent;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.objects.Intent;

public class IntentPanel extends Panel {

	private static final long serialVersionUID = 1685165705269620602L;

	public IntentPanel(String id, Intent intent) {

		super(id, new CompoundPropertyModel<Intent> (intent));

		// create and add components

		this.add(new Label("iname"));
		this.add(new Label("product"));
		this.add(new Label("price"));
		this.add(new Label("timerecv"));
		this.add(new Link<String> ("delete") {

			private static final long serialVersionUID = -8056799612786355435L;

			public void onClick() {

				Intent intent = (Intent) IntentPanel.this.getDefaultModelObject();

				try {

					String inumber = DanubeApiClient.orionObject.inumber();
					DanubeApiClient.polarisObject.del(inumber + "+intent" + "!" + intent.getId(), null, null);
				} catch (Exception ex) {

					throw new RuntimeException(ex.getMessage(), ex);
				}
			}
		});
	}
}
