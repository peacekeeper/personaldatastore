package pds.p2p.node.webshell.webpages.relation;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.objects.Relation;
import pds.p2p.node.webshell.webapplication.components.NullValueLabel;

public class RelationPanel extends Panel {

	private static final long serialVersionUID = 1685165705269620602L;

	private PersonalDataPanel personalDataPanel;

	public RelationPanel(String id, Relation relation) {

		super(id, new CompoundPropertyModel<Relation> (relation));

		this.setOutputMarkupId(true);

		// create and add components

		this.personalDataPanel = new PersonalDataPanel("personalData", relation.getInumber(), relation.getXdiUri(), true);
		this.personalDataPanel.setVisible(false);

		this.add(new NullValueLabel("iname", "(not found)"));
		this.add(new NullValueLabel("inumber", "(not found)"));
		this.add(new NullValueLabel("nodeId", "(not found)"));
		this.add(new NullValueLabel("xdiUri", "(not found)"));
		this.add(new Link<String> ("delete") {

			private static final long serialVersionUID = -8056799612786355435L;

			public void onClick() {

				Relation relation = (Relation) RelationPanel.this.getDefaultModelObject();

				try {

					String inumber = DanubeApiClient.orionObject.inumber();
					DanubeApiClient.polarisObject.del(inumber + "/+friend/" + relation.getIname(), null, null);
				} catch (Exception ex) {

					throw new RuntimeException(ex.getMessage(), ex);
				}
			}
		});
		this.add(new AjaxFallbackLink<String> ("togglePersonalData") {

			private static final long serialVersionUID = 10235818828214869L;

			public void onClick(AjaxRequestTarget target) {

				if (RelationPanel.this.personalDataPanel.isVisible()) {

					RelationPanel.this.personalDataPanel.setVisible(false);
				} else {

					RelationPanel.this.personalDataPanel.setVisible(true);
				}

				target.add(RelationPanel.this);
			}
		});
		this.add(this.personalDataPanel);
	}
}
