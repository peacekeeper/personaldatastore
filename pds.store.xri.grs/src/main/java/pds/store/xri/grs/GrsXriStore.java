package pds.store.xri.grs;

import ibrokerkit.epptools4java.EppTools;
import ibrokerkit.epptools4java.EppToolsException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxri.config.ServerConfig;
import org.openxri.exceptions.StoreException;
import org.openxri.store.StoreAttributable;
import org.openxri.store.SubSegment;
import org.openxri.xml.CanonicalID;

import pds.store.xri.Xri;
import pds.store.xri.XriConstants;
import pds.store.xri.XriData;
import pds.store.xri.XriStoreException;
import pds.store.xri.openxri.OpenxriXri;
import pds.store.xri.openxri.OpenxriXriData;
import pds.store.xri.openxri.OpenxriXriStore;

import com.neulevel.epp.xri.EppXriAuthority;
import com.neulevel.epp.xri.EppXriName;
import com.neulevel.epp.xri.EppXriNumber;
import com.neulevel.epp.xri.EppXriNumberAttribute;
import com.neulevel.epp.xri.EppXriSocialData;
import com.neulevel.epp.xri.response.EppResponseDataCreateXriAuthority;
import com.neulevel.epp.xri.response.EppResponseDataCreateXriName;
import com.neulevel.epp.xri.response.EppResponseDataCreateXriNumber;

/**
 * The GrsXriStore is used for creating, retrieving and managing i-names in the GRS.
 * This extends OpenxriXriStore, i.e. changes to i-names in the GRS also affect
 * OpenXRI.
 */
public class GrsXriStore extends OpenxriXriStore {

	private static Log log = LogFactory.getLog(GrsXriStore.class.getName());

	private EppTools eppTools;

	public GrsXriStore(ServerConfig openxriServerConfig, EppTools eppTools) {

		super(openxriServerConfig);

		this.eppTools = eppTools;
	}

