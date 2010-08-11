package pds.store.xri;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.openxri.xml.CanonicalEquivID;
import org.openxri.xml.CanonicalID;
import org.openxri.xml.EquivID;
import org.openxri.xml.Redirect;
import org.openxri.xml.Ref;
import org.openxri.xml.Service;

/**
 * This class is used to represent i-names (not i-numbers).
 * 
 * - In the GRS, this corresponds to an i-name object.
 * - In OpenXRI, this corresponds to a subsegment object.
 * 
 * Every i-name has 0..1 associated parent authorities.
 * Every i-name has 1 associated authority.
 * 
 * In the case of synonyms, multiple i-names can share the same authority.
 * 
 * Associated with an authority are the elements of an XRD.
 */
public interface Xri extends Serializable, Comparable<Xri> {

	//
	// Basic information about the i-name
	//

	/**
	 * Returns the "local name" of this i-name.
	 * 
	 * In the case of a top-level i-name, this is simply the i-name itself (e.g. =myname)
	 * In the case of a community i-name, this is the subsegment (e.g. *myname)
	 */
	public String getLocalName();

	/**
	 * Returns a single "full name" of this i-name.
	 * 
	 * In the case of a top-level i-name, this is simply the i-name itself (e.g. =myname)
	 * In the case of a community i-name, this is the whole i-name (e.g. =mycommunity*myname)
	 */
	public String getFullName();

	/**
	 * Returns all "full names" of this i-name. This can be more than one if the i-name
	 * is a community i-name, and if one or more parent i-names higher up in the hierarchy
	 * have synonyms.
	 * 
	 * For example, if =mycommunity and =myothercommunity are synonyms, then there
	 * could be a community i-name *myname which has two "full names":
	 * =mycommunity*myname and =myothercommunity*myname
	 */
	public List<String> getFullNames();

	/**
	 * Returns the ID of the OpenXRI authority of this i-name.
	 */
	public String getAuthorityId();

	/**
	 * Returns the registration date of this i-name.
	 */
	public Date getDate();

	/**
	 * Returns the expiration date of this i-name.
	 */
	public Date getExpirationDate();

	/**
	 * Returns the user identifier of this i-name.
	 */
	public String getUserIdentifier();

	/**
	 * Returns a child XRI.
	 */
	public Xri getChildXri(String localName) throws XriStoreException;

	/**
	 * Returns a list of "full names" of i-names that resolve to the parent authority of 
	 * this i-name.
	 */
	public List<String> getParentAliases();

	/**
	 * Returns a list of "full names" of i-names that resolve to the authority of 
	 * this i-name.
	 */
	public List<String> getAliases();

	/**
	 * Returns true, if the i-name is "stale". An i-name is "stale" if it has a
	 * parent authority (i.e. is a community i-name) but the parent authority has
	 * no "full name". 
	 * 
	 * For example, let's assume we have a community i-name @free*earth*moon. If
	 * someone deletes the community i-name *earth, then *moon still exists, but
	 * its parent authority has no "full name" anymore. Therefore *moon is stale.
	 */
	public boolean isStale();

	//
	// Attributes of this i-name and its associated authority
	//

	public String getXriAttribute(String key) throws XriStoreException;
	public boolean hasXriAttribute(String key) throws XriStoreException;
	public void setXriAttribute(String key, String value) throws XriStoreException;
	public String getAuthorityAttribute(String key) throws XriStoreException;
	public boolean hasAuthorityAttribute(String key) throws XriStoreException;
	public void setAuthorityAttribute(String key, String value) throws XriStoreException;

	//
	// get XRD elements of this i-name's authority
	//

	public CanonicalID getCanonicalID() throws XriStoreException;
	public CanonicalEquivID getCanonicalEquivID() throws XriStoreException;
	public String getExtension() throws XriStoreException;
	public List<EquivID> getEquivIDs() throws XriStoreException;
	public List<Ref> getRefs() throws XriStoreException;
	public List<Redirect> getRedirects() throws XriStoreException;
	public List<Service> getServices() throws XriStoreException;	

	//
	// set XRD elements of this i-name's authority
	//

	public void setCanonicalID(CanonicalID canonicalID) throws XriStoreException;
	public void setCanonicalEquivID(CanonicalEquivID canonicalID) throws XriStoreException;
	public void setExtension(String extension) throws XriStoreException;
	public void addEquivID(EquivID equivID) throws XriStoreException;
	public void addRef(Ref ref) throws XriStoreException;
	public void addRedirect(Redirect redirect) throws XriStoreException;
	public void addService(Service service) throws XriStoreException;
	public void addServices(Service[] services) throws XriStoreException;

	//
	// delete XRD elements of this i-name's authority
	//

	public void deleteCanonicalEquivID() throws XriStoreException;
	public void deleteExtension() throws XriStoreException;
	public void deleteEquivID(EquivID equivID) throws XriStoreException;
	public void deleteRef(Ref ref) throws XriStoreException;
	public void deleteRedirect(Redirect redirect) throws XriStoreException;
	public void deleteService(Service service) throws XriStoreException;

	public void deleteAllServices() throws XriStoreException;
	public void deleteStandardServices() throws XriStoreException;
}
