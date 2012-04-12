package pds.p2p.node.webshell.webpages.user;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class RelationPanel extends Panel {

	private static final long serialVersionUID = 1685165705269620602L;

	public RelationPanel(String id, String relation) {

		super(id);

		// create and add components

		this.add(new Label("relation", new Model<String> (relation)));
	}
}
