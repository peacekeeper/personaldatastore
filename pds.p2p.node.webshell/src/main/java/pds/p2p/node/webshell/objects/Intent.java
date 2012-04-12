package pds.p2p.node.webshell.objects;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Intent extends Packet {

	private static final long serialVersionUID = -3734622557993328577L;

	private static Logger log = LoggerFactory.getLogger(Intent.class.getName());

	private String id;
	private String product;
	private String price;

	public Intent() {

		this.id = createId();
	}

	public String getId() {

		return this.id;
	}

	public void setId(String id) {

		this.id = id;
	}

	public String getProduct() {

		return this.product;
	}

	public void setProduct(String product) {

		this.product = product;
	}

	public String getPrice() {

		return this.price;
	}

	public void setPrice(String price) {

		this.price = price;
	}

	@Override
	public void fromPacket (String rawpacket) throws UnsupportedEncodingException, JSONException {

		super.fromPacket(rawpacket);

		this.fromContent(this.getContent());
	}

	public void fromContent(String content) throws JSONException, UnsupportedEncodingException {

		log.debug("content: " + content);

		JSONObject contentObject = new JSONObject(content);

		this.id = contentObject.getString("id");
		this.product = contentObject.getString("product");
		this.price = contentObject.getString("price");
	}

	public String toJSON() {

		StringBuffer json = new StringBuffer();
		json.append("{");
		json.append("\"id\":\"" + this.id.replace("\"", "\\\"") + "\"");
		json.append(",");
		json.append("\"product\":\"" + this.product.replace("\"", "\\\"") + "\"");
		json.append(",");
		json.append("\"price\":\"" + this.price.replace("\"", "\\\"") + "\"");
		json.append("}");

		return json.toString();
	}

	@Override
	public String toString() {

		return "#" + this.id + ": " + this.product + " (" + this.price + ")";
	}

	@Override
	public boolean equals(Object object) {

		if (this == object) return true;
		if (! (object instanceof Intent)) return false;

		Intent other = (Intent) object;

		if (! this.id.equals(other.id)) return false;
		if (! this.product.equals(other.product)) return false;
		if (! this.price.equals(other.price)) return false;

		return true;
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode += hashCode * 31 + this.id.hashCode();
		hashCode += hashCode * 31 + this.product.hashCode();
		hashCode += hashCode * 31 + this.price.hashCode();

		return hashCode;
	}

	private static String createId() {

		final Random random = new Random();
		final String hex = "0123456789abcdef";

		StringBuilder id = new StringBuilder();
		for (int i=0; i<8; i++) id.append(hex.charAt(random.nextInt(hex.length())));

		return id.toString();
	}
}
