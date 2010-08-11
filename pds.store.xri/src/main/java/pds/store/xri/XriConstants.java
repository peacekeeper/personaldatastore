package pds.store.xri;

/**
 * Various constants used by the XriStore
 */
public class XriConstants {

	/**
	 * This attribute is used to store creation dates on OpenXRI authorities and
	 * subsegments.
	 */
	public static final String ATTRIBUTE_KEY_DATE = "date";

	/**
	 * This attribute is used to store expiration dates on OpenXRI authorities and
	 * subsegments.
	 */
	public static final String ATTRIBUTE_KEY_EXPIRATIONDATE = "expirationdate";

	/**
	 * This attribute is used to store the GRS authority ID on an OpenXRI
	 * authority.
	 */
	public static final String ATTRIBUTE_GRS_AUTHORITYID = "grs-authorityid";

	/**
	 * This attribute is used to store the authority password on an OpenXRI
	 * authority.
	 */
	public static final String ATTRIBUTE_GRS_AUTHORITYPASSWORD = "grs-authoritypassword";

	/**
	 * This attribute is used to store GRS transfer tokens on an OpenXRI
	 * authority.
	 */
	public static final String ATTRIBUTE_GRS_TRANSFERTOKEN = "grs-transfertoken";

	private XriConstants() { }
}
