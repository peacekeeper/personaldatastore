package pds.p2p.api.vega.comm;

import rice.p2p.scribe.ScribeContent;

public class VegaScribeContent implements ScribeContent {

	private static final long serialVersionUID = -7010995036832648049L;

	private String topic;
	private String ray;
	private String iname;
	private String inumber;
	private String nonce;
	private String content;
	private String signature;
	private String hashcash;
	private String flags;
	private String extension;

	public VegaScribeContent(String topic, String ray, String iname, String inumber, String nonce, String content, String signature, String hashcash, String flags, String extension) {

		this.topic = topic;
		this.ray = ray;
		this.iname = iname;
		this.inumber = inumber;
		this.nonce = nonce;
		this.content = content;
		this.signature = signature;
		this.hashcash = hashcash;
		this.flags = flags;
		this.extension = extension;
	}

	public String getTopic() {

		return(this.topic);
	}

	public String getRay() {

		return(this.ray);
	}

	public String getIname() {

		return(this.iname);
	}

	public String getInumber() {

		return(this.inumber);
	}

	public String getNonce() {

		return(this.nonce);
	}

	public String getContent() {

		return(this.content);
	}

	public String getSignature() {

		return(this.signature);
	}

	public String getHashcash() {

		return(this.hashcash);
	}

	public String getFlags() {

		return(this.flags);
	}

	public String getExtension() {

		return(this.extension);
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof VegaScribeContent)) return(false);
		if (object == this) return(true);

		VegaScribeContent other = (VegaScribeContent) object;

		if (this.topic == null && other.topic != null) return(false);
		if (this.topic != null && ! this.topic.equals(other.topic)) return(false);

		if (this.ray == null && other.ray != null) return(false);
		if (this.ray != null && ! this.ray.equals(other.ray)) return(false);

		if (this.iname == null && other.iname != null) return(false);
		if (this.iname != null && ! this.iname.equals(other.iname)) return(false);

		if (this.inumber == null && other.inumber != null) return(false);
		if (this.inumber != null && ! this.inumber.equals(other.inumber)) return(false);

		if (this.nonce == null && other.nonce != null) return(false);
		if (this.nonce != null && ! this.nonce.equals(other.nonce)) return(false);

		if (this.content == null && other.content != null) return(false);
		if (this.content != null && ! this.content.equals(other.content)) return(false);

		if (this.signature == null && other.signature != null) return(false);
		if (this.signature != null && ! this.signature.equals(other.signature)) return(false);

		if (this.hashcash == null && other.hashcash != null) return(false);
		if (this.hashcash != null && ! this.hashcash.equals(other.hashcash)) return(false);

		if (this.flags == null && other.flags != null) return(false);
		if (this.flags != null && ! this.flags.equals(other.flags)) return(false);

		if (this.extension == null && other.extension != null) return(false);
		if (this.extension != null && ! this.extension.equals(other.extension)) return(false);

		return(true);
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + (this.topic == null ? 0 : this.topic.hashCode());
		hashCode = (hashCode * 31) + (this.ray == null ? 0 : this.ray.hashCode());
		hashCode = (hashCode * 31) + (this.iname == null ? 0 : this.iname.hashCode());
		hashCode = (hashCode * 31) + (this.inumber == null ? 0 : this.inumber.hashCode());
		hashCode = (hashCode * 31) + (this.nonce == null ? 0 : this.nonce.hashCode());
		hashCode = (hashCode * 31) + (this.content == null ? 0 : this.content.hashCode());
		hashCode = (hashCode * 31) + (this.signature == null ? 0 : this.signature.hashCode());
		hashCode = (hashCode * 31) + (this.hashcash == null ? 0 : this.hashcash.hashCode());
		hashCode = (hashCode * 31) + (this.flags == null ? 0 : this.flags.hashCode());
		hashCode = (hashCode * 31) + (this.extension == null ? 0 : this.extension.hashCode());

		return(hashCode);
	}

	@Override
	public String toString() {

		return(this.content == null ? null : this.content.toString());
	}
}
