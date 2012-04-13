package pds.p2p.node.webshell.webapplication.models;

import org.apache.wicket.model.AbstractReadOnlyModel;

import pds.p2p.api.node.client.DanubeApiClient;

public class NeighborsModel extends AbstractReadOnlyModel<String> {

	private static final long serialVersionUID = -809438420430966011L;

	@Override
	public String getObject() {

		try {

			if ("1".equals(DanubeApiClient.vegaObject.connected())) {

				return Integer.toString(DanubeApiClient.vegaObject.lookupNeighbors("99").length);
			} else {

				return null;
			}
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
