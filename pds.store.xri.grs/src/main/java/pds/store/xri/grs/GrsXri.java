package pds.store.xri.grs;

import ibrokerkit.epptools4java.EppTools;
import ibrokerkit.epptools4java.EppToolsException;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.openxri.xml.CanonicalEquivID;
import org.openxri.xml.CanonicalID;
import org.openxri.xml.EquivID;
import org.openxri.xml.Redirect;
import org.openxri.xml.Ref;
import org.openxri.xml.Service;

import pds.store.xri.Xri;
import pds.store.xri.XriStoreException;
import pds.store.xri.openxri.OpenxriXri;
import pds.store.xri.util.ServiceUtil;

import com.neulevel.epp.xri.EppXriAuthority;
import com.neulevel.epp.xri.EppXriName;
import com.neulevel.epp.xri.EppXriRef;
import com.neulevel.epp.xri.EppXriServiceEndpoint;
import com.neulevel.epp.xri.EppXriSynonym;
import com.neulevel.epp.xri.EppXriURI;

/**
 * An i-name that exists in the GRS, i.e. a top-level i-name.
 * This class extends OpenxriXri, i.e. all top-level i-names are also stored
 * in OpenXRI.
 */
public class GrsXri extends OpenxriXri {

	private static final long serialVersionUID = 9212440485125727348L;

	private EppTools eppTools;
	private char gcs;
	private String grsAuthorityId;
	private String grsAuthorityPassword;
	private transient EppXriAuthority eppXriAuthority;
	private transient EppXriName eppXriName;

	public GrsXri(OpenxriXri xri, EppTools eppTools, char gcs, String grsAuthorityId, String grsAuthorityPassword) {

		super(xri);

		if (eppTools == null || grsAuthorityId == null) throw new NullPointerException();
		if (gcs != '=' && gcs != '@') throw new IllegalArgumentException("Invalid GCS: " + gcs); 

		this.eppTools = eppTools;
		this.gcs = gcs;
		this.grsAuthorityId = grsAuthorityId;
		this.grsAuthorityPassword = grsAuthorityPassword;
		this.eppXriAuthority = null;
	}

	public GrsXri(OpenxriXri xri, EppTools eppTools, char gcs, String grsAuthorityId, String grsAuthorityPassword, EppXriAuthority eppXriAuthority) {

		super(xri);

		if (eppTools == null || grsAuthorityId == null) throw new NullPointerException();
		if (gcs != '=' && gcs != '@') throw new IllegalArgumentException("Invalid GCS: " + gcs); 

		this.eppTools = eppTools;
		this.gcs = gcs;
		this.grsAuthorityId = grsAuthorityId;
		this.grsAuthorityPassword = grsAuthorityPassword;
		this.eppXriAuthority = eppXriAuthority;
	}

	public char getGcs() {

		return(this.gcs);
	}

	public String getGrsAuthorityId() {

		return(this.grsAuthorityId);
	}

	public String getGrsAuthorityPassword() {

		return(this.grsAuthorityPassword);
	}

	@Override
	public String getLocalName() {

		return(super.getLocalName());
	}

	@Override
	public String getFullName() {

		return(super.getFullName());
	}

	@Override
	public List<String> getFullNames() {

		return(super.getFullNames());
	}

	@Override
	public String getAuthorityId() {

		return(super.getAuthorityId());
	}

	@Override
	public Date getDate() {

		return(super.getDate());
	}

	@Override
	public Date getExpirationDate() {

		return(super.getExpirationDate());
	}

	@Override
	public String getUserIdentifier() {

		return(super.getUserIdentifier());
	}

	@Override
	public Xri getChildXri(String localName) throws XriStoreException {

		return(super.getChildXri(localName));
	}

	@Override
	public String getXriAttribute(String key) throws XriStoreException {

		return(super.getXriAttribute(key));
	}

	@Override
	public boolean hasXriAttribute(String key) throws XriStoreException {

		return(super.hasXriAttribute(key));
	}

	public void setXriAttribute(String key, String value) throws XriStoreException {

		super.setXriAttribute(key, value);
	}
	
	@Override
	public String getAuthorityAttribute(String key) throws XriStoreException {

		return(super.getAuthorityAttribute(key));
	}

	@Override
	public boolean hasAuthorityAttribute(String key) throws XriStoreException {

		return(super.hasAuthorityAttribute(key));
	}

	public void setAuthorityAttribute(String key, String value) throws XriStoreException {

		super.setAuthorityAttribute(key, value);
	}

	@Override
	public List<String> getParentAliases() {

		return(null);
	}

	@Override
	public List<String> getAliases() {

		return(super.getAliases());
	}

	@Override
	public boolean isStale() {

		return(false);
	}

	@Override
	public CanonicalID getCanonicalID() throws XriStoreException {

		if (this.canonicalID == null) {

			this.canonicalID = super.getCanonicalID();
		}

		return(this.canonicalID);
	}

