package pds.core.single;

import javax.servlet.FilterConfig;

import pds.core.PdsConnection;
import pds.core.PdsConnectionException;
import pds.core.PdsConnectionFactory;

public class SinglePdsConnectionFactory implements PdsConnectionFactory {

	private String identifier;
	private String[] aliases;
	private String[] endpoints;

	@Override
	public void init(FilterConfig filterConfig) throws PdsConnectionException {

	}

	public PdsConnection getPdsConnection(String identifier) throws PdsConnectionException {

		// check identifier

		if (! identifier.equals(this.identifier)) return null;

		// done

		return new SinglePdsConnection(this.identifier, this.aliases, this.endpoints);
	}

	public String getIdentifier() {

		return this.identifier;
	}

	public void setIdentifier(String identifier) {

		this.identifier = identifier;
	}

	public String[] getAliases() {

		return this.aliases;
	}

	public void setAliases(String[] aliases) {

		this.aliases = aliases;
	}

	public String[] getEndpoints() {

		return this.endpoints;
	}

	public void setEndpoints(String[] endpoints) {

		this.endpoints = endpoints;
	}
}
