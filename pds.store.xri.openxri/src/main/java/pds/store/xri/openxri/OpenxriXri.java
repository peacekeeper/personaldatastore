package pds.store.xri.openxri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openxri.exceptions.StoreException;
import org.openxri.store.Authority;
import org.openxri.store.Store;
import org.openxri.store.StoreAttributable;
import org.openxri.store.StoreBetterLookup;
import org.openxri.store.StoreEditable;
import org.openxri.store.SubSegment;
import org.openxri.xml.CanonicalEquivID;
import org.openxri.xml.CanonicalID;
import org.openxri.xml.EquivID;
import org.openxri.xml.Redirect;
import org.openxri.xml.Ref;
import org.openxri.xml.Service;
import org.openxri.xml.XRD;

import pds.store.xri.Xri;
import pds.store.xri.XriConstants;
import pds.store.xri.XriStoreException;
import pds.store.xri.XriStoreRuntimeException;
import pds.store.xri.impl.AbstractXri;
import pds.store.xri.util.ServiceUtil;

/**
 * An i-name that exists in OpenXRI.
 * In ibrokerKit, both top-level and community i-names are stored in OpenXRI.
 */
public class OpenxriXri extends AbstractXri {

	private static final long serialVersionUID = 8297057913090804952L;

	protected Store openxriStore;
	protected SubSegment subSegment;
	protected Authority authority;

	protected Authority parentAuthority = null;
	protected XRD xrd = null;
	protected String authorityId = null;
	protected Map<String, String> subSegmentAttributes = null;
	protected Map<String, String> authorityAttributes = null;
	protected Date date = null;
	protected Date expirationDate = null;
	protected String userIdentifier = null;
	protected List<String> parentAliases = null;
	protected List<String> aliases = null;
	protected CanonicalID canonicalID = null;
	protected CanonicalEquivID canonicalEquivID = null;
	protected String extension = null;
	protected List<EquivID> equivIDs = null;
	protected List<Ref> refs = null;
	protected List<Redirect> redirects = null;
	protected List<Service> services = null;

	public OpenxriXri(Store openxriStore, SubSegment subSegment) {

		if (openxriStore == null || subSegment == null) throw new NullPointerException();

		this.openxriStore = openxriStore;
		this.subSegment = subSegment;
		this.authority = null;
	}

	public OpenxriXri(Store openxriStore, SubSegment subSegment, Authority authority) {

		if (openxriStore == null || subSegment == null || authority == null) throw new NullPointerException();

		this.openxriStore = openxriStore;
		this.subSegment = subSegment;
		this.authority = authority;
	}

	public OpenxriXri(OpenxriXri xri) {

		if (xri == null || xri.openxriStore == null || xri.subSegment == null || xri.authority == null) throw new NullPointerException();

		this.openxriStore = xri.openxriStore;
		this.subSegment = xri.subSegment;
		this.authority = xri.authority;
	}

	public SubSegment getSubSegment() {

		return(this.subSegment);
	}

	public Authority getAuthority() throws XriStoreException {

		if (this.authority == null) {

			try {

				this.authority = this.openxriStore.getSubSegmentAuthority(this.subSegment);
			} catch (StoreException ex) {

				throw new XriStoreException(ex.getMessage(), ex);
			}
		}

		return(this.authority);
	}

	public Authority getParentAuthority() throws XriStoreException {

		if (this.parentAuthority == null) {

			try {

				this.parentAuthority = this.openxriStore.getSubSegmentParentAuthority(this.subSegment);
			} catch (StoreException ex) {

				throw new XriStoreException(ex.getMessage(), ex);
			}
		}

		return(this.parentAuthority);
	}

	public String getLocalName() {

		return(this.subSegment.getName());
	}

	public String getFullName() {

		return(this.getFullNames().get(0));
	}

	public List<String> getFullNames() {

		List<String> fullNames = new ArrayList<String> ();
		List<String> parentAliases = this.getParentAliases();
		String localName = this.getLocalName();

		if (parentAliases == null) {

			fullNames.add(localName);
		} else if (parentAliases.size() < 1) {

			fullNames.add(localName);	// stale
		} else {

			for (String parentAlias : parentAliases) fullNames.add(parentAlias + localName);
		}

		return(fullNames);
	}

	public String getAuthorityId() {

		if (this.authorityId == null) {

			try {

				Authority authority = this.getAuthority();
				this.authorityId = authority.getId().toString();
			} catch (XriStoreException ex) {

				throw new XriStoreRuntimeException("Cannot read authority ID: " + ex.getMessage(), ex);
			}
		}

		return(this.authorityId);
	}

