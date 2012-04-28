package pds.p2p.node.webshell.objects;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Message extends Packet {

	private static final long serialVersionUID = 3053001933932900730L;

	private static Logger log = LoggerFactory.getLogger(Message.class.getName());

	private String messageId;
	private String message;

	public Message() {

		this.messageId = createId();
	}

	public String getMessageId() {

		return this.messageId;
	}

	public void setMessageId(String messageId) {

		this.messageId = messageId;
	}

	public String getMessage() {

		return this.message;
	}

	public void setMessage(String message) {

		this.message = message;
	}

	@Override
	public void fromPacket (String rawpacket) throws UnsupportedEncodingException, JSONException {

		super.fromPacket(rawpacket);

		this.fromContent(this.getContent());
	}

	public void fromContent(String content) throws JSONException, UnsupportedEncodingException {

		log.debug("content: " + content);

		JSONObject contentObject = new JSONObject(content);

		this.messageId = contentObject.has("messageid") ? contentObject.getString("messageid") : null;
		this.message = contentObject.has("message") ? contentObject.getString("message") : null;
	}

	public String toJSON() {

		StringBuffer json = new StringBuffer();
		json.append("{");
		json.append("\"messageid\":\"" + this.messageId.replace("\"", "\\\"") + "\"");
		json.append(",");
		json.append("\"message\":\"" + this.message.replace("\"", "\\\"") + "\"");
		json.append("}");

		return json.toString();
	}

	@Override
	public String toString() {

		return "#" + this.messageId + ": " + this.message;
	}

	@Override
	public boolean equals(Object object) {

		if (this == object) return true;
		if (! (object instanceof Message)) return false;

		Message other = (Message) object;

		if (! this.messageId.equals(other.messageId)) return false;
		if (! this.message.equals(other.message)) return false;

		return true;
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode += hashCode * 31 + this.messageId.hashCode();
		hashCode += hashCode * 31 + this.message.hashCode();

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
