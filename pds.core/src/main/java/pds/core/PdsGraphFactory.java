package pds.core;

import javax.servlet.FilterConfig;

import org.eclipse.higgins.xdi4j.Graph;

/**
 * This interface is used by PdsFilter to get an XDI4j Graph for a given PdsConnection.
 * 
 * For each XDI4j GraphFactory (E.g. Memory, BDB, etc.) there is a corresponding PdsGraphFactory.
 */
public interface PdsGraphFactory {

	/**
	 * Initializes the PdsGraphFactory.
	 */
	public void init(FilterConfig filterConfig) throws PdsException;

	/**
	 * Gets an XDI4j Graph for a given PdsConnection.
	 */
	public Graph getPdsConnectionGraph(PdsConnection pdsConnection) throws PdsException;
}