	public Date getDate() {

		if (this.date == null) {

			try {

				Map<String, String> attributes = this.getSubSegmentAttributes();
				String value = attributes.get(XriConstants.ATTRIBUTE_KEY_DATE);
				this.date = value == null ? null : new Date(Long.parseLong(value));
			} catch (XriStoreException ex) {

				throw new XriStoreRuntimeException(ex.getMessage(), ex);
			}
		}

		return(this.date);
	}

	public Date getExpirationDate() {

		if (this.expirationDate == null) {

			try {

				Map<String, String> attributes = this.getSubSegmentAttributes();
				String value = attributes.get(XriConstants.ATTRIBUTE_KEY_EXPIRATIONDATE);
				this.expirationDate = value == null ? null : new Date(Long.parseLong(value));
			} catch (XriStoreException ex) {

				throw new XriStoreRuntimeException(ex.getMessage(), ex);
			}
		}

		return(this.expirationDate);
	}

	public String getUserIdentifier() {

		if (this.userIdentifier == null) {

			try {

				Authority authority = this.getAuthority();
				this.userIdentifier = ((StoreAttributable) this.openxriStore).getAuthorityIndex(authority);
			} catch (XriStoreException ex) {

				throw new XriStoreRuntimeException(ex.getMessage(), ex);
			} catch (StoreException ex) {

				throw new XriStoreRuntimeException(ex.getMessage(), ex);
			}
		}

		return(this.userIdentifier);
	}

	public Xri getChildXri(String localName) throws XriStoreException {

		SubSegment childSubSegment;

		try {

			childSubSegment = ((StoreBetterLookup) this.openxriStore).findSubSegment(this.getAuthority(), localName);
		} catch (StoreException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}

		return(new OpenxriXri(this.openxriStore, childSubSegment));
	}

	public String getXriAttribute(String key) throws XriStoreException {

		Map<String, String> subSegmentAttributes = this.getSubSegmentAttributes();

		return(subSegmentAttributes.get(key));
	}

	@Override
	public boolean hasXriAttribute(String key) throws XriStoreException {

		return(this.getXriAttribute(key) != null);
	}

	public void setXriAttribute(String key, String value) throws XriStoreException {

		try {

			SubSegment subSegment = this.getSubSegment();
			Map<String, String> subSegmentAttributes = this.getSubSegmentAttributes();

			subSegmentAttributes.put(key, value);
			((StoreAttributable) this.openxriStore).setSubSegmentAttributes(subSegment, subSegmentAttributes);
		} catch (StoreException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}
	}

	@Override
	public String getAuthorityAttribute(String key) throws XriStoreException {

		Map<String, String> authorityAttributes = this.getAuthorityAttributes();

		return(authorityAttributes.get(key));
	}

	@Override
	public boolean hasAuthorityAttribute(String key) throws XriStoreException {

		return(this.getAuthorityAttribute(key) != null);
	}

	public void setAuthorityAttribute(String key, String value) throws XriStoreException {

		try {

			Authority authority = this.getAuthority();
			Map<String, String> authorityAttributes = this.getAuthorityAttributes();

			authorityAttributes.put(key, value);
			((StoreAttributable) this.openxriStore).setAuthorityAttributes(authority, authorityAttributes);
		} catch (StoreException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}
	}

	public List<String> getParentAliases() {

		if (this.parentAliases == null) {

			try {

				Authority parentAuthority = this.getParentAuthority();
				this.parentAliases = parentAuthority == null ? null : Arrays.asList(((StoreBetterLookup) this.openxriStore).getAuthorityQxris(parentAuthority, true, false));
			} catch (XriStoreException ex) {

				throw new XriStoreRuntimeException(ex.getMessage(), ex);
			} catch (StoreException ex) {

				throw new XriStoreRuntimeException(ex.getMessage(), ex);
			}
		}

		return(this.parentAliases);
	}

	public List<String> getAliases() {

		if (this.aliases == null) {

			try {

				Authority authority = this.getAuthority();
				this.aliases = Arrays.asList(((StoreBetterLookup) this.openxriStore).getAuthorityQxris(authority, true, false));
			} catch (XriStoreException ex) {

				throw new XriStoreRuntimeException(ex.getMessage(), ex);
			} catch (StoreException ex) {

				throw new XriStoreRuntimeException(ex.getMessage(), ex);
			}
		}

		return(this.aliases);
	}

	public boolean isStale() {

		try {

			return(this.getParentAuthority() != null && this.getParentAliases().size() < 1);
		} catch (XriStoreException ex) {

			return(false);
		}
	}

