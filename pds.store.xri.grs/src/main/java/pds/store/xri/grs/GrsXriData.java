package pds.store.xri.grs;

import pds.store.xri.openxri.OpenxriXriData;

/**
 * The user data needed for GRS i-names. This extends OpenxriXriData, which means
 * that it includes the user data needed for OpenXRI i-names.
 * 
 * - Registering a new i-name:
 *   - A user identifier
 *   - Name, street, postal code, city, country code, primary voice, primary email  
 * 
 * - Registering a new i-name as a synonym of an existing i-name
 *   - A user identifier
 *
 * - Transferring an existing i-name
 *   - A user identifier
 */
public class GrsXriData extends OpenxriXriData {

	private static final long serialVersionUID = -7311236926416670990L;

	private String name;
	private String organization;
	private String[] street;
	private String postalCode;
	private String city;
	private String state;
	private String countryCode;
	private String primaryVoice;
	private String secondaryVoice;
	private String fax;
	private String primaryEmail;
	private String secondaryEmail;
	private String pager;

	private String trusteeEscrowAgent;
	private String trusteeContactAgent;

	public GrsXriData() {

		super();

		this.name = null;
		this.organization = null;
		this.street = null;
		this.postalCode = null;
		this.city = null;
		this.state = null;
		this.countryCode = null;
		this.primaryVoice = null;
		this.secondaryVoice = null;
		this.fax = null;
		this.primaryEmail = null;
		this.secondaryEmail = null;
		this.pager = null;
		this.trusteeEscrowAgent = null;
		this.trusteeEscrowAgent = null;
	}

	@Override
	public boolean isCompleteForRegister() {

		if (! super.isCompleteForRegister()) return(false);

		if (this.name == null) return(false);
		if (this.street == null || this.street.length < 1) return(false);
		if (this.postalCode == null) return(false);
		if (this.city == null) return(false);
		if (this.countryCode == null) return(false);
		if (this.primaryVoice == null) return(false);
		if (this.primaryEmail == null) return(false);

		return(true);
	}

	@Override
	public boolean isCompleteForRegisterSynonym() {

		if (! super.isCompleteForRegisterSynonym()) return(false);

		return(true);
	}

	@Override
	public boolean isCompleteForTransfer() {

		if (! super.isCompleteForTransfer()) return(false);

		return(true);
	}

	public String getName() {

		return(this.name);
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getOrganization() {

		return(this.organization);
	}

	public void setOrganization(String organization) {

		this.organization = organization;
	}

	public String[] getStreet() {

		return(this.street);
	}

	public void setStreet(String[] street) {

		this.street = street;
	}

	public String getPostalCode() {

		return(this.postalCode);
	}

	public void setPostalCode(String postalCode) {

		this.postalCode = postalCode;
	}

	public String getCity() {

		return(this.city);
	}

	public void setCity(String city) {

		this.city = city;
	}

	public String getState() {

		return(this.state);
	}

	public void setState(String state) {

		this.state = state;
	}

	public String getCountryCode() {

		return(this.countryCode);
	}

	public void setCountryCode(String countryCode) {

		this.countryCode = countryCode;
	}

	public String getPrimaryVoice() {

		return(this.primaryVoice);
	}

	public void setPrimaryVoice(String primaryVoice) {

		this.primaryVoice = primaryVoice;
	}

	public String getSecondaryVoice() {

		return(this.secondaryVoice);
	}

	public void setSecondaryVoice(String secondaryVoice) {

		this.secondaryVoice = secondaryVoice;
	}

	public String getFax() {

		return(this.fax);
	}

	public void setFax(String fax) {

		this.fax = fax;
	}

	public String getPrimaryEmail() {

		return(this.primaryEmail);
	}

	public void setPrimaryEmail(String primaryEmail) {

		this.primaryEmail = primaryEmail;
	}

	public String getSecondaryEmail() {

		return(this.secondaryEmail);
	}

	public void setSecondaryEmail(String secondaryEmail) {

		this.secondaryEmail = secondaryEmail;
	}

	public String getPager() {

		return(this.pager);
	}

	public void setPager(String pager) {

		this.pager = pager;
	}

	public String getTrusteeEscrowAgent() {

		return(this.trusteeEscrowAgent);
	}

	public void setTrusteeEscrowAgent(String trusteeEscrowAgent) {

		this.trusteeEscrowAgent = trusteeEscrowAgent;
	}

	public String getTrusteeContactAgent() {

		return(this.trusteeContactAgent);
	}

	public void setTrusteeContactAgent(String trusteeContactAgent) {

		this.trusteeContactAgent = trusteeContactAgent;
	}
}
