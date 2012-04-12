package pds.p2p.node.webshell.webpages.intent;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.objects.Intent;
import pds.p2p.node.webshell.webapplication.behaviors.DefaultFocusBehavior;
import pds.p2p.node.webshell.webpages.BasePage;

public class CreateIntent extends BasePage {

	private static final long serialVersionUID = -4428962776875356738L;

	private static Logger log = LoggerFactory.getLogger(CreateIntent.class.getName());

	private String product;
	private String price;

	public CreateIntent() {

		this.setTitle(this.getString("title"));

		// create and add components

		this.add(new MyForm("form", new CompoundPropertyModel<CreateIntent> (this)));
	}

	private class MyForm extends Form<CreateIntent> {

		private static final long serialVersionUID = 6565821013414740274L;

		private TextField<String> productTextField;
		private TextField<String> priceTextField;

		private MyForm(String id, IModel<CreateIntent> model) {

			super(id, model);

			// create components

			this.productTextField = new TextField<String> ("product");
			this.productTextField.setLabel(new Model<String> ("Product"));
			this.productTextField.setRequired(true);
			this.productTextField.add(new DefaultFocusBehavior());
			this.priceTextField = new TextField<String> ("price");
			this.priceTextField.setLabel(new Model<String> ("Price"));
			this.priceTextField.setRequired(true);

			// add components

			this.add(this.productTextField);
			this.add(this.priceTextField);
		}

		@Override
		protected void onSubmit() {

			CreateIntent.log.debug("Issuing Intent.");

			try {

				Intent intent = new Intent(CreateIntent.this.product, CreateIntent.this.price);

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

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
}
