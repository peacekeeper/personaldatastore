package pds.core.any;

import javax.servlet.FilterConfig;

import pds.core.PdsConnection;
import pds.core.PdsConnectionException;
import pds.core.PdsConnectionFactory;

public class AnyPdsConnectionFactory implements PdsConnectionFactory {

	private String[] endpoints;

	@Override
	public void init(FilterConfig filterConfig) throws PdsConnectionException {

		if (this.endpoints == null) {

			this.endpoints = new String[] { filterConfig.getServletContext().getContextPath() };
		}
	}

	public PdsConnection getPdsConnection(String identifier) throws PdsConnectionException {

		// done

		return new AnyPdsConnection(identifier, this.endpoints);
	}

	public String[] getEndpoints() {

		return this.endpoints;
	}

	public void setEndpoints(String[] endpoints) {

		this.endpoints = endpoints;
	}
}
