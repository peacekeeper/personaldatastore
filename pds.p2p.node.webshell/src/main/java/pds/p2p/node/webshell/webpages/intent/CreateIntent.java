package pds.p2p.node.webshell.webpages.intent;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.objects.Intent;
import pds.p2p.node.webshell.webapplication.WebShellAuthorizationStrategy.NeedConnectedPage;
import pds.p2p.node.webshell.webapplication.WebShellAuthorizationStrategy.NeedLoggedInPage;
import pds.p2p.node.webshell.webpages.BasePage;

public class CreateIntent extends BasePage implements NeedLoggedInPage, NeedConnectedPage {

	private static final long serialVersionUID = -4428962776875356738L;

	private static Logger log = LoggerFactory.getLogger(CreateIntent.class.getName());

	private String item;
	private String price;

	public CreateIntent() {

		this.setTitle(this.getString("title"));

		// create and add components

		this.add(new MyForm("form", new CompoundPropertyModel<CreateIntent> (this)));
	}

	private class MyForm extends Form<CreateIntent> {

		private static final long serialVersionUID = 6565821013414740274L;

		private RadioGroup<String> itemRadioGroup;
		private TextField<String> priceTextField;

		private MyForm(String id, IModel<CreateIntent> model) {

			super(id, model);

			// create components

			this.itemRadioGroup = new RadioGroup<String> ("item");
			this.itemRadioGroup.setLabel(new Model<String> ("Item"));
			this.itemRadioGroup.setRequired(true);
			this.itemRadioGroup.add(new Radio<String> ("item1", new Model<String> ("1")));
			this.itemRadioGroup.add(new Radio<String> ("item2", new Model<String> ("2")));
			this.itemRadioGroup.add(new Radio<String> ("item3", new Model<String> ("3")));
			this.itemRadioGroup.add(new Radio<String> ("item4", new Model<String> ("4")));
			this.itemRadioGroup.add(new Radio<String> ("item5", new Model<String> ("5")));
			this.itemRadioGroup.add(new Radio<String> ("item6", new Model<String> ("6")));
			this.itemRadioGroup.add(new Radio<String> ("item7", new Model<String> ("7")));
			this.priceTextField = new TextField<String> ("price");
			this.priceTextField.setLabel(new Model<String> ("Price"));
			this.priceTextField.setRequired(true);

			// add components

			this.add(this.itemRadioGroup);
			this.add(this.priceTextField);
		}

		@Override
		protected void onSubmit() {

			CreateIntent.log.debug("Issuing Intent.");

			try {

				Intent intent = new Intent();
				intent.setItem(CreateIntent.this.item);
				intent.setPrice(CreateIntent.this.price);

				DanubeApiClient.vegaObject.multicast("intent", "intent", intent.toJSON(), null, null);
			} catch (Exception ex) {

				log.warn(ex.getMessage(), ex);
				error(CreateIntent.this.getString("fail") + ex.getMessage());
				return;
			}

			// done

			info(CreateIntent.this.getString("success"));
		}
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
}
