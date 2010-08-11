package pds.store.xri;

import java.util.Calendar;
import java.util.List;

import org.eclipse.higgins.xdi4j.Subject;

/**
 * The XriStore is used for creating, retrieving and managing i-names.
 */
public interface XriStore {

	//
	// XriData from an XDI Subject
	//

	/**
	 * Creates an XriData object (for registering new i-names) from an XDI Subject
	 */
	public XriData createXriDataFromSubject(Subject subject) throws XriStoreException;

	//
	// Registering i-names
	//

	/**
	 * Checks if a given i-name exists already.
	 * @param parentXri The parent i-name. In the case of top-level i-names, this is null.
	 * @param localName The i-name to check for. In the case of top-level i-names, this
	 * is the i-name itself (e.g. "=myname"). In the case of community i-names, this is
	 * the subsegment (e.g. "*myname").
	 * @return True, if the i-name exists already.
	 */
	public boolean existsXri(Xri parentXri, String localName) throws XriStoreException;

	/**
	 * Register a new i-name with a new i-name authority.
	 * @param parentXri The parent i-name. In the case of top-level i-names, this is null.
	 * @param localName The i-name to register. In the case of top-level i-names, this
	 * is the i-name itself (e.g. "=myname"). In the case of community i-names, this is
	 * the subsegment (e.g. "*myname").
	 * @param xriData The user data necessary to register the i-name.
	 * @param years The number of years for which to register the i-name. This only
	 * applies to top-level i-names.
	 * @return The newly created i-name.
	 */
	public Xri registerXri(Xri parentXri, String localName, XriData xriData, int years) throws XriStoreException;

	/**
	 * Register a new i-name with an already existing i-name authority (synonyms).
	 * @param parentXri The parent i-name. In the case of top-level i-names, this is null.
	 * @param localName The i-name to register. In the case of top-level i-names, this
	 * is the i-name itself (e.g. "=myname"). In the case of community i-names, this is
	 * the subsegment (e.g. "*myname").
	 * @param xri The already existing i-name. This should have the same parent authority
	 * as the i-name that is being registered. 
	 * @param xriData The user data necessary to register the i-name.
	 * @param years The number of years for which to register the i-name. This only
	 * applies to top-level i-names.
	 * @return The newly created i-name.
	 */
	public Xri registerXriSynonym(Xri parentXri, String localName, Xri xri, XriData xriData, int years) throws XriStoreException;

	//
	// Managing i-names
	//

	/**
	 * Issues a Transfer IN request for a top-level authority. This creates a provisional
	 * i-name in the XriStore, which will be updated when the Transfer IN is completed.
	 * @param localName The "local name" of the i-name. This is the i-name itself,
	 * e.g. =myname.
	 * @param xriData The user data necessary to transfer the i-name.
	 */
	public Xri transferAuthorityInRequest(String localName, XriData xriData) throws XriStoreException;

	/**
	 * Tells the XriStore that a top-level authority Transfer IN has been completed.
	 * This updates the i-name appropriately.
	 * @param xri The i-name Transfer IN which has been completed.
	 */
	public void transferAuthorityInComplete(Xri xri) throws XriStoreException;

	/**
	 * Tells the XriStore that a top-level authority Transfer IN has been canceled. 
	 * This updates the i-name appropriately.
	 * @param xri The i-name Transfer IN which has been canceled.
	 */
	public void transferAuthorityInCanceled(Xri xri) throws XriStoreException;

	/**
	 * Approves a Transfer OUT request for a top-level authority. This does not 
	 * update the i-name in the XriStore.
	 * @param xri The i-name to approve for Transfer OUT.
	 * @param token The transfer token with which to approve the Transfer OUT.
	 */
	public void transferAuthorityOutApprove(Xri xri, String token) throws XriStoreException;

	/**
	 * Rejects a Transfer OUT request for a top-level authority. This does not 
	 * update the i-name in the XriStore.
	 * @param xri The i-name to reject for Transfer OUT.
	 * @param token The transfer token with which to reject the Transfer OUT.
	 */
	public void transferAuthorityOutReject(Xri xri, String token) throws XriStoreException;

	/**
	 * Tells the XriStore that a top-level authority Transfer OUT has been completed.
	 * This updates the i-name appropriately.
	 * @param xri The i-name Transfer OUT which has been completed.
	 */
	public void transferAuthorityOutComplete(Xri xri) throws XriStoreException;

