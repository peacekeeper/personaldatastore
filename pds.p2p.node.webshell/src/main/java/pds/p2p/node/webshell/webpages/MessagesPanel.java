package pds.p2p.node.webshell.webpages;

import java.util.ArrayList;

import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.time.Duration;

import pds.p2p.node.webshell.objects.Message;
import pds.p2p.node.webshell.webapplication.models.MessagesListModel;

public class MessagesPanel extends Panel {

	private static final long serialVersionUID = -3956079314376621130L;

	public MessagesPanel(String id) {

		super(id);

		IModel<ArrayList<Message>> model = new MessagesListModel();

		// create and add components

		ListView<Message> messagesListView = new ListView<Message> ("messages", model) {

			private static final long serialVersionUID = 5916474908563392888L;

			@Override
			protected void populateItem(ListItem<Message> item) {

				item.add(new MessagePanel("message", item.getModelObject()));
			}
		};

		this.add(messagesListView);

		this.setOutputMarkupId(true);
		this.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
	}
}
