package pds.p2p.api.vega.comm;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pds.p2p.api.vega.VegaFactory;
import pds.p2p.api.vega.util.HashCash;
import rice.p2p.commonapi.Id;
import rice.p2p.past.ContentHashPastContentHandle;
import rice.p2p.past.Past;
import rice.p2p.past.PastContent;
import rice.p2p.past.PastContentHandle;
import rice.p2p.past.PastException;
import rice.p2p.past.gc.GCPast;
import rice.p2p.past.gc.GCPastContentHandle;

public class VegaPastContent implements PastContent {  

	private static final long serialVersionUID = 2539958341171708499L;

	private static Log log = LogFactory.getLog(VegaPastContent.class);

	/*
	 * Here is a list of patterns that define what keys are allowed in the DHT.
	 */

	public static final String[] WORLD_PUBLIC = new String[] {

		// examples:
		// a
		// test
		"^[^\\+\\$=@].*$",

		// examples:
		// +(XXXX)___
		"^[\\+]([^/]+?)___.*$",

		// examples:
		// +(XXXX)/$public___
		// +(XXXX)/$public+something___
		"^[\\+]([^/]+?)/\\$public([\\*!\\+\\$=@][^/]+?)?(/--R|/--L|/--G)?___.*$"
	};
	public static final String[] ALIEN_PUBLIC = new String[] {

		// examples:
		// =someone/$public=markus//...
		// =someone/$public=markus+opinion/'this_sucks'

		"^.+?/\\$public:::xri:::([\\*!\\+\\$=@][^/]+?)?(/.*|___.*)?$",

		// examples:
		// =someone/$public//=markus/...
		// =someone/$public+something//=markus+opinion/...

		"^.+?/\\$public([\\*!\\+\\$=@][^/]+?)?//:::xri:::([\\*!\\+\\$=@][^/]+?)?(/.*|___.*)?$",

		// examples:
		// =someone/$public//=markus///--S___3sWHVSWRQF/jKZd8506+QQ==
		// =someone/$public+something//=markus+opinion///--S___3sWHVSWRQF/jKZd8506+QQ==

		"^.+?/\\$public([\\*!\\+\\$=@][^/]+?)?///--S___:::hash:::$"
	};
	public static final String[] ALIEN_PUBLIC_WITH_ALIEN_VALUE = new String[] {

		// examples:
		// =someone/$public///--S___
		// =someone/$public+something///--S___

		"^.+?/\\$public([\\*!\\+\\$=@][^/]+?)?///--S___$"
	};
	public static final String[] SELF_PUBLIC = new String[] {

		// examples:
		// =markus/+email/'test'
		// =markus+mystory/$text$value/'sometext'

		"^:::xri:::([\\*!\\+\\$=@][^/]+?)?(/.*|___.*)?$"
	};

	private Id id;
	private Long version;
	private String iname;
	private String inumber;
	private String key;
	private String value;
	private String signature;
	private String hashcash;

	public VegaPastContent(Id id, Long version, String iname, String inumber, String key, String value, String signature, String hashcash) {

		this.id = id;
		this.version = version;
		this.iname = iname;
		this.inumber = inumber;
		this.key = key;
		this.value = value;
		this.signature = signature;
		this.hashcash = hashcash;
	}

