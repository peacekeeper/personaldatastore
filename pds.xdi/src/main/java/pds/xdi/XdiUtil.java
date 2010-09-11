package pds.xdi;

import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3SubSegment;

/**
 * Utility methods for the Store.
 */
public class XdiUtil {

	public static XRI3SubSegment randomSubSegment() {

		return new XRI3SubSegment("$" + UUID.randomUUID().toString().replace("-", "."));
	}

	public static String hashAndSaltPass(String pass) {

		if (pass == null) throw new NullPointerException();

		// create hash for password

		String salt = "" + randomChar() + randomChar() + randomChar();
		String hashedAndSaltedPass = DigestUtils.shaHex(salt + pass);

		return salt + hashedAndSaltedPass;
	}

	public static boolean checkPass(String pass, String claimedPass) {

		Matcher matcher = Pattern.compile("^(...)(.+?)$").matcher(pass);
		if (! matcher.matches()) return(false);
		String salt = matcher.group(1);
		String hashedAndSaltedPass = matcher.group(2);
		return DigestUtils.shaHex(salt + claimedPass).equals(hashedAndSaltedPass);
	}

	public static char randomChar() {

		final char[] chars = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
		return chars[new Random().nextInt(chars.length)];
	}

	public static XRI3 extractParentXri(XRI3 xri) {

		StringBuffer buffer = new StringBuffer();

		if (xri.hasPath()) {

			buffer.append(xri.getAuthority());

			for (int i=0; i<xri.getPath().getNumSegments() - 1; i++) {

				buffer.append("/");
				buffer.append(xri.getPath().getSegment(i).toString());
			}
		} else {

			return null;
		}

		return new XRI3(buffer.toString());
	}
/*
	public static XRI3Segment extractParentXriSegment(XRI3Segment xri) {

		StringBuffer buffer = new StringBuffer();

		if (xri.getNumSubSegments() > 1) {

			for (int i=0; i<xri.getNumSubSegments() - 1; i++) {

				buffer.append(xri.getSubSegment(i).toString());
			}
		} else {

			return null;
		}

		return new XRI3Segment(buffer.toString());
	}

	public static XRI3Segment extractLocalXriSegment(XRI3Segment xri) {

		if (xri.getNumSubSegments() > 0) {

			return new XRI3Segment("" + xri.getLastSubSegment());
		} else {

			return null;
		}
	}*/
}
