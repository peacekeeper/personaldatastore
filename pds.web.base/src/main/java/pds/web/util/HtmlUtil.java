package pds.web.util;

public class HtmlUtil {

	private HtmlUtil() {

	}

	public static String htmlEncode(String string, boolean encodeTags, boolean encodeNewlines) {

		if (string == null) return(null);

		String newString = string;

		if (encodeTags) {

			newString = newString
			.replaceAll("<", "&lt;")
			.replaceAll(">", "&gt;");
		}

		if (encodeNewlines) {

			newString = newString
			.replaceAll("\r\n", "<br>")
			.replaceAll("\n", "<br>");
		}

		return(newString);
	}
}
