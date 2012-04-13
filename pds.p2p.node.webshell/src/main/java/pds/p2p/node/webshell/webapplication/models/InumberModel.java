package pds.p2p.node.webshell.webapplication.models;

import org.apache.wicket.model.AbstractReadOnlyModel;

import pds.p2p.api.node.client.DanubeApiClient;

public class InumberModel extends AbstractReadOnlyModel<String> {

	private static final long serialVersionUID = 602197041909086712L;

	@Override
	public String getObject() {

		try {

			return DanubeApiClient.orionObject.inumber();
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}