package pds.p2p.node.webshell.webpages.relation;

import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import pds.p2p.node.webshell.webapplication.components.NullValueLabel;
import pds.p2p.node.webshell.webapplication.models.PolarisLiteralModel;

public class PersonalDataPanel extends Panel {

	private static final long serialVersionUID = 6948747841504074012L;

	public PersonalDataPanel(String id, String inumber, String xdiUri, boolean readonly) {

		super(id);

		// create and add components

		if (readonly) {

			this.add(new NullValueLabel("name", new PolarisLiteralModel(inumber + "+name", xdiUri), "(none)"));
			this.add(new NullValueLabel("address", new PolarisLiteralModel(inumber + "+address", xdiUri), "(none)"));
			this.add(new NullValueLabel("country", new PolarisLiteralModel(inumber + "+country", xdiUri), "(none)"));
			this.add(new NullValueLabel("email", new PolarisLiteralModel(inumber + "+email", xdiUri), "(none)"));
			this.add(new NullValueLabel("gender", new PolarisLiteralModel(inumber + "+gender", xdiUri), "(none)"));
			this.add(new NullValueLabel("dateOfBirth", new PolarisLiteralModel(inumber + "+dateOfBirth", xdiUri), "(none)"));
		} else {

			this.add(new MyAjaxEditableLabel<String> ("name", new PolarisLiteralModel(inumber + "+name", xdiUri)));
			this.add(new MyAjaxEditableLabel<String> ("address", new PolarisLiteralModel(inumber + "+address", xdiUri)));
			this.add(new MyAjaxEditableLabel<String> ("country", new PolarisLiteralModel(inumber + "+country", xdiUri)));
			this.add(new MyAjaxEditableLabel<String> ("email", new PolarisLiteralModel(inumber + "+email", xdiUri)));
			this.add(new MyAjaxEditableLabel<String> ("gender", new PolarisLiteralModel(inumber + "+gender", xdiUri)));
			this.add(new MyAjaxEditableLabel<String> ("dateOfBirth", new PolarisLiteralModel(inumber + "+dateOfBirth", xdiUri)));
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
