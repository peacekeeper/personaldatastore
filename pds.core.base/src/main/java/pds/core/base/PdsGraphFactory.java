package pds.core.base;

import javax.servlet.FilterConfig;

import xdi2.core.Graph;

/**
 * This interface is used by PdsFilter to get an XDI2 Graph for a given PdsInstance.
 * 
 * For each XDI2 GraphFactory (E.g. Memory, BDB, etc.) there is a corresponding PdsGraphFactory.
 */
public interface PdsGraphFactory {

	/**
	 * Initializes the PdsGraphFactory.
	 */
	public void init(FilterConfig filterConfig) throws PdsException;

	/**
	 * Gets an XDI2 Graph for a given PdsInstance.
	 */
	public Graph getPdsInstanceGraph(PdsInstance pdsInstance) throws PdsException;
}
