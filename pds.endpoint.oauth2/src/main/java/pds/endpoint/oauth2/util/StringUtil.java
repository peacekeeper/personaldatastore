package pds.endpoint.oauth2.util;

import java.util.Collection;
import java.util.Iterator;

public class StringUtil {

	private StringUtil() {

	}

	public static String join(Collection<String> s, String delimiter) {

		StringBuffer buffer = new StringBuffer();

		for (Iterator<String> i = s.iterator(); i.hasNext(); ) {

			buffer.append(i.next());
			if (i.hasNext()) buffer.append(delimiter);
		}

		return buffer.toString();
	}
}