	public CanonicalID getCanonicalID() throws XriStoreException {

		if (this.canonicalID == null) {

			XRD xrd = this.getXrd();
			this.canonicalID = xrd.getCanonicalID();
		}

		return(this.canonicalID);
	}

	public CanonicalEquivID getCanonicalEquivID() throws XriStoreException {

		if (this.canonicalEquivID == null) {

			XRD xrd = this.getXrd();
			this.canonicalEquivID = xrd.getCanonicalEquivID();
		}

		return(this.canonicalEquivID);
	}

	public String getExtension() throws XriStoreException {

		if (this.extension == null) {

			XRD xrd = this.getXrd();
			this.extension = xrd.getExtension();
		}

		return(this.extension);
	}

	public List<EquivID> getEquivIDs() throws XriStoreException {

		if (this.equivIDs == null) {

			XRD xrd = this.getXrd();
			this.equivIDs = new ArrayList<EquivID> (xrd.getNumEquivIDs());
			for (int i=0; i<xrd.getNumEquivIDs(); i++) this.equivIDs.add(xrd.getEquivIDAt(i));
		}

		return(this.equivIDs);
	}

	@SuppressWarnings("unchecked")
	public List<Ref> getRefs() throws XriStoreException {

		if (this.refs == null) {

			XRD xrd = this.getXrd();
			this.refs = xrd.getRefs();
		}

		return(this.refs);
	}

	@SuppressWarnings("unchecked")
	public List<Redirect> getRedirects() throws XriStoreException {

		if (this.redirects == null) {

			XRD xrd = this.getXrd();
			this.redirects = xrd.getRedirects();
		}

		return(this.redirects);
	}

	@SuppressWarnings("unchecked")
	public List<Service> getServices() throws XriStoreException {

		if (this.services == null) {

			XRD xrd = this.getXrd();
			this.services = xrd.getServices();
		}

		return(this.services);
	}

	public void setCanonicalID(CanonicalID canonicalID) throws XriStoreException {

		try {

			Authority authority = this.getAuthority();
			XRD xrd = this.getXrd();

			this.canonicalID = canonicalID;
			xrd.setCanonicalID(canonicalID);

			((StoreEditable) this.openxriStore).setXrd(authority, xrd);
		} catch (StoreException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}
	}

	public void setCanonicalEquivID(CanonicalEquivID canonicalEquivID) throws XriStoreException {

		try {

			Authority authority = this.getAuthority();
			XRD xrd = this.getXrd();

			this.canonicalEquivID = canonicalEquivID;
			xrd.setCanonicalEquivID(canonicalEquivID);

			((StoreEditable) this.openxriStore).setXrd(authority, xrd);
		} catch (StoreException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}
	}

	public void setExtension(String extension) throws XriStoreException {

		try {

			Authority authority = this.getAuthority();
			XRD xrd = this.getXrd();

			this.extension = extension;
			xrd.setExtension(extension);

			((StoreEditable) this.openxriStore).setXrd(authority, xrd);
		} catch (Exception ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}
	}

	public void addEquivID(EquivID equivID) throws XriStoreException {

		try {

			Authority authority = this.getAuthority();
			XRD xrd = this.getXrd();
			List<EquivID> equivIDList = this.getEquivIDs();

			equivIDList.add(equivID);
			xrd.setEquivIDs(equivIDList);

			((StoreEditable) this.openxriStore).setXrd(authority, xrd);
		} catch (StoreException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}
	}

	public void addRef(Ref ref) throws XriStoreException {

		try {

			Authority authority = this.getAuthority();
			XRD xrd = this.getXrd();
			List<Ref> refList = this.getRefs();

			refList.add(ref);
			xrd.setRefs(refList);

			((StoreEditable) this.openxriStore).setXrd(authority, xrd);
		} catch (StoreException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}
	}

	public void addRedirect(Redirect redirect) throws XriStoreException {

		try {

			Authority authority = this.getAuthority();
			XRD xrd = this.getXrd();
			List<Redirect> redirectList = this.getRedirects();

			redirectList.add(redirect);
			xrd.setRedirects(redirectList);

			((StoreEditable) this.openxriStore).setXrd(authority, xrd);
		} catch (StoreException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}
	}

	public void addService(Service service) throws XriStoreException {

		try {

			Authority authority = this.getAuthority();
			XRD xrd = this.getXrd();
			List<Service> serviceList = this.getServices();

			serviceList.add(service);
			xrd.setServices(serviceList);

			((StoreEditable) this.openxriStore).setXrd(authority, xrd);
		} catch (StoreException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}
	}

