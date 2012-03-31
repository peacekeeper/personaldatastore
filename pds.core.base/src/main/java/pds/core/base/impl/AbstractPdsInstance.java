package pds.core.base.impl;

import pds.core.base.PdsInstance;

public abstract class AbstractPdsInstance implements PdsInstance {

	private String pdsPath;

	public AbstractPdsInstance(String pdsPath) {

		this.pdsPath = pdsPath;
	}

	public String getPdsPath() {

		return this.pdsPath;
	}

	public void setPdsPath(String pdsPath) {

		this.pdsPath = pdsPath;
	}
}
