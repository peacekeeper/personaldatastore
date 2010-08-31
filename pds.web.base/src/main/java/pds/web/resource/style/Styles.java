package pds.web.resource.style;

import nextapp.echo.app.StyleSheet;
import nextapp.echo.app.serial.SerialException;
import nextapp.echo.app.serial.StyleSheetLoader;

/**
 * Look-and-feel information.
 */
public class Styles {

	/**
	 * Default application style sheet.
	 */
	public static final StyleSheet DEFAULT_STYLE_SHEET;

	static {

		try {

			DEFAULT_STYLE_SHEET = StyleSheetLoader.load(Styles.class.getResourceAsStream("Default.stylesheet"), Styles.class.getClassLoader());
			if (DEFAULT_STYLE_SHEET == null) throw new RuntimeException("Cannot find stylesheet.");
		} catch (SerialException ex) {

			throw new RuntimeException(ex);
		}
	}
}
