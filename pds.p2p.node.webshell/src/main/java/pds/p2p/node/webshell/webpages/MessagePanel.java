package pds.p2p.node.webshell.webpages;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.objects.Message;

public class MessagePanel extends Panel {

	private static final long serialVersionUID = -884151286598968708L;

	public MessagePanel(String id, Message message) {

		super(id, new CompoundPropertyModel<Message> (message));

		// create and add components

		this.add(new Label("iname"));
		this.add(new Label("message"));
		this.add(new Link<String> ("delete") {

			private static final long serialVersionUID = -8056799612786355435L;

			public void onClick() {

				Message message = (Message) MessagePanel.this.getDefaultModelObject();

				try {

					String inumber = DanubeApiClient.orionObject.inumber();
					DanubeApiClient.polarisObject.del(inumber + "+message" + "!" + message.getMessageId(), null, null);
				} catch (Exception ex) {

					throw new RuntimeException(ex.getMessage(), ex);
				}
			}
		});
	}
}
