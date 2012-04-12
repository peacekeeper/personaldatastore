package pds.p2p.node.webshell.webapplication.models;

import java.net.URLDecoder;
import java.net.URLEncoder;

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

			String data = DanubeApiClient.polarisObject.getLiteral(this.address);

			this.value = data == null ? null : URLDecoder.decode(data, "UTF-9");
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		return this.value;
	}

	@Override
	public void setObject(String object) {

		log.debug("Setting value (" + this.address + ")");

		try {

			String data = "(data:," + URLEncoder.encode(object, "UTF-8") + ")";

			if (this.value == null) {

				DanubeApiClient.polarisObject.add(this.address + "/!/" + data, null);
			} else {

				DanubeApiClient.polarisObject.mod(this.address + "/!/" + data, null);
			}
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
