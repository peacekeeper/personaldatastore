package pds.p2p.node.webshell.webpages.user;

import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.webapplication.components.PolarisLiteralModel;
import pds.p2p.node.webshell.webpages.BasePage;

public class PersonalData extends BasePage {

	private static final long serialVersionUID = -7620101858531404897L;

	private String name;
	private String email;
	private String country;

	public PersonalData() {

		this.setTitle(this.getString("title"));

		// create and add components

		String inumber;

		try {

			inumber = DanubeApiClient.orionObject.inumber();
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}

		this.add(new AjaxEditableLabel<String> ("name", new PolarisLiteralModel(inumber + "+name")));
		this.add(new AjaxEditableLabel<String> ("email", new PolarisLiteralModel(inumber + "+email")));
		this.add(new AjaxEditableLabel<String> ("country", new PolarisLiteralModel(inumber + "+country")));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}
