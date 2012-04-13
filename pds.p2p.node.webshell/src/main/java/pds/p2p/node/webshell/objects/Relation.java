package pds.p2p.node.webshell.objects;

import java.io.Serializable;

public class Relation implements Serializable {

	private static final long serialVersionUID = 1385227821281210771L;

	private String iname;
	private String inumber;
	private String nodeId;
	private String xdiUri;

	public Relation() {

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

	public String getNodeId() {

		return this.nodeId;
	}

	public void setNodeId(String nodeId) {

		this.nodeId = nodeId;
	}

	public String getXdiUri() {

		return this.xdiUri;
	}

	public void setXdiUri(String xdiUri) {

		this.xdiUri = xdiUri;
	}

	@Override
	public String toString() {

		return this.iname + " (" + this.inumber + "): " + this.nodeId + " / " + this.xdiUri;
	}

	@Override
	public boolean equals(Object object) {

		if (this == object) return true;
		if (! (object instanceof Relation)) return false;

		Relation other = (Relation) object;

		if (! this.iname.equals(other.iname)) return false;
		if (! this.inumber.equals(other.inumber)) return false;
		if (! this.nodeId.equals(other.nodeId)) return false;
		if (! this.xdiUri.equals(other.xdiUri)) return false;

		return true;
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode += hashCode * 31 + this.iname.hashCode();
		hashCode += hashCode * 31 + this.inumber.hashCode();
		hashCode += hashCode * 31 + this.nodeId.hashCode();
		hashCode += hashCode * 31 + this.xdiUri.hashCode();

		return hashCode;
	}
}
