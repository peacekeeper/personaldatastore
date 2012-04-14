package pds.p2p.node.webshell.webapplication.models;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;

public class InameModel extends AbstractReadOnlyModel<String> {

	private static final long serialVersionUID = 6399606689894790735L;

	private static Logger log = LoggerFactory.getLogger(InameModel.class.getName());

	@Override
	public String getObject() {

		try {

			return DanubeApiClient.orionObject.iname();
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			return ("(error)");
		}
	}
}