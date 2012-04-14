package pds.p2p.node.webshell.webapplication.models;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;

public class InumberModel extends AbstractReadOnlyModel<String> {

	private static final long serialVersionUID = 602197041909086712L;

	private static Logger log = LoggerFactory.getLogger(InumberModel.class.getName());

	@Override
	public String getObject() {

		try {

			return DanubeApiClient.orionObject.inumber();
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			return ("(error)");
		}
	}
}