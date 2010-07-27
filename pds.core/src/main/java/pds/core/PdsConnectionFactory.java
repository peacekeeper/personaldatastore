package pds.core;

import javax.servlet.FilterConfig;

public interface PdsConnectionFactory {

	public void init(FilterConfig filterConfig) throws PdsConnectionException;
	public PdsConnection getPdsConnection(String identifier) throws PdsConnectionException;
}
