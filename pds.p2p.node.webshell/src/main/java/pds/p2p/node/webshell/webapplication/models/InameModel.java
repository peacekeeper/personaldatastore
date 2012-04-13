package pds.p2p.node.webshell.webapplication.models;

import org.apache.wicket.model.AbstractReadOnlyModel;

import pds.p2p.api.node.client.DanubeApiClient;

public class InameModel extends AbstractReadOnlyModel<String> {

	private static final long serialVersionUID = 6399606689894790735L;

	@Override
	public String getObject() {

		try {

			return DanubeApiClient.orionObject.iname();
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}