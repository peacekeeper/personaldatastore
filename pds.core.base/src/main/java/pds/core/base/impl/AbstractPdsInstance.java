package pds.core.base.impl;

import pds.core.base.PdsInstance;

public abstract class AbstractPdsInstance implements PdsInstance {

	private String target;

	public AbstractPdsInstance(String target) {

		this.target = target;
	}

	public String getTarget() {

		return this.target;
	}

	public void setTarget(String target) {

		this.target = target;
	}
}
