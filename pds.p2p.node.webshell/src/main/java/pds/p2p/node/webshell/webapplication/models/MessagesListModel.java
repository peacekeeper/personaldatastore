package pds.p2p.node.webshell.webapplication.models;

import java.util.ArrayList;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.objects.Message;

public class MessagesListModel extends AbstractReadOnlyModel<ArrayList<Message>> {

	private static final long serialVersionUID = -5737049051528392511L;

	private static Logger log = LoggerFactory.getLogger(MessagesListModel.class.getName());

	private ArrayList<Message> messages;

	@Override
	public void detach() {

		this.messages = null;
	}

	public ArrayList<Message> getObject() {

		if (this.messages != null) return this.messages;
		
		log.debug("Getting messages.");

		// get XDI data

		String[] rawpackets;

		try {

			if (! "1".equals(DanubeApiClient.orionObject.loggedin())) return new ArrayList<Message> ();
			if (! "1".equals(DanubeApiClient.vegaObject.connected())) return new ArrayList<Message> ();

			String inumber = DanubeApiClient.orionObject.inumber();
			rawpackets = DanubeApiClient.polarisObject.getLiterals(inumber + "+message", null);
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		// prepare messages

		this.messages = new ArrayList<Message> ();

		for (String rawpacket : rawpackets) {

			try {

				Message message = new Message();
				message.fromPacket(rawpacket);

				this.messages.add(message);
			} catch (Exception ex) {

				throw new RuntimeException(ex.getMessage(), ex);
			}
		}

		// done

		log.debug("Loaded " + this.messages.size() + " messages.");

		return this.messages;
	}
}
