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
	 * Given a raw request path, returns the XDI4j messaging target path to which
	 * the request applies.
	 */
	public String getTarget(String path);

	/**
	 * Returns a PDS instance for a given XDI4j messaging target path.
	 */
	public PdsInstance getPdsInstance(String target) throws PdsException;

	/**
	 * Returns all XDI4j messaging target paths at which the PDS instance
	 * will be mounted.
	 */
	public String[] getAllMountTargets(PdsInstance pdsInstance) throws PdsException;
}
