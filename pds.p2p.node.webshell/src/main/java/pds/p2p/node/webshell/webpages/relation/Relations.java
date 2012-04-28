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
import pds.p2p.node.webshell.objects.Message;
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
	private String to;
	private String message;

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
		this.add(new RelationForm("relationForm", new CompoundPropertyModel<Relations> (this)));
		this.add(new MessageForm("messageForm", new CompoundPropertyModel<Relations> (this)));
	}

	private class RelationForm extends Form<Relations> {

		private static final long serialVersionUID = 4415680791046759951L;

		private TextField<String> relationTextField;

		private RelationForm(String id, IModel<Relations> model) {

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

			info(Relations.this.getString("successrelation"));
		}
	}

	private class MessageForm extends Form<Relations> {

		private static final long serialVersionUID = -3624585005960965743L;

		private TextField<String> toTextField;
		private TextField<String> messageTextField;

		private MessageForm(String id, IModel<Relations> model) {

			super(id, model);

			// create components

			this.toTextField = new TextField<String> ("to");
			this.toTextField.setLabel(new Model<String> ("To"));
			this.toTextField.setRequired(true);
			this.messageTextField = new TextField<String> ("message");
			this.messageTextField.setLabel(new Model<String> ("Message"));
			this.messageTextField.setRequired(true);

			// add components

			this.add(this.toTextField);
			this.add(this.messageTextField);
		}

		@Override
		protected void onSubmit() {

			Relations.log.debug("Sending Message.");

			try {

				String inumber = DanubeApiClient.orionObject.resolve(Relations.this.to);
				String nodeId = inumber == null ? null : DanubeApiClient.vegaObject.get("(" + inumber + ")$nodeid");

				if (nodeId == null) throw new RuntimeException("Destination not found.");
				
				Message message = new Message();
				message.setMessage(Relations.this.message);

				DanubeApiClient.vegaObject.send(nodeId, "message", message.toJSON(), null, null);
			} catch (Exception ex) {

				Relations.log.warn(ex.getMessage(), ex);
				error(Relations.this.getString("fail") + ex.getMessage());
				return;
			}

			// done

			info(Relations.this.getString("successmessage"));
		}
	}

	public String getRelation() {
		return this.relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getTo() {
		return this.to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
