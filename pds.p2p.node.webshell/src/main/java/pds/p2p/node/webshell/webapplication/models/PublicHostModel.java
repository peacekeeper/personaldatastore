package pds.p2p.node.webshell.webapplication.models;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;

public class PublicHostModel extends AbstractReadOnlyModel<String> {

	private static final long serialVersionUID = -8719256022328161935L;

	private static Logger log = LoggerFactory.getLogger(PublicHostModel.class.getName());

	@Override
	public String getObject() {

		try {

			return DanubeApiClient.vegaObject.publicHost();
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			return ("(error)");
		}
	}
}
