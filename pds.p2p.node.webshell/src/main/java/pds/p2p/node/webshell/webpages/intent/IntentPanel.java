package pds.p2p.node.webshell.webpages.intent;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import pds.p2p.node.webshell.objects.Intent;

public class IntentPanel extends Panel {

	private static final long serialVersionUID = 1685165705269620602L;

	public IntentPanel(String id, Intent intent) {

		super(id, new CompoundPropertyModel<Intent> (intent));

		// create and add components

		this.add(new Label("product"));
		this.add(new Label("price"));
	}
}