	public PastContent checkInsert(Id id, PastContent existingContent) throws PastException {

		log.debug("--> checkInsert(" + id.toStringFull() + "," + existingContent + ")");
		log.debug("--> checkInsert: id=" + this.id.toStringFull() + " iname=" + this.iname + " inumber=" + this.inumber + " key=" + this.key + " value=" + this.value + " signature=" + this.signature + " hashcash=" + this.hashcash);

		try {

			// check general message properties

			boolean dataOk = id.equals(this.id) && this.iname != null && this.inumber != null && this.signature != null && this.hashcash != null;

			boolean signatureOk = "1".equals(VegaFactory.getOrion().verify(this.key + this.value, this.signature, this.inumber));

			HashCash hashcash = HashCash.fromString(this.hashcash);
			boolean hashcashOk = hashcash.getTo().equals(this.key) && hashcash.isValid();

			log.debug("--> checkInsert: dataOk=" + dataOk + " signatureOk=" + signatureOk + " hashcashOk=" + hashcashOk);
			if (! dataOk || ! signatureOk || ! hashcashOk) return null;

			// check for alien value; we need this for the permission patterns

			boolean alienValue = false;

			if (this.key.endsWith("___") &&
					this.value.length() > 1 &&
					(this.value.startsWith("+") || this.value.startsWith("-")) &&
					(this.value.substring(1).equals(hash(this.iname)) ||
							this.value.substring(1).equals(hash(this.inumber)))) {

				alienValue = true;
			}

			// special logic for adjusting values for multiPut and multiDelete

			if (this.key.endsWith("___") && this.value.startsWith("+")) {

				if (existingContent instanceof VegaPastContent)
					this.value = addIndexToIndexList(this.value.substring(1), ((VegaPastContent) existingContent).value);
				else
					this.value = this.value.substring(1);

				log.debug("--> checkInsert: +adjusting value to " + this.value);
			}

			if (this.key.endsWith("___") && this.value.startsWith("-")) {

				if (existingContent instanceof VegaPastContent)
					this.value = removeIndexFromIndexList(this.value.substring(1), ((VegaPastContent) existingContent).value);
				else
					this.value = "";

				log.debug("--> checkInsert: -adjusting value to " + this.value);
			}

			// check WORLD_PUBLIC patterns

			boolean worldPublicMatches = false;
			for (String worldPublic : WORLD_PUBLIC) { 

				if (checkKey(worldPublic)) {

					worldPublicMatches = true; 
					break;
				}
			}
			if (worldPublicMatches) {

				log.debug("--> checkInsert: OK: WORLD_PUBLIC");
				return this;
			}

			// check ALIEN_PUBLIC patterns

			boolean alienPublicMatches = false;
			for (String alienPublic : ALIEN_PUBLIC) { 

				if (checkKey(alienPublic)) {

					alienPublicMatches = true; 
					break;
				}
			}
			if (alienPublicMatches) {

				log.debug("--> checkInsert: OK: ALIEN_PUBLIC");
				return this;
			}

			// check ALIEN_PUBLIC_WITH_ALIEN_VALUE patterns

			boolean alienPublicWithAlienValueMatches = false;
			for (String alienPublicWithAlienValue : ALIEN_PUBLIC_WITH_ALIEN_VALUE) { 

				if (checkKey(alienPublicWithAlienValue) && alienValue) {

					alienPublicWithAlienValueMatches = true; 
					break;
				}
			}
			if (alienPublicWithAlienValueMatches) {

				log.debug("--> checkInsert: OK: ALIEN_PUBLIC_WITH_ALIEN_VALUE");
				return this;
			}

			// check SELF_PUBLIC patterns

			boolean selfPublicMatches = false;
			for (String selfPublic : SELF_PUBLIC) { 

				if (checkKey(selfPublic)) {

					selfPublicMatches = true; 
					break;
				}
			}
			if (selfPublicMatches) {

				log.debug("--> checkInsert: OK: SELF_PUBLIC");
				return this;
			}
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}

		log.debug("--> checkInsert: NOT OK");
		return null;
	}

	public PastContentHandle getHandle(Past past) {

		return new VegaPastContentHandle(past);
	}
/*
	public GCPastContentHandle getHandle(GCPast past, long expiration) {

		return new VegaPastContentHandle(past);
	}

	public GCPastMetadata getMetadata(long expiration) {

		return new GCPastMetadata(expiration);
	}*/

	public Id getId() {

		return this.id;
	}

/*	public long getVersion() {

		return this.version.longValue();
	}*/

	public String getIname() {

		return this.iname;
	}

	public String getInumber() {

		return this.inumber;
	}

	public String getKey() {

		return this.key;
	}

	public String getValue() {

		return this.value;
	}

	public String getSignature() {

		return this.signature;
	}

	public String getHashcash() {

		return this.hashcash;
	}

	public boolean isMutable() {

		return true;
	}

	private boolean checkKey(String pattern) {

		String escapedIname = escapeRegex(this.iname);
		String escapedInumber = escapeRegex(this.inumber);
		String escapedInameHash = escapeRegex(hash(this.iname));
		String escapedInumberHash = escapeRegex(hash(this.inumber));

		Set<String> replacedPatterns = new HashSet<String> ();
		replacedPatterns.add(pattern.replace(":::xri:::", escapedIname).replace(":::hash:::", escapedInameHash));
		replacedPatterns.add(pattern.replace(":::xri:::", escapedInumber).replace(":::hash:::", escapedInameHash));
		replacedPatterns.add(pattern.replace(":::xri:::", escapedIname).replace(":::hash:::", escapedInumberHash));
		replacedPatterns.add(pattern.replace(":::xri:::", escapedInumber).replace(":::hash:::", escapedInumberHash));

		for (String replacedPattern : replacedPatterns) {

			boolean matches = Pattern.matches(replacedPattern, this.key);
			log.debug("--> checkKey: replacedPattern=" + replacedPattern + " matches=" + Boolean.toString(matches));

			if (matches) return true;
		}

		return false;
	}