	@Override
	public CanonicalEquivID getCanonicalEquivID() throws XriStoreException {

		if (this.canonicalEquivID == null) {

			try {

				EppXriAuthority eppXriAuthority = this.getEppXriAuthority();
				if (eppXriAuthority.getCanonicalEquivID() == null) return(null);
				this.canonicalEquivID = new CanonicalEquivID(eppXriAuthority.getCanonicalEquivID());
			} catch (EppToolsException ex) {

				throw new XriStoreException(ex.getMessage(), ex);
			}
		}

		return(this.canonicalEquivID);
	}

	@Override
	public String getExtension() throws XriStoreException {

		if (this.extension == null) {

			try {

				EppXriAuthority eppXriAuthority = this.getEppXriAuthority();
				this.extension = eppXriAuthority.getExtension();
			} catch (EppToolsException ex) {

				throw new XriStoreException(ex.getMessage(), ex);
			}
		}

		return(this.extension);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EquivID> getEquivIDs() throws XriStoreException {

		if (this.equivIDs == null) {

			try {

				EppXriAuthority eppXriAuthority = this.getEppXriAuthority();
				List<EppXriSynonym> eppXriSynonyms = eppXriAuthority.getEquivID();
				this.equivIDs = new ArrayList<EquivID> (eppXriSynonyms.size());
				for (EppXriSynonym eppXriSynonym : eppXriSynonyms) this.equivIDs.add(EppTools.makeEquivID(eppXriSynonym));
			} catch (EppToolsException ex) {

				throw new XriStoreException(ex.getMessage(), ex);
			}
		}

		return(this.equivIDs);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ref> getRefs() throws XriStoreException {

		if (this.refs == null) {

			try {

				EppXriAuthority eppXriAuthority = this.getEppXriAuthority();
				List<EppXriRef> eppXriRefs = eppXriAuthority.getRef();
				this.refs = new ArrayList<Ref> (eppXriRefs.size());
				for (EppXriRef eppXriRef : eppXriRefs) this.refs.add(EppTools.makeRef(eppXriRef));
			} catch (EppToolsException ex) {

				throw new XriStoreException(ex.getMessage(), ex);
			}
		}

		return(this.refs);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Redirect> getRedirects() throws XriStoreException {

		if (this.redirects == null) {

			try {

				EppXriAuthority eppXriAuthority = this.getEppXriAuthority();
				List<EppXriURI> eppXriURIs = eppXriAuthority.getRedirect();
				this.redirects = new ArrayList<Redirect> (eppXriURIs.size());
				for (EppXriURI eppXriURI : eppXriURIs) this.redirects.add(EppTools.makeRedirect(eppXriURI));
			} catch (EppToolsException ex) {

				throw new XriStoreException(ex.getMessage(), ex);
			}
		}

		return(this.redirects);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Service> getServices() throws XriStoreException {

		if (this.services == null) {

			try {

				EppXriAuthority eppXriAuthority = this.getEppXriAuthority();
				List<EppXriServiceEndpoint> eppXriServiceEndpoints = eppXriAuthority.getServiceEndpoint();
				this.services = new ArrayList<Service> (eppXriServiceEndpoints.size());
				for (EppXriServiceEndpoint eppXriServiceEndpoint : eppXriServiceEndpoints) this.services.add(EppTools.makeService(eppXriServiceEndpoint));
			} catch (EppToolsException ex) {

				throw new XriStoreException(ex.getMessage(), ex);
			}
		}

		return(this.services);
	}

	@Override
	public void setCanonicalID(CanonicalID canonicalID) throws XriStoreException {

		throw new XriStoreException("Cannot set canonical ID in GRS.");
	}

	@Override
	public void setCanonicalEquivID(CanonicalEquivID canonicalEquivID) throws XriStoreException {

		try {

			if (this.getCanonicalEquivID() != null) this.eppTools.deleteCanonicalEquivID(this.gcs, this.grsAuthorityId, this.grsAuthorityPassword, this.canonicalEquivID);
			this.eppTools.setCanonicalEquivID(this.gcs, this.grsAuthorityId, this.grsAuthorityPassword, canonicalEquivID);
		} catch (EppToolsException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}

		super.setCanonicalEquivID(canonicalEquivID);
	}

	@Override
	public void setExtension(String extension) throws XriStoreException {

		try {

			this.eppTools.setExtension(this.gcs, this.grsAuthorityId, this.grsAuthorityPassword, extension);
		} catch (EppToolsException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}

		super.setExtension(extension);
	}

	@Override
	public void addEquivID(EquivID equivID) throws XriStoreException {

		try {

			this.eppTools.addEquivIDs(this.gcs, this.grsAuthorityId, this.grsAuthorityPassword, new EquivID[] { equivID });
		} catch (EppToolsException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}

		super.addEquivID(equivID);
	}

	@Override
	public void addRef(Ref ref) throws XriStoreException {

		try {

			this.eppTools.addRefs(this.gcs, this.grsAuthorityId, this.grsAuthorityPassword, new Ref[] { ref });
		} catch (EppToolsException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}

		super.addRef(ref);
	}

	@Override
	public void addRedirect(Redirect redirect) throws XriStoreException {

		try {

			this.eppTools.addRedirects(this.gcs, this.grsAuthorityId, this.grsAuthorityPassword, new Redirect[] { redirect });
		} catch (EppToolsException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}

		super.addRedirect(redirect);
	}

	@Override
	public void addService(Service service) throws XriStoreException {

		try {

			this.eppTools.addServices(this.gcs, this.grsAuthorityId, this.grsAuthorityPassword, new Service[] { service });
		} catch (EppToolsException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}

		super.addService(service);
	}

	@Override
	public void addServices(Service[] services) throws XriStoreException {

		try {

			this.eppTools.addServices(this.gcs, this.grsAuthorityId, this.grsAuthorityPassword, services);
		} catch (EppToolsException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}

		super.addServices(services);
	}

	@Override
	public void deleteCanonicalEquivID() throws XriStoreException {

		try {

			CanonicalEquivID canonicalEquivID = this.getCanonicalEquivID();

			if (canonicalEquivID != null) this.eppTools.deleteCanonicalEquivID(this.gcs, this.grsAuthorityId, this.grsAuthorityPassword, canonicalEquivID);
		} catch (EppToolsException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}

		super.deleteCanonicalEquivID();
	}

	@Override
	public void deleteExtension() throws XriStoreException {

		try {

			this.eppTools.setExtension(this.gcs, this.grsAuthorityId, this.grsAuthorityPassword, "");
		} catch (EppToolsException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}

		super.deleteExtension();
	}

	@Override
	public void deleteEquivID(EquivID equivID) throws XriStoreException {

		try {

			this.eppTools.deleteEquivIDs(this.gcs, this.grsAuthorityId, this.grsAuthorityPassword, new EquivID[] { equivID });
		} catch (EppToolsException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}

		super.deleteEquivID(equivID);
	}

	@Override
	public void deleteRef(Ref ref) throws XriStoreException {

		try {

			this.eppTools.deleteRefs(this.gcs, this.grsAuthorityId, this.grsAuthorityPassword, new Ref[] { ref });
		} catch (EppToolsException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}

		super.deleteRef(ref);
	}

	@Override
	public void deleteRedirect(Redirect redirect) throws XriStoreException {

		try {

			this.eppTools.deleteRedirects(this.gcs, this.grsAuthorityId, this.grsAuthorityPassword, new Redirect[] { redirect });
		} catch (EppToolsException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}

		super.deleteRedirect(redirect);
	}

	@Override
	public void deleteService(Service service) throws XriStoreException {

		try {

			this.eppTools.deleteServices(this.gcs, this.grsAuthorityId, this.grsAuthorityPassword, new Service[] { service });
		} catch (EppToolsException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}

		super.deleteService(service);
	}

	@Override
	public void deleteAllServices() throws XriStoreException {

		try {

			List<Service> serviceList = this.getServices();
			if (serviceList.size() < 1) return;

			this.eppTools.deleteServices(this.gcs, this.grsAuthorityId, this.grsAuthorityPassword, serviceList.toArray(new Service[serviceList.size()]));
		} catch (EppToolsException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}

		super.deleteAllServices();
	}

	@Override
	public void deleteStandardServices() throws XriStoreException {

		try {

			List<Service> serviceList = this.getServices();
			if (serviceList.size() < 1) return;

			List<Service> removeList = new ArrayList<Service> (serviceList);

			for (Iterator<Service> services = removeList.iterator(); services.hasNext(); ) {

				Service service = services.next();

				if (ServiceUtil.isStandard(service)) continue;

				services.remove();
			}
			if (removeList.size() < 1) return;

			this.eppTools.deleteServices(this.gcs, this.grsAuthorityId, this.grsAuthorityPassword, removeList.toArray(new Service[removeList.size()]));
		} catch (EppToolsException ex) {

			throw new XriStoreException(ex.getMessage(), ex);
		}

		super.deleteStandardServices();
	}

	/*
	 * Helper methods
	 */

	public EppXriAuthority getEppXriAuthority() throws EppToolsException {

		if (this.eppXriAuthority == null) {

			this.eppXriAuthority = this.eppTools.infoAuthority(this.gcs, this.grsAuthorityId, false);
		}

		return(this.eppXriAuthority);
	}

	public EppXriName getEppXriName() throws EppToolsException {

		if (this.eppXriName == null) {

			this.eppXriName = this.eppTools.infoIname(this.gcs, this.getLocalName());
		}

		return(this.eppXriName);
	}
}
