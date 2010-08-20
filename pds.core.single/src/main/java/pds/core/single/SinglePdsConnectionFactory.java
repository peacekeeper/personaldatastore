package pds.core.single;

import javax.servlet.FilterConfig;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.PdsConnection;
import pds.core.PdsConnectionFactory;
import pds.core.PdsException;

public class SinglePdsConnectionFactory implements PdsConnectionFactory {

	private XRI3Segment canonical;
	private XRI3Segment[] aliases;
	private String[] endpoints;

	@Override
	public void init(FilterConfig filterConfig) throws PdsException {

		// check canonical
		
		if (this.canonical == null) {

			throw new PdsException("Please configure an identifier for pds-core-single! See http://www.personaldatastore.info/pds-core-single/ for more information.");
		}

		// check aliases
		
		if (this.aliases == null || this.aliases.length < 1) {

			this.aliases = new XRI3Segment[0];
		}
	}

	public PdsConnection getPdsConnection(String identifier) throws PdsException {

		if (! identifier.equals("")) return null;
 
		return new SinglePdsConnection(identifier, this.canonical, this.aliases, this.endpoints);
	}

	public XRI3Segment getCanonical() {

		return this.canonical;
	}

	public void setCanonical(XRI3Segment canonical) {

		this.canonical = canonical;
	}

	public void setCanonical(String canonical) {

		this.canonical = new XRI3Segment(canonical);
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
