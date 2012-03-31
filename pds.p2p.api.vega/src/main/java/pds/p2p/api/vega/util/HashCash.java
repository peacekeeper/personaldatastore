package pds.p2p.api.vega.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;

public class HashCash {

//	private static final int[] STRENGTH_MASKS = new int[] { 255, 255, 15 };
	private static final int[] STRENGTH_MASKS = new int[] { };
	private static final Pattern PATTERN_HASHCASH = Pattern.compile("^(.+?):(.+?):(.+?)$");

	private static Random random = new Random();

	private Date date;
	private String to;
	private byte[] code;

	public HashCash(Date date, String to) { 

		this.date = date;
		this.to = to;

		this.calculate();
	}

	public HashCash() {

	}

	public static HashCash fromString(String string) {

		HashCash hashCash = new HashCash();
		Matcher matcher = PATTERN_HASHCASH.matcher(string);
		if (! matcher.matches()) return(null);

		hashCash.date = new Date(Long.parseLong(matcher.group(1)));
		try {
			hashCash.to = URLDecoder.decode(matcher.group(2), "UTF-8");
		} catch (UnsupportedEncodingException ex) { }
		hashCash.code = Base64.decodeBase64(matcher.group(3));

		return(hashCash);
	}

	public boolean isValid() {

		String string = this.toString();
		byte[] hash;

		try {

			MessageDigest digest = MessageDigest.getInstance("MD5");
			hash = digest.digest(string.getBytes("UTF-8"));
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}

		for (int i=0; i<STRENGTH_MASKS.length; i++) if (((hash[i]+128) & STRENGTH_MASKS[i]) != 0) return(false);

		return(true);
	}

	public void calculate() {

		this.code = new byte[20]; 

		do {

			random.nextBytes(this.code);
		} while (! this.isValid());
	}

	public Date getDate() {

		return(this.date);
	}

	public void setDate(Date date) {

		this.date = date;
	}

	public String getTo() {

		return(this.to);
	}

	public void setTo(String to) {

		this.to = to;
	}

	public void setCode(byte[] code) {

		this.code = code;
	}

	public byte[] getCode() {

		return(this.code);
	}

	@Override
	public String toString() {

		StringBuffer buffer = new StringBuffer();

		buffer.append(Long.toString(this.date.getTime()));
		buffer.append(":");
		try {
			buffer.append(URLEncoder.encode(this.to, "UTF-8"));
		} catch (UnsupportedEncodingException ex) { }
		buffer.append(":");
		buffer.append(new String(Base64.encodeBase64(this.code)));

		return(buffer.toString());
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof HashCash)) return(false);
		if (object == this) return(true);

		HashCash other = (HashCash) object;

		if (this.date == null && other.date != null) return(false);
		if (this.date != null && ! this.date.equals(other.date)) return(false);

		if (this.to == null && other.to != null) return(false);
		if (this.to != null && ! this.to.equals(other.to)) return(false);

		if (this.code == null && other.code != null) return(false);
		if (this.code != null && ! this.code.equals(other.code)) return(false);

		return(true);
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + (this.date == null ? 0 : this.date.hashCode());
		hashCode = (hashCode * 31) + (this.to == null ? 0 : this.to.hashCode());
		hashCode = (hashCode * 31) + (this.code == null ? 0 : this.code.hashCode());

		return(hashCode);
	}
}
