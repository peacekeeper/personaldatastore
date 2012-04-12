package pds.p2p.node.webshell.webpages.user;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.webpages.BasePage;
import pds.p2p.node.webshell.webpages.index.Index;

public class Connection extends BasePage {

	private static final long serialVersionUID = -4101890723769407353L;

	protected static Logger log = LoggerFactory.getLogger(Connection.class.getName());

	public Connection() {

		this.setTitle(this.getString("title"));

		// create and add components

		this.add(new ConnectForm("connectForm", new CompoundPropertyModel<Connection> (this)));
		this.add(new DisconnectForm("disconnectForm", new CompoundPropertyModel<Connection> (this)));
	}

	private class ConnectForm extends Form<Connection> {

		private static final long serialVersionUID = -2720901123859278741L;

		private ConnectForm(String id, IModel<Connection> model) {

			super(id, model);
		}

		@Override
		protected void onSubmit() {

			Connection.log.debug("Beginning Login.");

			RequestCycle requestCycle = this.getRequestCycle();

			// connect to vega

			Connection.log.debug("Connecting");

			try {

				DanubeApiClient.vegaObject.connect(null, null, null, null);
			} catch (Exception ex) {

				log.warn(ex.getMessage(), ex);
				error(Connection.this.getString("fail") + ex.getMessage());
				return;
			}

			// send user to homepage

			requestCycle.setResponsePage(Index.class);
			return;
		}
	}

	private class DisconnectForm extends Form<Connection> {

		private static final long serialVersionUID = -6253657678202734856L;

		private DisconnectForm(String id, IModel<Connection> model) {

			super(id, model);
		}

		@Override
		protected void onSubmit() {

			RequestCycle requestCycle = this.getRequestCycle();

			// disconnect from vega

			Connection.log.debug("Disconnecting");

			try {

				DanubeApiClient.vegaObject.disconnect();
			} catch (Exception ex) {

				log.warn(ex.getMessage(), ex);
				error(Connection.this.getString("fail") + ex.getMessage());
				return;
			}

			// send user to homepage

			requestCycle.setResponsePage(Index.class);
			return;
		}
	}
}
