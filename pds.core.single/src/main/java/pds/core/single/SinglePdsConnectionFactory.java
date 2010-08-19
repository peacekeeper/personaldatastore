package pds.core.single;

import javax.servlet.FilterConfig;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.PdsConnection;
import pds.core.PdsConnectionException;
import pds.core.PdsConnectionFactory;

public class SinglePdsConnectionFactory implements PdsConnectionFactory {

	private XRI3Segment identifier;
	private XRI3Segment[] aliases;
	private String[] endpoints;

	@Override
	public void init(FilterConfig filterConfig) throws PdsConnectionException {

		// check identifier
		
		if (this.identifier == null) {

			throw new PdsConnectionException("Please configure an identifier for pds-core-single! See http://www.personaldatastore.info/pds-core-single/ for more information.");
		}

		// check aliases
		
		if (this.aliases == null || this.aliases.length < 1) {

			this.aliases = new XRI3Segment[0];
		}
		
		// check endpoints

		if (this.endpoints == null || this.endpoints.length < 1) {

			this.endpoints = new String[] { filterConfig.getServletContext().getContextPath() };
		}
	}

	public PdsConnection getPdsConnection(String identifier) throws PdsConnectionException {

		if (! identifier.equals("")) return null;

		return new SinglePdsConnection(this.identifier, this.aliases, this.endpoints);
	}

	public XRI3Segment getIdentifier() {

		return this.identifier;
	}

	public void setIdentifier(XRI3Segment identifier) {

		this.identifier = identifier;
	}

	public void setIdentifier(String identifier) {

		this.identifier = new XRI3Segment(identifier);
	}

	public XRI3Segment[] getAliases() {

		return this.aliases;
	}

	public void setAliases(XRI3Segment[] aliases) {

		this.aliases = aliases;
	}

	public void setAliases(String[] aliases) {

		this.aliases = new XRI3Segment[aliases.length];
		for (int i=0; i<aliases.length; i++) this.aliases[i] = new XRI3Segment(aliases[i]);
	}

	public String[] getEndpoints() {

		return this.endpoints;
	}

	public void setEndpoints(String[] endpoints) {

		this.endpoints = endpoints;
	}
}