	@Override
	public boolean existsXri(Xri parentXri, String localName) throws XriStoreException {

		if (parentXri != null && ! (parentXri instanceof OpenxriXri)) throw new IllegalArgumentException("XRI " + parentXri.toString() + " is not from this store.");

		// look in OpenXRI first. if we have a parent XRI, we're done

		if (super.existsXri(parentXri, localName)) return(true);

		if (parentXri != null) return(false);

		// build the GRS data

		char gcs = localName.charAt(0);

		// look in GRS

		try {

			return(this.eppTools.checkIname(gcs, localName));
		} catch (EppToolsException ex) {

			log.error(ex);
			throw new XriStoreException("Cannot check in GRS for existence of " + localName + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public Xri registerXri(Xri parentXri, String localName, XriData xriData, int years) throws XriStoreException {

		if (parentXri != null && ! (parentXri instanceof OpenxriXri)) throw new IllegalArgumentException("XRI " + parentXri.toString() + " is not valid.");
		if (localName == null) throw new NullPointerException("localName is null");
		if (xriData == null || ! xriData.isCompleteForRegister()) throw new IllegalArgumentException("Incomplete XRI data.");
		if (parentXri instanceof OpenxriXri && ! (xriData instanceof OpenxriXriData)) throw new IllegalArgumentException("Invalid XRI data: " + xriData.getClass().getName());
		if (parentXri == null && ! (xriData instanceof GrsXriData)) throw new IllegalArgumentException("Invalid XRI data.");

		GrsXriData grsXriData = (xriData instanceof GrsXriData) ? (GrsXriData) xriData : null;

		// register the xri in OpenXRI first. if we have a parent xri, we're done.

		OpenxriXri newXri = (OpenxriXri) super.registerXri(parentXri, localName, xriData, years);

		if (parentXri != null) return(newXri);

		// build the GRS data

		char gcs = localName.charAt(0);
		String grsAuthorityId = this.eppTools.makeGrsAuthorityId(gcs, newXri.getAuthorityId());
		String grsAuthorityPassword = EppTools.makeGrsAuthorityPassword();
		EppXriSocialData eppXriSocialData = EppTools.makeEppXriSocialData(grsXriData.getStreet(), grsXriData.getCity(), grsXriData.getState(), grsXriData.getPostalCode(), grsXriData.getCountryCode(), grsXriData.getName(), grsXriData.getOrganization(), grsXriData.getPrimaryVoice(), grsXriData.getSecondaryVoice(), grsXriData.getFax(), grsXriData.getPrimaryEmail(), grsXriData.getSecondaryEmail(), grsXriData.getPager());
		String trusteeEscrowAgent = grsXriData.getTrusteeEscrowAgent();
		String trusteeContactAgent = grsXriData.getTrusteeContactAgent();

		// create authority, i-number and i-name

		EppResponseDataCreateXriAuthority eppResponseDataCreateXriAuthority; 
		EppResponseDataCreateXriNumber eppResponseDataCreateXriNumber;
		EppResponseDataCreateXriName eppResponseDataCreateXriName;
		String inumber;

		try {

			eppResponseDataCreateXriAuthority = this.eppTools.createAuthority(gcs, grsAuthorityId, grsAuthorityPassword, eppXriSocialData, trusteeEscrowAgent, trusteeContactAgent);
			eppResponseDataCreateXriNumber = this.eppTools.createInumber(gcs, grsAuthorityId, grsAuthorityId, years);
			eppResponseDataCreateXriName = this.eppTools.createIname(gcs, localName, grsAuthorityId, years);

			inumber = eppResponseDataCreateXriNumber.getINumber();
		} catch (EppToolsException ex) {

			log.error(ex);
			throw new XriStoreException("Cannot register in GRS " + localName + ": " + ex.getMessage(), ex);
		}

		// update the attributes in OpenXRI

		newXri.setAuthorityAttribute(XriConstants.ATTRIBUTE_GRS_AUTHORITYID, grsAuthorityId);
		newXri.setAuthorityAttribute(XriConstants.ATTRIBUTE_GRS_AUTHORITYPASSWORD, grsAuthorityPassword);
		newXri.setAuthorityAttribute(XriConstants.ATTRIBUTE_KEY_DATE, Long.toString(eppResponseDataCreateXriAuthority.getDateCreated().getTime().getTime()));
		newXri.setXriAttribute(XriConstants.ATTRIBUTE_KEY_DATE, Long.toString(eppResponseDataCreateXriName.getDateCreated().getTime().getTime()));
		newXri.setXriAttribute(XriConstants.ATTRIBUTE_KEY_EXPIRATIONDATE, Long.toString(eppResponseDataCreateXriName.getDateExpired().getTime().getTime()));

		// update the CanonicalID in OpenXRI and create i-number subsegment

		newXri.setCanonicalID(new CanonicalID(inumber));

		try {

			this.store.registerSubsegment(null, inumber, newXri.getAuthority());
		} catch (StoreException ex) {

			log.error(ex);
			throw new XriStoreException("Cannot register in OpenXRI " + inumber + ": " + ex.getMessage(), ex);
		}

		// done

		return(new GrsXri(newXri, this.eppTools, gcs, grsAuthorityId, grsAuthorityPassword, new EppXriAuthority()));
	}

	@Override
	public Xri registerXriSynonym(Xri parentXri, String localName, Xri xri, XriData xriData, int years) throws XriStoreException {

		if (parentXri != null && ! (parentXri instanceof OpenxriXri)) throw new IllegalArgumentException("XRI " + parentXri.toString() + " is not valid.");
		if (localName == null) throw new NullPointerException("localName is null");
		if (xriData == null || ! xriData.isCompleteForRegisterSynonym()) throw new IllegalArgumentException("Incomplete XRI data.");
		if (parentXri instanceof OpenxriXri && ! (xriData instanceof OpenxriXriData)) throw new IllegalArgumentException("Invalid XRI data: " + xriData.getClass().getName());
		if (parentXri == null && ! (xriData instanceof GrsXriData)) throw new IllegalArgumentException("Invalid XRI data: " + xriData.getClass().getName());
		if (parentXri == null && ! (xri instanceof GrsXri)) throw new IllegalArgumentException("Invalid XRI: " + xri.getClass().getName());

		// register the xri in OpenXRI first. if we have a parent xri, we're done.

		OpenxriXri newXri = (OpenxriXri) super.registerXriSynonym(parentXri, localName, xri, xriData, years);

		if (parentXri != null) return(xri);

		// build the GRS data

		char gcs = localName.charAt(0);
		String grsAuthorityId = xri.getAuthorityAttribute(XriConstants.ATTRIBUTE_GRS_AUTHORITYID);
		String grsAuthorityPassword = xri.getAuthorityAttribute(XriConstants.ATTRIBUTE_GRS_AUTHORITYPASSWORD);

		// create i-name

		EppResponseDataCreateXriName eppResponseDataCreateXriName;

		try {

			eppResponseDataCreateXriName = this.eppTools.createIname(gcs, localName, grsAuthorityId, years);
		} catch (EppToolsException ex) {

			log.error(ex);
			throw new XriStoreException("Cannot register in GRS " + localName + ": " + ex.getMessage(), ex);
		}

		// update the attributes in OpenXRI

		newXri.setXriAttribute(XriConstants.ATTRIBUTE_KEY_DATE, Long.toString(eppResponseDataCreateXriName.getDateCreated().getTime().getTime()));
		newXri.setXriAttribute(XriConstants.ATTRIBUTE_KEY_EXPIRATIONDATE, Long.toString(eppResponseDataCreateXriName.getDateExpired().getTime().getTime()));

		// done

		return(new GrsXri(newXri, this.eppTools, gcs, grsAuthorityId, grsAuthorityPassword));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Xri transferAuthorityInRequest(String localName, XriData xriData) throws XriStoreException {

		if (localName == null) throw new NullPointerException("localName is null");
		if (xriData == null || ! xriData.isCompleteForTransfer()) throw new IllegalArgumentException("Incomplete XRI data.");
		if (! (xriData instanceof GrsXriData)) throw new IllegalArgumentException("Invalid XRI data: " + xriData.getClass().getName());

		GrsXriData grsXriData = (xriData instanceof GrsXriData) ? (GrsXriData) xriData : null;

		// transfer the xri in OpenXRI first.

		OpenxriXri newXri = (OpenxriXri) super.transferAuthorityInRequest(localName, xriData);

		// build the GRS data

		char gcs = localName.charAt(0);
		String grsAuthorityId;
		String grsAuthorityPassword = "unknownTransferPending";
		String transferToken;

		// request transfer

		EppXriName eppXriName;
		EppXriAuthority eppXriAuthority;
		List<String> inames;
		String inumber;

		try {

			eppXriName = this.eppTools.infoIname(gcs, localName);
			grsAuthorityId = eppXriName.getAuthorityId();

			transferToken = this.eppTools.transferRequestAuthority(gcs, grsAuthorityId);
			eppXriAuthority = this.eppTools.infoAuthority(gcs, grsAuthorityId, true);

			inames = eppXriAuthority.getIName();
			inumber = ((EppXriNumberAttribute) eppXriAuthority.getINumber().get(0)).getINumber();
		} catch (EppToolsException ex) {

			log.error(ex);
			throw new XriStoreException("Cannot transfer in GRS " + localName + ": " + ex.getMessage(), ex);
		}

		// update the attributes in OpenXRI

		newXri.setAuthorityAttribute(XriConstants.ATTRIBUTE_GRS_AUTHORITYID, grsAuthorityId);
		newXri.setAuthorityAttribute(XriConstants.ATTRIBUTE_GRS_AUTHORITYPASSWORD, grsAuthorityPassword);
		newXri.setAuthorityAttribute(XriConstants.ATTRIBUTE_GRS_TRANSFERTOKEN, transferToken);
		newXri.setAuthorityAttribute(XriConstants.ATTRIBUTE_KEY_DATE, Long.toString(eppXriAuthority.getDateCreated().getTime().getTime()));
		newXri.setXriAttribute(XriConstants.ATTRIBUTE_KEY_DATE, Long.toString(eppXriName.getDateCreated().getTime().getTime()));
		newXri.setXriAttribute(XriConstants.ATTRIBUTE_KEY_EXPIRATIONDATE, Long.toString(eppXriName.getDateExpired().getTime().getTime()));

		// update the CanonicalID in OpenXRI and create i-number and synonym i-names

		newXri.setCanonicalID(new CanonicalID(inumber));

		try {

			this.store.registerSubsegment(null, inumber, newXri.getAuthority());

			for (String iname : inames) {

				if (iname.equals(localName)) continue;

				SubSegment subSegment = this.store.registerSubsegment(null, iname, newXri.getAuthority());

				// set timestamp and user identifier

				eppXriName = this.eppTools.infoIname(iname.charAt(0), iname);

				Map<String, String> subSegmentAttributes = ((StoreAttributable) this.store).getSubSegmentAttributes(subSegment);
				subSegmentAttributes.put(XriConstants.ATTRIBUTE_KEY_DATE, Long.toString(eppXriName.getDateCreated().getTime().getTime()));
				subSegmentAttributes.put(XriConstants.ATTRIBUTE_KEY_EXPIRATIONDATE, Long.toString(eppXriName.getDateExpired().getTime().getTime()));
				((StoreAttributable) this.store).setSubSegmentAttributes(subSegment, subSegmentAttributes);
				((StoreAttributable) this.store).setSubSegmentIndex(subSegment, grsXriData.getUserIdentifier());
			}
		} catch (StoreException ex) {

			log.error(ex);
			throw new XriStoreException("Cannot transfer in OpenXRI " + inumber + ": " + ex.getMessage(), ex);
		} catch (EppToolsException ex) {

			log.error(ex);
			throw new XriStoreException("Cannot transfer in OpenXRI " + inumber + ": " + ex.getMessage(), ex);
		}

		// done

		return(new GrsXri(newXri, this.eppTools, gcs, grsAuthorityId, grsAuthorityPassword, new EppXriAuthority()));
	}

	@Override
	public void transferAuthorityInComplete(Xri xri) throws XriStoreException {

		if (xri == null) throw new NullPointerException();

		// transfer the xri in OpenXRI first.

		super.transferAuthorityInComplete(xri);

		// read the authority password

		char gcs = ((GrsXri) xri).getGcs();
		String grsAuthorityId = ((GrsXri) xri).getGrsAuthorityId();
		String grsAuthorityPassword;

		try {

			EppXriAuthority eppXriAuthority = this.eppTools.infoAuthority(gcs, grsAuthorityId, true);
			grsAuthorityPassword = eppXriAuthority.getAuthInfo().getValue();
		} catch (EppToolsException ex) {

			log.error(ex);
			throw new XriStoreException("Cannot read password of authority " + grsAuthorityId + ": " + ex.getMessage(), ex);
		}

		// update the attributes in OpenXRI

		xri.setAuthorityAttribute(XriConstants.ATTRIBUTE_GRS_AUTHORITYPASSWORD, grsAuthorityPassword);
		xri.setAuthorityAttribute(XriConstants.ATTRIBUTE_GRS_TRANSFERTOKEN, null);
	}

	@Override
	public void transferAuthorityInCanceled(Xri xri) throws XriStoreException {

		if (xri == null) throw new NullPointerException();

		// transfer the xri in OpenXRI first.

		super.transferAuthorityInCanceled(xri);
	}

	@Override
	public void transferAuthorityOutApprove(Xri xri, String token) throws XriStoreException {

		if (xri == null) throw new NullPointerException();

		// transfer the xri in OpenXRI first.

		super.transferAuthorityOutApprove(xri, token);

		// approve transfer

		char gcs = ((GrsXri) xri).getGcs();
		String grsAuthorityId = ((GrsXri) xri).getGrsAuthorityId();
		String grsAuthorityPassword = ((GrsXri) xri).getGrsAuthorityPassword();

		try {

			this.eppTools.transferApproveAuthority(gcs, grsAuthorityId, grsAuthorityPassword, token);
		} catch (EppToolsException ex) {

			log.error(ex);
			throw new XriStoreException("Cannot approve transfer of authority " + grsAuthorityId + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public void transferAuthorityOutReject(Xri xri, String token) throws XriStoreException {

		if (xri == null) throw new NullPointerException();

		// transfer the xri in OpenXRI first.

		super.transferAuthorityOutReject(xri, token);

		// reject transfer

		char gcs = ((GrsXri) xri).getGcs();
		String grsAuthorityId = ((GrsXri) xri).getGrsAuthorityId();
		String grsAuthorityPassword = ((GrsXri) xri).getGrsAuthorityPassword();

		try {

			this.eppTools.transferRejectAuthority(gcs, grsAuthorityId, grsAuthorityPassword, token);
		} catch (EppToolsException ex) {

			log.error(ex);
			throw new XriStoreException("Cannot reject transfer of authority " + grsAuthorityId + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public void transferAuthorityOutComplete(Xri xri) throws XriStoreException {

		if (xri == null) throw new NullPointerException();

		// transfer the xri in OpenXRI first.

		super.transferAuthorityOutComplete(xri);
	}

	@Override
	public void transferAuthorityOutCanceled(Xri xri) throws XriStoreException {

		if (xri == null) throw new NullPointerException();

		// transfer the xri in OpenXRI first.

		super.transferAuthorityOutCanceled(xri);
	}

	@Override
	public Xri transferXriInRequest(String localName, XriData xriData) throws XriStoreException {

		throw new XriStoreException("Transfer IN for individual i-names not currently supported (only for whole authority).");
	}

	@Override
	public void transferXriInComplete(Xri xri) throws XriStoreException {

		if (xri == null) throw new NullPointerException();

		// transfer the xri in OpenXRI first.

		super.transferAuthorityInComplete(xri);
	}

	@Override
	public void transferXriInCanceled(Xri xri) throws XriStoreException {

		if (xri == null) throw new NullPointerException();

		// transfer the xri in OpenXRI first.

		super.transferAuthorityInCanceled(xri);
	}

	@Override
	public void transferXriOutApprove(Xri xri, String token) throws XriStoreException {

		if (xri == null) throw new NullPointerException();

		// transfer the xri in OpenXRI first.

		super.transferXriOutApprove(xri, token);

		// approve transfer

		char gcs = ((GrsXri) xri).getGcs();
		String iname = xri.getFullName();
		String grsAuthorityPassword = ((GrsXri) xri).getGrsAuthorityPassword();

		try {

			this.eppTools.transferApproveIname(gcs, iname, grsAuthorityPassword, token);
		} catch (EppToolsException ex) {

			log.error(ex);
			throw new XriStoreException("Cannot approve transfer of i-name " + iname + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public void transferXriOutReject(Xri xri, String token) throws XriStoreException {

		if (xri == null) throw new NullPointerException();

		// transfer the xri in OpenXRI first.

		super.transferXriOutReject(xri, token);

		// reject transfer

		char gcs = ((GrsXri) xri).getGcs();
		String iname = xri.getFullName();
		String grsAuthorityPassword = ((GrsXri) xri).getGrsAuthorityPassword();

		try {

			this.eppTools.transferRejectIname(gcs, iname, grsAuthorityPassword, token);
		} catch (EppToolsException ex) {

			log.error(ex);
			throw new XriStoreException("Cannot reject transfer of i-name " + iname + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public void transferXriOutComplete(Xri xri) throws XriStoreException {

		if (xri == null) throw new NullPointerException();

		// transfer the xri in OpenXRI first.

		super.transferXriOutComplete(xri);
	}

	@Override
	public void transferXriOutCanceled(Xri xri) throws XriStoreException {

		if (xri == null) throw new NullPointerException();

		// transfer the xri in OpenXRI first.

		super.transferXriOutCanceled(xri);
	}

	@Override
	public Calendar renewXri(Xri xri, int years) throws XriStoreException {

		if (xri == null) throw new NullPointerException();

		// renew the xri in OpenXRI first. 

		super.renewXri(xri, years);

		// renew i-name and i-number

		char gcs = ((GrsXri) xri).getGcs();
		String iname = xri.getFullName();
		String inumber = xri.getCanonicalID().getValue();
		Calendar newExpDate;

		try {

			EppXriName eppXriName = this.eppTools.infoIname(gcs, iname);

			EppXriNumber eppXriNumber = this.eppTools.infoInumber(gcs, inumber); 

			// renew the i-name

			newExpDate = this.eppTools.renewIname(gcs, eppXriName.getIName(), eppXriName.getDateExpired(), years);

			// only renew the i-number if necessary

			if (eppXriNumber.getDateExpired().before(newExpDate)) {

				this.eppTools.renewInumber(gcs, eppXriNumber.getINumber(), eppXriNumber.getDateExpired(), years);
			}
		} catch (EppToolsException ex) {

			log.error(ex);
			throw new XriStoreException("Cannot renew in GRS " + iname + ": " + ex.getMessage(), ex);
		}

		// update the attributes in OpenXRI

		xri.setXriAttribute(XriConstants.ATTRIBUTE_KEY_EXPIRATIONDATE, Long.toString(newExpDate.getTime().getTime()));

		// done

		return(newExpDate);
	}

	@Override
	public void deleteXri(Xri xri) throws XriStoreException {

		if (xri == null) throw new NullPointerException();

		// delete i-name

		char gcs = ((GrsXri) xri).getGcs();
		String iname = xri.getFullName();

		try {

			this.eppTools.deleteIname(gcs, iname);
		} catch (EppToolsException ex) {

			log.error(ex);
			throw new XriStoreException("Cannot delete in GRS " + iname + ": " + ex.getMessage(), ex);
		}

		// delete the xri in OpenXRI.

		super.deleteXri(xri);
	}

	@Override
	public void deleteAuthority(Xri xri) throws XriStoreException {

		if (xri == null) throw new NullPointerException();

		// delete all i-names

		char gcs = ((GrsXri) xri).getGcs();
		List<String> inames = ((GrsXri) xri).getAliases();

		for (String iname : inames) {

			try {

				this.eppTools.deleteIname(gcs, iname);
			} catch (EppToolsException ex) {

				log.error(ex);
				throw new XriStoreException("Cannot delete in GRS " + iname + ": " + ex.getMessage(), ex);
			}
		}

		// delete i-number

		String inumber = xri.getCanonicalID().getValue();

		try {

			this.eppTools.deleteInumber(gcs, inumber);
		} catch (EppToolsException ex) {

			log.error(ex);
			throw new XriStoreException("Cannot delete in GRS " + inumber + ": " + ex.getMessage(), ex);
		}

		// delete the authority in OpenXRI.

		super.deleteAuthority(xri);
	}

	@Override
	public List<Xri> listXris() throws XriStoreException {

		List<Xri> openxriXris = super.listXris();
		List<Xri> inames = new ArrayList<Xri> ();

		for (Xri openxriXri : openxriXris) {

			boolean root = ((OpenxriXri) openxriXri).getParentAuthority() == null;

			if (! root) {

				inames.add(openxriXri);
			} else {

				char gcs = openxriXri.getLocalName().charAt(0);
				String grsAuthorityId = openxriXri.getAuthorityAttribute(XriConstants.ATTRIBUTE_GRS_AUTHORITYID);
				String grsAuthorityPassword = openxriXri.getAuthorityAttribute(XriConstants.ATTRIBUTE_GRS_AUTHORITYPASSWORD);

				if (grsAuthorityId == null || grsAuthorityPassword == null) {

					inames.add(openxriXri);
					continue;
				}

				inames.add(new GrsXri((OpenxriXri) openxriXri, this.eppTools, gcs, grsAuthorityId, grsAuthorityPassword));
			}
		}

		return(inames);
	}

	@Override
	public List<Xri> listRootXris() throws XriStoreException {

		List<Xri> openxriRootXris = super.listRootXris();
		List<Xri> rootInames = new ArrayList<Xri> ();

		for (Xri openxriRootXri : openxriRootXris) {

			boolean root = ((OpenxriXri) openxriRootXri).getParentAuthority() == null;

			if (! root) {

				rootInames.add(openxriRootXri);
			} else {

				char gcs = openxriRootXri.getLocalName().charAt(0);
				String grsAuthorityId = openxriRootXri.getAuthorityAttribute(XriConstants.ATTRIBUTE_GRS_AUTHORITYID);
				String grsAuthorityPassword = openxriRootXri.getAuthorityAttribute(XriConstants.ATTRIBUTE_GRS_AUTHORITYPASSWORD);

				if (grsAuthorityId == null || grsAuthorityPassword == null) {

					rootInames.add(openxriRootXri);
				} else {

					rootInames.add(new GrsXri((OpenxriXri) openxriRootXri, this.eppTools, gcs, grsAuthorityId, grsAuthorityPassword));
				}
			}
		}

		return(rootInames);
	}

	@Override
	public List<Xri> listUserXris(String userIdentifier) throws XriStoreException {

		List<Xri> openxriUserXris = super.listUserXris(userIdentifier);
		List<Xri> userInames = new ArrayList<Xri> ();

		for (Xri openxriUserXri : openxriUserXris) {

			if (((OpenxriXri) openxriUserXri).getParentAliases() != null) {

				userInames.add(openxriUserXri);
			} else {

				char gcs = openxriUserXri.getLocalName().charAt(0);
				String grsAuthorityId = openxriUserXri.getAuthorityAttribute(XriConstants.ATTRIBUTE_GRS_AUTHORITYID);
				String grsAuthorityPassword = openxriUserXri.getAuthorityAttribute(XriConstants.ATTRIBUTE_GRS_AUTHORITYPASSWORD);

				if (grsAuthorityId == null || grsAuthorityPassword == null) {

					userInames.add(openxriUserXri);
					continue;
				}

				userInames.add(new GrsXri((OpenxriXri) openxriUserXri, this.eppTools, gcs, grsAuthorityId, grsAuthorityPassword));
			}
		}

		return(userInames);
	}

	@Override
	public Xri findXri(String xri) throws XriStoreException {

		Xri openxriXri = super.findXri(xri);
		if (openxriXri == null) return(null);

		boolean root = ((OpenxriXri) openxriXri).getParentAuthority() == null;

		if (! root) {

			return(openxriXri);
		} else {

			char gcs = openxriXri.getLocalName().charAt(0);
			String grsAuthorityId = openxriXri.getAuthorityAttribute(XriConstants.ATTRIBUTE_GRS_AUTHORITYID);
			String grsAuthorityPassword = openxriXri.getAuthorityAttribute(XriConstants.ATTRIBUTE_GRS_AUTHORITYPASSWORD);

			if (grsAuthorityId == null || grsAuthorityPassword == null) {

				return(openxriXri);
			} else {

				return(new GrsXri((OpenxriXri) openxriXri, this.eppTools, gcs, grsAuthorityId, grsAuthorityPassword));
			}
		}
	}

	@Override
	public Xri findXriByGrsAuthorityId(String grsAuthorityId) throws XriStoreException {

		Xri openxriXri = super.findXriByGrsAuthorityId(grsAuthorityId);
		if (openxriXri == null) return(null);

		if (((OpenxriXri) openxriXri).getParentAliases() != null) {

			return(openxriXri);
		} else {

			char gcs = openxriXri.getLocalName().charAt(0);
			String grsAuthorityPassword = openxriXri.getAuthorityAttribute(XriConstants.ATTRIBUTE_GRS_AUTHORITYPASSWORD);

			return(new GrsXri((OpenxriXri) openxriXri, this.eppTools, gcs, grsAuthorityId, grsAuthorityPassword));
		}
	}

	@Override
	public String findUserIdentifier(String xri) throws XriStoreException {

		return(super.findUserIdentifier(xri));
	}

	@Override
	public long getXriCount() throws XriStoreException {

		return(super.getXriCount());
	}

	@Override
	public long getAuthorityCount() throws XriStoreException {

		return(super.getAuthorityCount());
	}
}
