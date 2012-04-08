package pds.p2p.node.webshell.webpages.user;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.webapplication.behaviors.DefaultFocusBehavior;
import pds.p2p.node.webshell.webpages.BasePage;
import pds.p2p.node.webshell.webpages.index.Index;

public class Login extends BasePage {

	private static final long serialVersionUID = -4706614828963732568L;

	private static Logger log = LoggerFactory.getLogger(Login.class.getName());

	private String userIdentifier;
	private String password;

	public Login() {

		this.setTitle(this.getString("title"));

		// create and add components

		this.add(new MyForm("form", new CompoundPropertyModel<Login> (this)));
	}

	private class MyForm extends Form<Login> {

		private static final long serialVersionUID = 1045735353194990548L;

		private TextField<String> identifierTextField;
		private PasswordTextField passwordTextField;

		private MyForm(String id, IModel<Login> model) {

			super(id, model);

			// create components

			this.identifierTextField = new TextField<String> ("userIdentifier");
			this.identifierTextField.setLabel(new Model<String> ("I-Name"));
			this.identifierTextField.setRequired(true);
			this.identifierTextField.add(new DefaultFocusBehavior());
			this.passwordTextField = new PasswordTextField("password");
			this.passwordTextField.setLabel(new Model<String> ("Password"));
			this.passwordTextField.setRequired(true);

			// add components

			this.add(this.identifierTextField);
			this.add(this.passwordTextField);
		}

		@Override
		protected void onSubmit() {

			Login.log.debug("Beginning Login.");

			RequestCycle requestCycle = this.getRequestCycle();

			// login to orion

			Login.log.debug("Logging in: " + Login.this.userIdentifier);

			try {

				DanubeApiClient.orionObject.login(Login.this.userIdentifier, Login.this.password);
			} catch (Exception ex) {

				log.warn(ex.getMessage(), ex);
				error(Login.this.getString("fail") + ex.getMessage());
				return;
			}

			// send user to homepage

			requestCycle.setResponsePage(Index.class);
			return;
		}
	}

	public String getPass() {
		return (this.password);
	}

	public void setPass(String pass) {
		this.password = pass;
	}

	public String getIdentifier() {
		return (this.userIdentifier);
	}

	public void setIdentifier(String identifier) {
		this.userIdentifier = identifier;
	}
}
