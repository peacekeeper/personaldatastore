package pds.store.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Utility methods for the Store.
 */
public class StoreUtil {

	/**
	 * Calculates a random password or recovery code.
	 * @param length The length of the random password or recovery code.
	 * @return A random password or recovery code.
	 */
	public static String randomPass(int length) {
		
		byte b[] = new byte[length];
		for (int i=0; i<length; i++) b[i] = (byte)((int)(Math.random() * ('z' - 'a') + 'a'));

		return(new String(b));
	}

	/**
	 * Calculates a random password or recovery code with default length.
	 * @return A random password or recovery code.
	 */
	public static String randomPass() {
		
		return(randomPass(6));
	}
	
	/**
	 * Calculates a hashed password suitable for a user.
	 * @param pass A password.
	 * @return The hashed password.
	 */
	public static String hashPass(String pass) {

		// create hash for password

		String hashedPass;

		if (pass == null) {

			hashedPass = null;
		} else {

			try {

				MessageDigest digest = MessageDigest.getInstance("SHA-1");
				digest.reset();
				digest.update(pass.getBytes());
				hashedPass = new String(Base64.encodeBase64(digest.digest()));
			} catch (NoSuchAlgorithmException ex) {

				throw new RuntimeException("Cannot calculate password hash.", ex);
			}
		}

		return(hashedPass);
	}

	/**
	 * Checks if a claimed pass is correct.
	 * @param pass The pass stored in iserviceStore.
	 * @param claimedPass A claimed pass.
	 * @return True, if the claimed pass is correct.
	 */
	public static boolean checkPass(String pass, String claimedPass) {

		if (pass.startsWith("linksafe--")) {

			Matcher matcher = Pattern.compile("^linksafe--(.+?)--(.+?)$").matcher(pass);
			if (! matcher.matches()) return(false);
			String linksafeSalt = matcher.group(1);
			String linksafePass = matcher.group(2);
			String linksafeSaltedPass = linksafeSaltedHash(linksafeSalt + linksafeSaltedHash(claimedPass));
			return(linksafePass.equals(linksafeSaltedPass));
		} else {

			return(hashPass(claimedPass).equals(pass));
		}
	}

	private static String linksafeSaltedHash(String string) {

		return(DigestUtils.shaHex("@#AF$RQETQEE" + "--" + string + "--"));
	}
}
