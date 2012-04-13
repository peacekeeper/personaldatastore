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
	private String xdiUri;
	private String object;

	public PolarisLiteralModel(String address, String xdiUri) {

		this.address = address;
		this.xdiUri = xdiUri;
		this.object = null;
	}

	@Override
	public void detach() {

		this.object = null;
	}

	@Override
	public String getObject() {

		if (this.object != null) return this.object;

		log.debug("Getting value (" + this.address + ")");

		try {

			String data = DanubeApiClient.polarisObject.getLiteral(this.address, this.xdiUri);

			this.object = data == null ? null : URLDecoder.decode(data, "UTF-8");
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		return this.object;
	}

	@Override
	public void setObject(String object) {

		log.debug("Setting value (" + this.address + ")");

		try {

			if (object != null) {

				String data = "(data:," + URLEncoder.encode(object, "UTF-8") + ")";

				if (this.object == null) {

					DanubeApiClient.polarisObject.add(this.address + "/!/" + data, this.xdiUri, null);
					this.object = object;
				} else {

					DanubeApiClient.polarisObject.mod(this.address + "/!/" + data, this.xdiUri, null);
					this.object = object;
				}
			} else {

				DanubeApiClient.polarisObject.del(this.address, this.xdiUri, null);
				this.object = null;
			}
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
