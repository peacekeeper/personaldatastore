package pds.p2p.node.webshell.webapplication.models;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;

public class NodeIdModel extends AbstractReadOnlyModel<String> {

	private static final long serialVersionUID = -1004143566184261706L;

	private static Logger log = LoggerFactory.getLogger(NodeIdModel.class.getName());

	@Override
	public String getObject() {

		try {

			return DanubeApiClient.vegaObject.nodeId();
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			return ("(error)");
		}
	}
}
