package pds.p2p.node.webshell.webpages.relation;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.objects.Relation;

public class RelationPanel extends Panel {

	private static final long serialVersionUID = 1685165705269620602L;

	public RelationPanel(String id, Relation relation) {

		super(id, new CompoundPropertyModel<Relation> (relation));

		// create and add components

		this.add(new Label("iname"));
		this.add(new Label("inumber"));
		this.add(new Label("nodeId"));
		this.add(new Link<String> ("delete") {

			private static final long serialVersionUID = -8056799612786355435L;

			public void onClick() {

				Relation relation = (Relation) RelationPanel.this.getDefaultModelObject();

				try {

					String inumber = DanubeApiClient.orionObject.inumber();
					DanubeApiClient.polarisObject.del(inumber + "/+friend/" + relation.getIname(), null);
				} catch (Exception ex) {

					throw new RuntimeException(ex.getMessage(), ex);
				}
			}
		});
	}
}
