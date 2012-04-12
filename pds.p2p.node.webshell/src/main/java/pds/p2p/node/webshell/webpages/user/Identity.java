package pds.p2p.node.webshell.webpages.user;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.webapplication.behaviors.DefaultFocusBehavior;
import pds.p2p.node.webshell.webpages.BasePage;

public class Identity extends BasePage {

	private static final long serialVersionUID = -4706614828963732568L;

	private static Logger log = LoggerFactory.getLogger(Identity.class.getName());

	private String identifier;
	private String password;

	public Identity() {

		this.setTitle(this.getString("title"));

		// create and add components

		this.add(new LoginForm("loginForm", new CompoundPropertyModel<Identity> (this)));
		this.add(new LogoutForm("logoutForm", new CompoundPropertyModel<Identity> (this)));
	}

	private class LoginForm extends Form<Identity> {

		private static final long serialVersionUID = 1045735353194990548L;

		private TextField<String> identifierTextField;
		private PasswordTextField passwordTextField;

		private LoginForm(String id, IModel<Identity> model) {

			super(id, model);

			// create components

			this.identifierTextField = new TextField<String> ("identifier");
			this.identifierTextField.setLabel(new Model<String> ("Identifier"));
			this.identifierTextField.setRequired(true);
			this.identifierTextField.add(new DefaultFocusBehavior());
			this.passwordTextField = new PasswordTextField("password");
			this.passwordTextField.setLabel(new Model<String> ("Password"));
			this.passwordTextField.setRequired(false);

			// add components

			this.add(this.identifierTextField);
			this.add(this.passwordTextField);
		}

		@Override
		protected void onSubmit() {

			// login to orion

			Identity.log.debug("Logging in: " + Identity.this.identifier);

			try {

				DanubeApiClient.orionObject.login(Identity.this.identifier, "xxx");
			} catch (Exception ex) {

				log.warn(ex.getMessage(), ex);
				error(Identity.this.getString("fail") + ex.getMessage());
				return;
			}

			// done

			info(Identity.this.getString("loggedin"));
		}
	}

	private class LogoutForm extends Form<Identity> {

		private static final long serialVersionUID = 5165186814705755609L;

		private LogoutForm(String id, IModel<Identity> model) {

			super(id, model);
		}

		@Override
		protected void onSubmit() {

			// logout from orion

			Identity.log.debug("Logging out.");

			try {

				DanubeApiClient.orionObject.logout();
			} catch (Exception ex) {

				Identity.log.warn(ex.getMessage(), ex);
				error(Identity.this.getString("fail") + ex.getMessage());
				return;
			}

			// done

			info(Identity.this.getString("loggedout"));
		}
	}

	public String getIdentifier() {
		return (this.identifier);
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getPassword() {
		return (this.password);
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
