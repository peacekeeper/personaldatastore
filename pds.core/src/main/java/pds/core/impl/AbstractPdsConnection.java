package pds.core.impl;

import pds.core.PdsConnection;

public abstract class AbstractPdsConnection implements PdsConnection {

	private String identifier;

	public AbstractPdsConnection(String identifier) {

		this.identifier = identifier;
	}

	public String getIdentifier() {

		return this.identifier;
	}
}
