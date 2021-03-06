package pds.p2p.api.vega.comm;

import rice.p2p.commonapi.Message;

public final class VegaMessage implements Message {

	private static final long serialVersionUID = 1777369729763882136L;

	private String ray;
	private String iname;
	private String inumber;
	private String nonce;
	private String content;
	private String signature;
	private String hashcash;
	private String flags;
	private String extension;
	private String timesent;
	private String timerecv;

	public VegaMessage(String ray, String iname, String inumber, String nonce, String content, String signature, String hashcash, String flags, String extension, String timesent, String timerecv) {

		this.ray = ray;
		this.iname = iname;
		this.inumber = inumber;
		this.nonce = nonce;
		this.content = content;
		this.signature = signature;
		this.hashcash = hashcash;
		this.flags = flags;
		this.extension = extension;
		this.timesent = timesent;
		this.timerecv = timerecv;
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

	public String getNonce() {

		return this.nonce;
	}

	public void setNonce(String nonce) {

		this.nonce = nonce;
	}

	public String getContent() {

		return this.content;
	}

	public void setContent(String content) {

		this.content = content;
	}

	public String getSignature() {

		return this.signature;
	}

	public void setSignature(String signature) {

		this.signature = signature;
	}

	public String getHashcash() {

		return this.hashcash;
	}

	public void setHashcash(String hashcash) {

		this.hashcash = hashcash;
	}

	public String getFlags() {

		return this.flags;
	}

	public void setFlags(String flags) {

		this.flags = flags;
	}

	public String getExtension() {

		return this.extension;
	}

	public void setExtension(String extension) {

		this.extension = extension;
	}

	public String getTimesent() {

		return this.timesent;
	}

	public void setTimesent(String timesent) {

		this.timesent = timesent;
	}

	public String getTimerecv() {

		return this.timerecv;
	}

	public void setTimerecv(String timerecv) {

		this.timerecv = timerecv;
	}

	public int getPriority() {

		return Message.LOW_PRIORITY;
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof VegaMessage)) return false;
		if (object == this) return true;

		VegaMessage other = (VegaMessage) object;

		if (this.ray == null && other.ray != null) return false;
		if (this.ray != null && ! this.ray.equals(other.ray)) return false;

		if (this.iname == null && other.iname != null) return false;
		if (this.iname != null && ! this.iname.equals(other.iname)) return false;

		if (this.inumber == null && other.inumber != null) return false;
		if (this.inumber != null && ! this.inumber.equals(other.inumber)) return false;

		if (this.nonce == null && other.nonce != null) return false;
		if (this.nonce != null && ! this.nonce.equals(other.nonce)) return false;

		if (this.content == null && other.content != null) return false;
		if (this.content != null && ! this.content.equals(other.content)) return false;

		if (this.signature == null && other.signature != null) return false;
		if (this.signature != null && ! this.signature.equals(other.signature)) return false;

		if (this.hashcash == null && other.hashcash != null) return false;
		if (this.hashcash != null && ! this.hashcash.equals(other.hashcash)) return false;

		if (this.flags == null && other.flags != null) return false;
		if (this.flags != null && ! this.flags.equals(other.flags)) return false;

		if (this.extension == null && other.extension != null) return false;
		if (this.extension != null && ! this.extension.equals(other.extension)) return false;

		if (this.timesent == null && other.timesent != null) return false;
		if (this.timesent != null && ! this.timesent.equals(other.timesent)) return false;

		if (this.timerecv == null && other.timerecv != null) return false;
		if (this.timerecv != null && ! this.timerecv.equals(other.timerecv)) return false;

		return true;
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + (this.ray == null ? 0 : this.ray.hashCode());
		hashCode = (hashCode * 31) + (this.iname == null ? 0 : this.iname.hashCode());
		hashCode = (hashCode * 31) + (this.inumber == null ? 0 : this.inumber.hashCode());
		hashCode = (hashCode * 31) + (this.nonce == null ? 0 : this.nonce.hashCode());
		hashCode = (hashCode * 31) + (this.content == null ? 0 : this.content.hashCode());
		hashCode = (hashCode * 31) + (this.signature == null ? 0 : this.signature.hashCode());
		hashCode = (hashCode * 31) + (this.hashcash == null ? 0 : this.hashcash.hashCode());
		hashCode = (hashCode * 31) + (this.flags == null ? 0 : this.flags.hashCode());
		hashCode = (hashCode * 31) + (this.extension == null ? 0 : this.extension.hashCode());
		hashCode = (hashCode * 31) + (this.timesent == null ? 0 : this.timesent.hashCode());
		hashCode = (hashCode * 31) + (this.timerecv == null ? 0 : this.timerecv.hashCode());

		return hashCode;
	}

	@Override
	public String toString() {

		return this.content == null ? null : this.content.toString();
	}
}
