package pds.dictionary;

import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

public class PdsDictionary {

	public static final XRI3Segment XRI_NAME = new XRI3Segment("" + Multiplicity.collectionArcXri(new XRI3SubSegment("+name")));
	public static final XRI3Segment XRI_STREET = new XRI3Segment("" + Multiplicity.collectionArcXri(new XRI3SubSegment("+street")));
	public static final XRI3Segment XRI_POSTAL_CODE = new XRI3Segment("" + Multiplicity.collectionArcXri(new XRI3SubSegment("+postal.code")));
	public static final XRI3Segment XRI_CITY = new XRI3Segment("" + Multiplicity.collectionArcXri(new XRI3SubSegment("+city")));
	public static final XRI3Segment XRI_STATE_OR_PROVINCE = new XRI3Segment("" + Multiplicity.collectionArcXri(new XRI3SubSegment("+state.or.province")));
	public static final XRI3Segment XRI_COUNTRY = new XRI3Segment("" + Multiplicity.collectionArcXri(new XRI3SubSegment("+country")));
	public static final XRI3Segment XRI_TEL = new XRI3Segment("" + Multiplicity.collectionArcXri(new XRI3SubSegment("+tel")));
	public static final XRI3Segment XRI_FAX = new XRI3Segment("" + Multiplicity.collectionArcXri(new XRI3SubSegment("+fax")));
	public static final XRI3Segment XRI_EMAIL = new XRI3Segment("" + Multiplicity.collectionArcXri(new XRI3SubSegment("+email")));
	public static final XRI3Segment XRI_GENDER = new XRI3Segment("" + Multiplicity.attributeSingletonArcXri(new XRI3SubSegment("+gender")));
	public static final XRI3Segment XRI_DATE_OF_BIRTH = new XRI3Segment("" + Multiplicity.attributeSingletonArcXri(new XRI3SubSegment("+date.of.birth")));
	public static final XRI3Segment XRI_LANGUAGE = new XRI3Segment("" + Multiplicity.collectionArcXri(new XRI3SubSegment("+language")));
	public static final XRI3Segment XRI_TIMEZONE = new XRI3Segment("" + Multiplicity.attributeSingletonArcXri(new XRI3SubSegment("+timezone")));

	public static final XRI3Segment[] DICTIONARY_PERSON = new XRI3Segment[] {

		XRI_NAME,
		XRI_STREET,
		XRI_POSTAL_CODE,
		XRI_CITY,
		XRI_STATE_OR_PROVINCE,
		XRI_COUNTRY,
		XRI_TEL,
		XRI_FAX,
		XRI_EMAIL,
		XRI_GENDER,
		XRI_DATE_OF_BIRTH,
		XRI_LANGUAGE,
		XRI_TIMEZONE
	};
}