	private static String hash(String str) {

		String hash;

		try {

			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(str.getBytes());
			hash = new String(Base64.encodeBase64(digest.digest()));
		} catch (NoSuchAlgorithmException ex) {

			throw new RuntimeException("hash(): " + ex.getMessage(), ex);
		}

		return(hash);
	}

	private static String escapeRegex(String string) {

		StringBuilder result = new StringBuilder();
		StringCharacterIterator iterator = new StringCharacterIterator(string);

		char c = iterator.current();
		while (c != CharacterIterator.DONE) {

			if (c == '.') 
				result.append("\\.");
			else if (c == '\\')
				result.append("\\\\");
			else if (c == '?')
				result.append("\\?");
			else if (c == '*')
				result.append("\\*");
			else if (c == '+')
				result.append("\\+");
			else if (c == '&')
				result.append("\\&");
			else if (c == ':')
				result.append("\\:");
			else if (c == '{')
				result.append("\\{");
			else if (c == '}')
				result.append("\\}");
			else if (c == '[')
				result.append("\\[");
			else if (c == ']')
				result.append("\\]");
			else if (c == '(')
				result.append("\\(");
			else if (c == ')')
				result.append("\\)");
			else if (c == '^')
				result.append("\\^");
			else if (c == '$')
				result.append("\\$");
			else
				result.append(c);

			c = iterator.next();
		}
		return result.toString();
	}

	private static String addIndexToIndexList(String newindex, String indexlist) {

		String[] indices = indexlist.split(" ");

		for (String index : indices) {

			if (index.equals(newindex)) return indexlist;
		}

		return indexlist + " " + newindex;
	}

	private static String removeIndexFromIndexList(String removeindex, String indexlist) {

		String[] indices = indexlist.split(" ");

		String newindexlist = "";
		boolean needSpace = false;

		for (String index : indices) {

			if (index.equals(removeindex)) continue;
			if (needSpace) newindexlist += " "; else needSpace = true;
			newindexlist += index;
		}

		return newindexlist;
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof VegaPastContent)) return(false);
		if (object == this) return(true);

		VegaPastContent other = (VegaPastContent) object;

		if (this.id == null && other.id != null) return(false);
		if (this.id != null && ! this.id.equals(other.id)) return(false);

		if (this.iname == null && other.iname != null) return(false);
		if (this.iname != null && ! this.iname.equals(other.iname)) return(false);

		if (this.inumber == null && other.inumber != null) return(false);
		if (this.inumber != null && ! this.inumber.equals(other.inumber)) return(false);

		if (this.key == null && other.key != null) return(false);
		if (this.key != null && ! this.key.equals(other.key)) return(false);

		if (this.value == null && other.value != null) return(false);
		if (this.value != null && ! this.value.equals(other.value)) return(false);

		if (this.signature == null && other.signature != null) return(false);
		if (this.signature != null && ! this.signature.equals(other.signature)) return(false);

		if (this.hashcash == null && other.hashcash != null) return(false);
		if (this.hashcash != null && ! this.hashcash.equals(other.hashcash)) return(false);

		return(true);
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + (this.id == null ? 0 : this.id.hashCode());
		hashCode = (hashCode * 31) + (this.iname == null ? 0 : this.iname.hashCode());
		hashCode = (hashCode * 31) + (this.inumber == null ? 0 : this.inumber.hashCode());
		hashCode = (hashCode * 31) + (this.key == null ? 0 : this.key.hashCode());
		hashCode = (hashCode * 31) + (this.value == null ? 0 : this.value.hashCode());
		hashCode = (hashCode * 31) + (this.signature == null ? 0 : this.signature.hashCode());
		hashCode = (hashCode * 31) + (this.hashcash == null ? 0 : this.hashcash.hashCode());

		return(hashCode);
	}

	@Override
	public String toString() {

		return(this.key + " --> " + this.value);
	}

	private class VegaPastContentHandle extends ContentHashPastContentHandle implements GCPastContentHandle {

		private static final long serialVersionUID = 5964405912214494355L;

		private VegaPastContentHandle(Past past) {

			super(past.getLocalNodeHandle(), VegaPastContent.this.id);
		}

		public long getVersion() {

			return VegaPastContent.this.version.longValue();
		}

		public long getExpiration() {

			return GCPast.INFINITY_EXPIRATION;
		}
	}
}
