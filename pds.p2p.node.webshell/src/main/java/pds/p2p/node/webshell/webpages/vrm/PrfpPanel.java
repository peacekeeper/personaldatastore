package pds.p2p.node.webshell.webpages.vrm;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import pds.p2p.node.webshell.objects.Prfp;

public class PrfpPanel extends Panel {

	private static final long serialVersionUID = 1685165705269620602L;

	public PrfpPanel(String id, Prfp prfp) {

		super(id, new CompoundPropertyModel<Prfp> (prfp));

		// create and add components

		this.add(new Label("product"));
		this.add(new Label("price"));
	}
}
