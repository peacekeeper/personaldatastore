package pds.p2p.node.webshell.webpages.relation;

import java.util.ArrayList;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.objects.Relation;
import pds.p2p.node.webshell.webapplication.WebShellAuthorizationStrategy.NeedConnectedPage;
import pds.p2p.node.webshell.webapplication.WebShellAuthorizationStrategy.NeedLoggedInPage;
import pds.p2p.node.webshell.webapplication.behaviors.DefaultFocusBehavior;
import pds.p2p.node.webshell.webapplication.models.RelationsListModel;
import pds.p2p.node.webshell.webpages.BasePage;

public class Relations extends BasePage implements NeedLoggedInPage, NeedConnectedPage {

	private static final long serialVersionUID = -6000684597170356861L;

	private static Logger log = LoggerFactory.getLogger(Relations.class.getName());

	private String relation;

	public Relations() {

		this.setTitle(this.getString("title"));

		IModel<ArrayList<Relation>> model = new RelationsListModel();

		// create and add components

		ListView<Relation> relationsListView = new ListView<Relation> ("relations", model) {

			private static final long serialVersionUID = -6705504072982616125L;

			@Override
			protected void populateItem(ListItem<Relation> item) {

				item.add(new RelationPanel("relation", item.getModelObject()));
			}
		};

		this.add(relationsListView);
		this.add(new MyForm("form", new CompoundPropertyModel<Relations> (this)));
	}

	private class MyForm extends Form<Relations> {

		private static final long serialVersionUID = 4415680791046759951L;

		private TextField<String> relationTextField;

		private MyForm(String id, IModel<Relations> model) {

			super(id, model);

			// create components

			this.relationTextField = new TextField<String> ("relation");
			this.relationTextField.setLabel(new Model<String> ("Relation"));
			this.relationTextField.setRequired(true);
			this.relationTextField.add(new DefaultFocusBehavior());

			// add components

			this.add(this.relationTextField);
		}

		@Override
		protected void onSubmit() {

			Relations.log.debug("Creating Relation.");

			try {

				String inumber = DanubeApiClient.orionObject.inumber();
				DanubeApiClient.polarisObject.add(inumber + "/+friend/" + Relations.this.relation, null, null);
			} catch (Exception ex) {

				Relations.log.warn(ex.getMessage(), ex);
				error(Relations.this.getString("fail") + ex.getMessage());
				return;
			}

			// done

			info(Relations.this.getString("success"));
		}
	}

	public String getRelation() {
		return this.relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}
}
