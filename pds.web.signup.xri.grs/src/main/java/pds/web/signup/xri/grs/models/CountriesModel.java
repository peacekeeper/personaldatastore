package pds.web.signup.xri.grs.models;

import java.util.HashMap;
import java.util.Map;

import nextapp.echo.app.list.DefaultListModel;

public class CountriesModel extends DefaultListModel {

	private static final long serialVersionUID = 1697235881917061601L;

	public static final String DEFAULT_COUNTRY = "United States";

	public static final String[] COUNTRY_NAMES = { "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory", "British Virgin Islands", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos Islands", "Colombia", "Comoros", "Congo", "Congo", "Cook Islands", "Costa Rica", "Cote D'Ivoire", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Faeroe Islands", "Falkland Islands", "Fiji", "Finland", "France", "French Guiana", "French Polynesia", "French Southern Territories", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guadaloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard & McDonald Islands", "Holy See", "Honduras", "Hong Kong", "Hrvatska", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea", "Korea", "Kuwait", "Kyrgyz Republic", "Lao People's Dem. Rep.", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libyan Arab Jamahiriya", "Liechtenstein", "Lithuania", "Luxembourg", "Macao", "Macedonia", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands Antilles", "Netherlands", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Palestinian Territory", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn Island", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russian Federation", "Rwanda", "St. Helena", "St. Kitts and Nevis", "St. Lucia", "St. Pierre and Miquelon", "St. Vincent and the Gren.", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Serbia and Montenegro", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Georgia and Islands", "Spain", "Sri Lanka", "Sudan", "Suriname", "Svalbard & J.M. Islands", "Swaziland", "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor-Leste", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Virgin Islands", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States Outlying Isl.", "United States", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Viet Nam", "Wallis and Futuna Islands", "Western Sahara", "Yemen", "Zambia", "Zimbabwe" };
	public static final String[] COUNTRY_CODES2 = { "AF", "AL", "DZ", "AS", "AD", "AO", "AI", "AQ", "AG", "AR", "AM", "AW", "AU", "AT", "AZ", "BS", "BH", "BD", "BB", "BY", "BE", "BZ", "BJ", "BM", "BT", "BO", "BA", "BW", "BV", "BR", "IO", "VG", "BN", "BG", "BF", "BI", "KH", "CM", "CA", "CV", "KY", "CF", "TD", "CL", "CN", "CX", "CC", "CO", "KM", "CD", "CG", "CK", "CR", "CI", "CU", "CY", "CZ", "DK", "DJ", "DM", "DO", "EC", "EG", "SV", "GQ", "ER", "EE", "ET", "FO", "FK", "FJ", "FI", "FR", "GF", "PF", "TF", "GA", "GM", "GE", "DE", "GH", "GI", "GR", "GL", "GD", "GP", "GU", "GT", "GN", "GW", "GY", "HT", "HM", "VA", "HN", "HK", "HR", "HU", "IS", "IN", "ID", "IR", "IQ", "IE", "IL", "IT", "JM", "JP", "JO", "KZ", "KE", "KI", "KP", "KR", "KW", "KG", "LA", "LV", "LB", "LS", "LR", "LY", "LI", "LT", "LU", "MO", "MK", "MG", "MW", "MY", "MV", "ML", "MT", "MH", "MQ", "MR", "MU", "YT", "MX", "FM", "MD", "MC", "MN", "MS", "MA", "MZ", "MM", "NA", "NR", "NP", "AN", "NL", "NC", "NZ", "NI", "NE", "NG", "NU", "NF", "MP", "NO", "OM", "PK", "PW", "PS", "PA", "PG", "PY", "PE", "PH", "PN", "PL", "PT", "PR", "QA", "RE", "RO", "RU", "RW", "SH", "KN", "LC", "PM", "VC", "WS", "SM", "ST", "SA", "SN", "CS", "SC", "SL", "SG", "SK", "SI", "SB", "SO", "ZA", "GS", "ES", "LK", "SD", "SR", "SJ", "SZ", "SE", "CH", "SY", "TW", "TJ", "TZ", "TH", "TL", "TG", "TK", "TO", "TT", "TN", "TR", "TM", "TC", "TV", "VI", "UG", "UA", "AE", "GB", "UM", "US", "UY", "UZ", "VU", "VE", "VN", "WF", "EH", "YE", "ZM", "ZW" };
	public static final String[] COUNTRY_PREFIX = { "93", "355", "213", "684", "376", "244", "809", "0", "268", "54", "374", "297", "61", "43", "994", "242", "973", "880", "246", "375", "32", "501", "229", "809", "975", "591", "387", "267", "0", "55", "0", "284", "673", "359", "226", "257", "855", "237", "1", "238", "345", "236", "235", "56", "86", "1", "1", "57", "269", "242", "242", "682", "506", "243", "53", "357", "420", "45", "253", "767", "809", "593", "20", "503", "240", "291", "372", "241", "298", "500", "679", "358", "33", "594", "596", "596", "241", "220", "995", "49", "233", "350", "30", "299", "473", "0", "671", "502", "224", "245", "592", "509", "0", "39", "504", "852", "385", "36", "354", "91", "62", "98", "964", "353", "972", "39", "876", "81", "962", "7", "254", "686", "850", "82", "965", "996", "856", "371", "961", "266", "231", "218", "423", "370", "352", "853", "389", "261", "265", "60", "960", "223", "356", "692", "596", "222", "230", "269", "52", "691", "373", "33", "976", "473", "212", "258", "95", "264", "674", "977", "599", "31", "687", "64", "505", "227", "234", "683", "1", "167", "47", "968", "92", "680", "0", "507", "675", "595", "51", "63", "0", "48", "351", "1", "974", "262", "40", "7", "250", "0", "0", "0", "0", "0", "0", "378", "239", "966", "221", "381", "248", "232", "65", "421", "386", "677", "252", "27", "1", "34", "94", "249", "597", "0", "268", "46", "41", "963", "886", "7", "255", "66", "0", "228", "690", "676", "1", "216", "90", "993", "0", "688", "1", "256", "380", "971", "44", "1", "1", "598", "7", "678", "58", "84", "681", "685", "381", "260", "263" }; 

	private static Map<String, String> NAMES_TO_CODES2 = new HashMap<String, String> ();;
	private static Map<String, String> NAMES_TO_PREFIX = new HashMap<String, String> ();;

	static {

		for (int i=0; i<COUNTRY_NAMES.length; i++) NAMES_TO_CODES2.put(COUNTRY_NAMES[i], COUNTRY_CODES2[i]);
		for (int i=0; i<COUNTRY_NAMES.length; i++) NAMES_TO_PREFIX.put(COUNTRY_NAMES[i], COUNTRY_PREFIX[i]);
	}

	public static String nameToCode2(String name) {

		if (name == null) return(null);
		return(NAMES_TO_CODES2.get(name));
	}

	public static String nameToPrefix(String name) {

		if (name == null) return(null);
		return(NAMES_TO_PREFIX.get(name));
	}

	public CountriesModel() {

		super(COUNTRY_NAMES);
	}
}
