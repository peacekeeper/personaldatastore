package pds.store.xri.impl;

import pds.store.xri.Xri;


public abstract class AbstractXri implements Xri {

	private static final long serialVersionUID = 5501774125670768779L;

	@Override
	public String toString() {

		return(this.getFullName());
	}

	public int compareTo(Xri other) {

		if (other == this) return(0);
		if (other == null) return(0);

		return(this.getFullName().compareTo(other.getFullName()));
	}

	@Override
	public boolean equals(Object o) {

		if (o == this) return(true);
		if (o == null) return(false);

		if (this.getFullName() != null) return(this.getFullName().equals(((Xri) o).getFullName()));

		return(false);
	}

	@Override
	public int hashCode() {

		if (this.getFullName() != null) return(this.getFullName().hashCode());

		return(0);
	}
}
