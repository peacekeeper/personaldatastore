package pds.core.base;

import javax.servlet.FilterConfig;

/**
 * A PdsInstanceFactory can instantiate PDSes (PdsInstance) for given request paths.
 * 
 * @author Markus
 */
public interface PdsInstanceFactory {

	/**
	 * Initialize the PdsInstanceFactory.
	 */
	public void init(FilterConfig filterConfig) throws PdsException;

	/**
	 * Given a raw request path, returns the PDS path to which
	 * the request applies.
	 */
	public String getPdsPath(String path);

	/**
	 * Returns a PDS instance for a given PDS path.
	 */
	public PdsInstance getPdsInstance(String pdsPath) throws PdsException;

	/**
	 * Returns all PDS paths at which the PDS instance will be registered.
	 */
	public String[] getAllPdsPaths(PdsInstance pdsInstance) throws PdsException;
}