	public void addServices(Service[] services) throws XriStoreException {

		try {

			Authority authority = this.getAuthority();
			XRD xrd = this.getXrd();
			List<Service> serviceList = this.getServices();

			serviceList.addAll(Arrays.asList(services));
			xrd.setServices(serviceList);

			((StoreEditable) this.openxriStore).setXrd(authority, xrd);
		} catch (StoreException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}
	}

	public void deleteCanonicalEquivID() throws XriStoreException {

		try {

			Authority authority = this.getAuthority();
			XRD xrd = this.getXrd();

			this.canonicalEquivID = null;
			xrd.setCanonicalEquivID(null);

			((StoreEditable) this.openxriStore).setXrd(authority, xrd);
		} catch (StoreException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}
	}

	public void deleteExtension() throws XriStoreException {

		try {

			Authority authority = this.getAuthority();
			XRD xrd = this.getXrd();

			this.extension = null;
			xrd.setExtension("");

			((StoreEditable) this.openxriStore).setXrd(authority, xrd);
		} catch (Exception ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}
	}

	public void deleteEquivID(EquivID equivID) throws XriStoreException {

		try {

			Authority authority = this.getAuthority();
			XRD xrd = this.getXrd();
			List<EquivID> equivIDList = this.getEquivIDs();

			equivIDList.remove(equivID);
			xrd.setEquivIDs(equivIDList);

			((StoreEditable) this.openxriStore).setXrd(authority, xrd);
		} catch (StoreException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}
	}

	public void deleteRef(Ref ref) throws XriStoreException {

		try {

			Authority authority = this.getAuthority();
			XRD xrd = this.getXrd();
			List<Ref> refList = this.getRefs();

			refList.remove(ref);
			xrd.setRefs(refList);

			((StoreEditable) this.openxriStore).setXrd(authority, xrd);
		} catch (StoreException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}
	}

	public void deleteRedirect(Redirect redirect) throws XriStoreException {

		try {

			Authority authority = this.getAuthority();
			XRD xrd = this.getXrd();
			List<Redirect> redirectList = this.getRedirects();

			redirectList.remove(redirect);
			xrd.setRedirects(redirectList);

			((StoreEditable) this.openxriStore).setXrd(authority, xrd);
		} catch (StoreException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}
	}

	public void deleteService(Service service) throws XriStoreException {

		try {

			Authority authority = this.getAuthority();
			XRD xrd = this.getXrd();
			List<Service> serviceList = this.getServices();

			serviceList.remove(service);
			xrd.setServices(serviceList);

			((StoreEditable) this.openxriStore).setXrd(authority, xrd);
		} catch (StoreException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}
	}

	public void deleteAllServices() throws XriStoreException {

		try {

			Authority authority = this.getAuthority();
			XRD xrd = this.getXrd();
			List<Service> serviceList = this.getServices();
			if (serviceList.size() < 1) return;

			serviceList.clear();
			xrd.setServices(serviceList);

			((StoreEditable) this.openxriStore).setXrd(authority, xrd);
		} catch (StoreException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}
	}

	public void deleteStandardServices() throws XriStoreException {

		try {

			Authority authority = this.getAuthority();
			XRD xrd = this.getXrd();
			List<Service> serviceList = this.getServices();
			if (serviceList.size() < 1) return;

			for (Iterator<Service> services = serviceList.iterator(); services.hasNext(); ) {

				Service service = services.next();

				if (ServiceUtil.isStandard(service)) { services.remove(); continue; }
			}
			if (serviceList.size() < 1) return;

			xrd.setServices(serviceList);

			((StoreEditable) this.openxriStore).setXrd(authority, xrd);
		} catch (StoreException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}
	}

	/*
	 * Helper methods
	 */

	private XRD getXrd() throws XriStoreException {

		if (this.xrd == null) {

			Authority authority = this.getAuthority();
			this.xrd = authority.getXrd();
		}

		return(this.xrd);
	}

	private Map<String, String> getSubSegmentAttributes() throws XriStoreException {

		SubSegment subSegment = this.getSubSegment();

		if (this.subSegmentAttributes == null) {

			try {

				this.subSegmentAttributes = ((StoreAttributable) this.openxriStore).getSubSegmentAttributes(subSegment);
			} catch (StoreException ex) {

				throw new XriStoreException(ex.getMessage(), ex);
			}
		}

		return(this.subSegmentAttributes);
	}

	private Map<String, String> getAuthorityAttributes() throws XriStoreException {

		Authority authority = this.getAuthority();

		if (this.authorityAttributes == null) {

			try {

				this.authorityAttributes = ((StoreAttributable) this.openxriStore).getAuthorityAttributes(authority);
			} catch (StoreException ex) {

				throw new XriStoreException(ex.getMessage(), ex);
			}
		}

		return(this.authorityAttributes);
	}
}
