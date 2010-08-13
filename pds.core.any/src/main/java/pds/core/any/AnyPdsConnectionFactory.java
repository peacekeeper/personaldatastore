package pds.core.any;

import javax.servlet.FilterConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.PdsConnection;
import pds.core.PdsConnectionException;
import pds.core.PdsConnectionFactory;

public class AnyPdsConnectionFactory implements PdsConnectionFactory {

	private static Log log = LogFactory.getLog(AnyPdsConnectionFactory.class.getName());

	private String[] endpoints;

	@Override
	public void init(FilterConfig filterConfig) throws PdsConnectionException {

		if (this.endpoints == null) {

			this.endpoints = new String[] { filterConfig.getServletContext().getContextPath() };
		}
	}

	public PdsConnection getPdsConnection(String identifier) throws PdsConnectionException {

		// check if the identifier is a valid XRI3Segment

		try {

			new XRI3Segment(identifier);
		} catch (Exception ex) {

			log.warn("Not a valid XRI3Segment: " + identifier + ": " + ex.getMessage(), ex);
			return null;
		}

		// done

		return new AnyPdsConnection(new XRI3Segment(identifier), this.endpoints);
	}

	public String[] getEndpoints() {

		return this.endpoints;
	}

	public void setEndpoints(String[] endpoints) {

		this.endpoints = endpoints;
	}
}
