package pds.store.xri;

import java.io.Serializable;

/**
 * The user data needed for
 * - Registering a new i-name
 * - Registering a new i-name as a synonym of an existing i-name
 * - Transferring an existing i-name
 * 
 * The list of what XriData must contain depends on which of the above operations
 * is being performed, and on the kind of i-name (OpenXRI or GRS).
 */
public interface XriData extends Serializable {

	/**
	 * Checks if the XriData contains everything needed for registering
	 * a new i-name in the XriStore.
	 */
	public boolean isCompleteForRegister();

	/**
	 * Checks if the XriData contains everything needed for registering
	 * a new i-name as a synonym of an existing i-name in the XriStore.
	 */
	public boolean isCompleteForRegisterSynonym();

	/**
	 * Checks if the XriData contains everything needed for transferring an
	 * existing i-name into the XriStore.
	 */
	public boolean isCompleteForTransfer();
}
