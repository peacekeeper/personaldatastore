package pds.p2p.node.webshell.webapplication.components;

import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;

public class PolarisLiteralModel implements IModel<String> {

	private static final long serialVersionUID = 8961480202051169216L;

	private static Logger log = LoggerFactory.getLogger(PolarisLiteralModel.class.getName());

	private String address;
	private String value;

	public PolarisLiteralModel(String address) {

		this.address = address;
		this.value = null;
	}

	@Override
	public void detach() {

		this.value = null;
	}

	@Override
	public String getObject() {

		if (this.value != null) return this.value;

		log.debug("Getting value (" + this.address + ")");

		try {

			this.value = DanubeApiClient.polarisObject.getLiteral(this.address);
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		return this.value;
	}

	@Override
	public void setObject(String object) {

		log.debug("Setting value (" + this.address + ")");

		try {

			if (this.value == null) {

				DanubeApiClient.polarisObject.add(this.address + "/!/" + object, null);
			} else {

				DanubeApiClient.polarisObject.mod(this.address + "/!/" + object, null);
			}
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
