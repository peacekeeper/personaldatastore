package pds.p2p.node.webshell.webpages.node;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.webpages.BasePage;

public class Connection extends BasePage {

	private static final long serialVersionUID = -4101890723769407353L;

	private static final String LOCAL_HOST = "172.17.1.1/";
	private static final String REMOTE_HOST_LIST = "172.17.218.193,172.17.217.183,172.17.217.243,172.17.90.193";

	protected static Logger log = LoggerFactory.getLogger(Connection.class.getName());

	public Connection() {

		this.setTitle(this.getString("title"));

		// create and add components

		this.add(new ConnectNewForm("connectNewForm", new CompoundPropertyModel<Connection> (this)));
		this.add(new ConnectExistingForm("connectExistingForm", new CompoundPropertyModel<Connection> (this)));
		this.add(new DisconnectForm("disconnectForm", new CompoundPropertyModel<Connection> (this)));
	}

	private class ConnectNewForm extends Form<Connection> {

		private static final long serialVersionUID = 7493476895327795699L;

		private ConnectNewForm(String id, IModel<Connection> model) {

			super(id, model);
		}

		@Override
		protected void onSubmit() {

			// connect to vega

			Connection.log.debug("Connecting");

			try {

				DanubeApiClient.vegaObject.connect(LOCAL_HOST, null, null);
			} catch (Exception ex) {

				log.warn(ex.getMessage(), ex);
				error(Connection.this.getString("fail") + ex.getMessage());
				return;
			}

			// done

			info(Connection.this.getString("connected"));
		}
	}

	private class ConnectExistingForm extends Form<Connection> {

		private static final long serialVersionUID = -2720901123859278741L;

		private ConnectExistingForm(String id, IModel<Connection> model) {

			super(id, model);
		}

		@Override
		protected void onSubmit() {

			// connect to vega

			Connection.log.debug("Connecting");

			try {

				DanubeApiClient.vegaObject.connectList(null, REMOTE_HOST_LIST, null);
			} catch (Exception ex) {

				log.warn(ex.getMessage(), ex);
				error(Connection.this.getString("fail") + ex.getMessage());
				return;
			}

			// done

			info(Connection.this.getString("connected"));
		}
	}

	private class DisconnectForm extends Form<Connection> {

		private static final long serialVersionUID = -6253657678202734856L;

		private DisconnectForm(String id, IModel<Connection> model) {

			super(id, model);
		}

		@Override
		protected void onSubmit() {

			// disconnect from vega

			Connection.log.debug("Disconnecting");

			try {

				DanubeApiClient.vegaObject.disconnect();
			} catch (Exception ex) {

				log.warn(ex.getMessage(), ex);
				error(Connection.this.getString("fail") + ex.getMessage());
				return;
			}

			// done

			info(Connection.this.getString("disconnected"));
		}
	}
}
