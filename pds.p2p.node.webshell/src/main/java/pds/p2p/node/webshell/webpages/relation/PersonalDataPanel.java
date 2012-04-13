package pds.p2p.node.webshell.webpages.relation;

import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import pds.p2p.node.webshell.webapplication.models.PolarisLiteralModel;

public class PersonalDataPanel extends Panel {

	private static final long serialVersionUID = 6948747841504074012L;

	public PersonalDataPanel(String id, String inumber, String xdiUri, boolean readonly) {

		super(id);

		// create and add components

		if (readonly) {

			this.add(new Label("name", new PolarisLiteralModel(inumber + "+name", xdiUri)));
			this.add(new Label("email", new PolarisLiteralModel(inumber + "+email", xdiUri)));
			this.add(new Label("country", new PolarisLiteralModel(inumber + "+country", xdiUri)));
		} else {

			this.add(new MyAjaxEditableLabel<String> ("name", new PolarisLiteralModel(inumber + "+name", xdiUri)));
			this.add(new MyAjaxEditableLabel<String> ("email", new PolarisLiteralModel(inumber + "+email", xdiUri)));
			this.add(new MyAjaxEditableLabel<String> ("country", new PolarisLiteralModel(inumber + "+country", xdiUri)));
		}
	}

	private static class MyAjaxEditableLabel<T> extends AjaxEditableLabel<T> {

		private static final long serialVersionUID = -1687616091839360654L;

		public MyAjaxEditableLabel(String id, IModel<T> model) {

			super(id, model);
		}

		@Override
		protected String defaultNullLabel() {

			return "(... click here to edit ...)";
		}
	}
}
