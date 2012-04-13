package pds.p2p.node.webshell.objects;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Packet implements Serializable {

	private static final long serialVersionUID = 9085664239802323944L;

	private static Logger log = LoggerFactory.getLogger(Packet.class.getName());

	private String topic;
	private String ray;
	private String iname;
	private String inumber;
	private String content;
	private String timerecv;

	public Packet() {

	}

	public String getTopic() {

		return this.topic;
	}

	public void setTopic(String topic) {

		this.topic = topic;
	}

	public String getRay() {

		return this.ray;
	}

	public void setRay(String ray) {

		this.ray = ray;
	}

	public String getIname() {

		return this.iname;
	}

	public void setIname(String iname) {

		this.iname = iname;
	}

	public String getInumber() {

		return this.inumber;
	}

	public void setInumber(String inumber) {

		this.inumber = inumber;
	}

	public String getContent() {

		return this.content;
	}

	public void setContent(String content) {

		this.content = content;
	}

	public String getTimerecv() {

		return new SimpleDateFormat().format(new Date(Long.valueOf(this.timerecv)));
	}

	public void setTimerecv(String timerecv) {

		this.timerecv = timerecv;
	}

	public void fromPacket (String rawpacket) throws UnsupportedEncodingException, JSONException {

		log.debug("raw packet: " + rawpacket);

		String decodedPacket = URLDecoder.decode(rawpacket, "UTF-8");
		log.debug("decoded packet: " + decodedPacket);

		JSONObject packetObject = new JSONObject(decodedPacket);

		this.topic = packetObject.getString("topic");
		this.ray = packetObject.getString("ray");
		this.iname = packetObject.getString("iname");
		this.inumber = packetObject.getString("inumber");
		this.content = packetObject.getString("content");
		this.timerecv = packetObject.getString("timerecv");
	}

	@Override
	public String toString() {

		return this.iname + " (" + this.inumber + "): " + this.ray + " (" + this.topic + ")";
	}

	@Override
	public boolean equals(Object object) {

		if (this == object) return true;
		if (! (object instanceof Packet)) return false;

		Packet other = (Packet) object;

		if (! this.topic.equals(other.topic)) return false;
		if (! this.ray.equals(other.ray)) return false;
		if (! this.iname.equals(other.iname)) return false;
		if (! this.inumber.equals(other.inumber)) return false;
		if (! this.content.equals(other.content)) return false;
		if (! this.timerecv.equals(other.timerecv)) return false;

		return true;
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode += hashCode * 31 + this.topic.hashCode();
		hashCode += hashCode * 31 + this.ray.hashCode();
		hashCode += hashCode * 31 + this.iname.hashCode();
		hashCode += hashCode * 31 + this.inumber.hashCode();
		hashCode += hashCode * 31 + this.content.hashCode();
		hashCode += hashCode * 31 + this.timerecv.hashCode();

		return hashCode;
	}
}
