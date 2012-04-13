package pds.p2p.node.webshell.webapplication.models;

import org.apache.wicket.model.AbstractReadOnlyModel;

import pds.p2p.api.node.client.DanubeApiClient;

public class NodeIdModel extends AbstractReadOnlyModel<String> {

	private static final long serialVersionUID = -1004143566184261706L;

	@Override
	public String getObject() {

		try {

			return DanubeApiClient.vegaObject.nodeId();
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
