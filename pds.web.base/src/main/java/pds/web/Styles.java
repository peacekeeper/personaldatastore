package pds.web;

import nextapp.echo.app.ImageReference;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.StyleSheet;
import nextapp.echo.app.serial.SerialException;
import nextapp.echo.app.serial.StyleSheetLoader;

/**
 * Look-and-feel information.
 */
public class Styles {

    public static final String STYLE_PATH = "/pds/web/resource/style/";
    public static final String IMAGE_PATH = "/pds/web/resource/image/";
    public static final String GENERAL_ICON_PATH = IMAGE_PATH + "icon/general/";

    /**
     * Default application style sheet.
     */
    public static final StyleSheet DEFAULT_STYLE_SHEET;
    static {
        try {
            DEFAULT_STYLE_SHEET = StyleSheetLoader.load(STYLE_PATH + "Default.stylesheet", 
                    Thread.currentThread().getContextClassLoader());
        } catch (SerialException ex) {
            throw new RuntimeException(ex);
        }
    }

    // Images
    public static final ImageReference ICON_24_NO = new ResourceImageReference(GENERAL_ICON_PATH + "Icon24No.gif");
    public static final ImageReference ICON_24_YES = new ResourceImageReference(GENERAL_ICON_PATH + "Icon24Yes.gif");
}
