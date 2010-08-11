package pds.store.xri.openxri;

import org.openxri.xml.XRD;

import pds.store.xri.XriData;

/**
 * The user data needed for OpenXRI i-names.
 * 
 * - Registering a new i-name:
 *   - A user identifier
 * 
 * - Registering a new i-name as a synonym of an existing i-name
 *   - A user identifier
 *
 * - Transferring an existing i-name
 *   - A user identifier
 */
public class OpenxriXriData implements XriData {

	private static final long serialVersionUID = 5342930246867286526L;

	private String userIdentifier;
	private XRD xrd;

	public OpenxriXriData() {

		this.userIdentifier = null;
		this.xrd = null;
	}

	public boolean isCompleteForRegister() {

		if (this.userIdentifier == null) return(false);

		return(true);
	}

	public boolean isCompleteForRegisterSynonym() {

		if (this.userIdentifier == null) return(false);

		return(true);
	}

	public boolean isCompleteForTransfer() {

		if (this.userIdentifier == null) return(false);

		return(true);
	}

	public String getUserIdentifier() {

		return(this.userIdentifier);
	}

	public void setUserIdentifier(String userIdentifier) {

		this.userIdentifier = userIdentifier;
	}

	public XRD getXrd() {

		return(this.xrd);
	}

	public void setXrd(XRD xrd) {

		this.xrd = xrd;
	}

	@Override
	public String toString() {

		return(this.userIdentifier);
	}
}
