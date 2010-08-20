package pds.core;

import javax.servlet.FilterConfig;

public interface PdsConnectionFactory {

	public void init(FilterConfig filterConfig) throws PdsException;
	public PdsConnection getPdsConnection(String identifier) throws PdsException;
}
