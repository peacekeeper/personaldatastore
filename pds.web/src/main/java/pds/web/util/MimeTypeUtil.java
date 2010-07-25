package pds.web.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MimeTypeUtil {

	private static Map<String, String> EXTENSION_MIMETYPE_MAP;

	static {

		EXTENSION_MIMETYPE_MAP = new HashMap<String, String> ();
		EXTENSION_MIMETYPE_MAP.put(".png", "image/png");
		EXTENSION_MIMETYPE_MAP.put(".gif", "image/gif");
		EXTENSION_MIMETYPE_MAP.put(".tif", "image/tiff");
		EXTENSION_MIMETYPE_MAP.put(".tiff", "image/tiff");
		EXTENSION_MIMETYPE_MAP.put(".jpg", "image/jpeg");
		EXTENSION_MIMETYPE_MAP.put(".jpeg", "image/jpeg");
		EXTENSION_MIMETYPE_MAP.put(".bmp", "image/bmp");
	}

	private MimeTypeUtil() {

	}

	public static String guessMimeTypeFromFilename(String filename) {

		for (Entry<String, String> entry : EXTENSION_MIMETYPE_MAP.entrySet()) {

			if (filename.toLowerCase().endsWith(entry.getKey())) return entry.getValue();
		}

		return null;
	}
}
