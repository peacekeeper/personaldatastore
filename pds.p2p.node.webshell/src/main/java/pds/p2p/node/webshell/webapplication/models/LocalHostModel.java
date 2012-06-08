package pds.p2p.node.webshell.webapplication.models;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;

public class LocalHostModel extends AbstractReadOnlyModel<String> {

	private static final long serialVersionUID = 3451099674928356272L;

	private static Logger log = LoggerFactory.getLogger(LocalHostModel.class.getName());

	@Override
	public String getObject() {

		try {

			return DanubeApiClient.vegaObject.localHost();
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			return ("(error)");
		}
	}
}