	/**
	 * Tells the XriStore that a top-level authority Transfer OUT has been canceled.
	 * This updates the i-name appropriately.
	 * @param xri The i-name Transfer OUT which has been canceled.
	 */
	public void transferAuthorityOutCanceled(Xri xri) throws XriStoreException;

	/**
	 * Issues a Transfer IN request for a top-level i-name. This creates a provisional
	 * i-name in the XriStore, which will be updated when the Transfer IN is completed.
	 * @param localName The "local name" of the i-name. This is the i-name itself,
	 * e.g. =myname.
	 * @param xriData The user data necessary to transfer the i-name.
	 */
	public Xri transferXriInRequest(String localName, XriData xriData) throws XriStoreException;

	/**
	 * Tells the XriStore that a top-level i-name Transfer IN has been completed.
	 * This updates the i-name appropriately.
	 * @param xri The i-name Transfer IN which has been completed.
	 */
	public void transferXriInComplete(Xri xri) throws XriStoreException;

	/**
	 * Tells the XriStore that a top-level i-name Transfer IN has been canceled. 
	 * This updates the i-name appropriately.
	 * @param xri The i-name Transfer IN which has been canceled.
	 */
	public void transferXriInCanceled(Xri xri) throws XriStoreException;

	/**
	 * Approves a Transfer OUT request for a top-level i-name. This does not 
	 * update the i-name in the XriStore.
	 * @param xri The i-name to approve for Transfer OUT.
	 * @param token The transfer token with which to approve the Transfer OUT.
	 */
	public void transferXriOutApprove(Xri xri, String token) throws XriStoreException;

	/**
	 * Rejects a Transfer OUT request for a top-level i-name. This does not 
	 * update the i-name in the XriStore.
	 * @param xri The i-name to reject for Transfer OUT.
	 * @param token The transfer token with which to reject the Transfer OUT.
	 */
	public void transferXriOutReject(Xri xri, String token) throws XriStoreException;

	/**
	 * Tells the XriStore that a top-level i-name Transfer OUT has been completed.
	 * This updates the i-name appropriately.
	 * @param xri The i-name Transfer OUT which has been completed.
	 */
	public void transferXriOutComplete(Xri xri) throws XriStoreException;

	/**
	 * Tells the XriStore that a top-level i-name Transfer OUT has been canceled.
	 * This updates the i-name appropriately.
	 * @param xri The i-name Transfer OUT which has been canceled.
	 */
	public void transferXriOutCanceled(Xri xri) throws XriStoreException;

	/**
	 * This renews an i-name. Only top-level i-names can be renewed.
	 * Both the i-name and associated i-number get renewed in the GRS. In order to
	 * renew synonyms of an i-name, this method must be called on those synonym i-name
	 * separately.
	 * @param xri The i-name to renew.
	 * @param years The number of years by which to renew the i-name and i-number.
	 * @return The new expiration date of the i-name.
	 * @throws XriStoreException
	 */
	public Calendar renewXri(Xri xri, int years) throws XriStoreException;

	/**
	 * This deletes an i-name. Only community i-names can be deleted.
	 */
	public void deleteXri(Xri xri) throws XriStoreException;

	/**
	 * This deletes an i-name's authority and all i-names that use it. Only community i-names can be deleted.
	 */
	public void deleteAuthority(Xri xri) throws XriStoreException;

	//
	// Listing and finding i-names
	//

	/**
	 * Lists all i-names in the XriStore.
	 */
	public List<Xri> listXris() throws XriStoreException;

	/**
	 * Lists all root i-names in the XriStore. A root i-name is one that does not have
	 * a parent authority. This can be a top-level i-name or the namespace of a
	 * community registry.
	 */
	public List<Xri> listRootXris() throws XriStoreException;

	/**
	 * Lists all i-names associated with a given user identifier.
	 */
	public List<Xri> listUserXris(String userIdentifier) throws XriStoreException;

	/**
	 * Finds an i-name by string, e.g. =myname or =mycommunity*myname
	 */
	public Xri findXri(String xri) throws XriStoreException;

	/**
	 * Finds an i-name by its OpenXRI authority ID.
	 */
	public Xri findXriByGrsAuthorityId(String grsAuthorityId) throws XriStoreException;

	/**
	 * Finds the user identifier associated with an i-name.
	 */
	public String findUserIdentifier(String xri) throws XriStoreException;

	//
	// Statistics
	//

	/**
	 * Returns the number of i-names in the XriStore.
	 */
	public long getXriCount() throws XriStoreException;

	/**
	 * Returns the number of i-name authorities in the XriStore.
	 */
	public long getAuthorityCount() throws XriStoreException;
}
