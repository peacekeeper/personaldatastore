package pds.p2p.api.orion;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import pds.p2p.api.Orion;
import pds.p2p.api.orion.util.XriUtil;

public class OrionImpl implements Orion {

	private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
	private static final String ENCRYPTION_ALGORITHM = "RSA";
	private static final String SYMENCRYPTION_ALGORITHM = "AES";
	private static final int SYMENCRYPTION_KEYSIZE = 128;

	private static Log log = LogFactory.getLog(OrionImpl.class);

//	private static final SecureRandom random = new SecureRandom();
	private static final SecureRandom fakeRandom = new SecureRandom(new byte[] { 0 });

	private String iname;
	private String password;
	private String inumber;
	/*private String xdiUri;*/
	private PrivateKey privateKey;
	private PublicKey publicKey;

	static {

		Security.addProvider(new BouncyCastleProvider());
	}

	OrionImpl() {

		this.iname = null;
		this.password = null;
		this.inumber = null;
		/*this.xdiUri = null;*/
		this.privateKey = null;
		this.publicKey = null;
	}

	public void init() throws Exception {

		log.info("init()");
	}

	public void shutdown() {

		log.info("shutdown()");
	}

	/*
	 * Actions
	 */

	public void login(String iname, String password) throws Exception {

		log.debug("login(" + iname + "," + password.replaceAll(".", "*") + ")");

		try {

			this.iname = iname;
			this.password = password;

			this.inumber = XriUtil.discoverCanonicalId(this.iname);
			if (this.inumber == null) throw new RuntimeException("No I-Number found for this I-Name.");
			log.debug("login: inumber=" + this.inumber);

/*			this.xdiUri = XriUtil.discoverXdiUri(this.iname);
			if (this.xdiUri == null) throw new RuntimeException("No XDI endpoint found for this I-Name.");
			if (! this.xdiUri.endsWith("/")) this.xdiUri += "/";
			log.debug("login: xdiUri=" + this.xdiUri);*/

			// retrieve key pair

			KeyPair keyPair = XriUtil.retrieveKeyPair(this.inumber, this.password);

			this.privateKey = keyPair.getPrivate();
			this.publicKey = keyPair.getPublic();
		} catch (Exception ex) {

			this.logout();

			throw ex;
		}
	}

	public void logout() throws Exception {

		log.debug("logout()");

		this.iname = null;
		this.password = null;
		this.inumber = null;
		/*this.xdiUri = null;*/
		this.privateKey = null;
		this.publicKey = null;
	}

	public String loggedin() throws Exception {

		log.debug("loggedin()");

		if (this.iname != null && this.inumber != null && this.privateKey != null && this.publicKey != null) {

			return("1");
		} else {

			return(null);
		}
	}

	public String iname() throws Exception {

		log.debug("iname() = " + this.iname);

		return(this.iname);
	}

	public String inumber() throws Exception {

		log.debug("inumber() = " + this.inumber);

		return(this.inumber);
	}

/*	public String xdiUri() throws Exception {

		log.debug("xdiUri() = " + this.xdiUri);

		return(this.xdiUri);
	}*/

	public String resolve(String iname) throws Exception {

		log.debug("resolve(" + iname + ")");

		return XriUtil.discoverCanonicalId(iname);
	}

	public String sign(String str) throws Exception {

		log.debug("sign(" + str + ")");

		java.security.Signature s = java.security.Signature.getInstance(SIGNATURE_ALGORITHM);
		s.initSign(this.privateKey, fakeRandom);
		s.update(str.getBytes("UTF-8"));
		return(new String(Base64.encodeBase64(s.sign())));
	}

	public String verify(String str, String signature, String inumber) throws Exception {

		log.debug("verify(" + str + "," + signature + "," + inumber + ")");

		// fake!!
		if (str != null) return "1";
		
		Certificate certificate = XriUtil.discoverCertificate(inumber);
		PublicKey publicKey = certificate.getPublicKey();
		java.security.Signature s = java.security.Signature.getInstance(SIGNATURE_ALGORITHM);
		s.initVerify(publicKey);
		s.update(str.getBytes("UTF-8"));

		if (s.verify(Base64.decodeBase64(signature))) {

			return("1");
		} else {

			return(null);
		}
	}

	public String encrypt(String str, String inumber) throws Exception {

		log.debug("encrypt(" + str + "," + inumber + ")");

		Certificate certificate = XriUtil.discoverCertificate(inumber);
		PublicKey publicKey = certificate.getPublicKey();
		Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey, fakeRandom);
		cipher.update(str.getBytes("UTF-8"));
		return(new String(Base64.encodeBase64(cipher.doFinal())));
	}

	public String decrypt(String str) throws Exception {

		log.debug("decrypt(" + str + ")");

		Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, this.privateKey, fakeRandom);
		cipher.update(Base64.decodeBase64(str));
		return(new String(cipher.doFinal(), "UTF-8"));
	}

	public String symGenerateKey() throws Exception {

		log.debug("symGenerateKey()");

		KeyGenerator keyGenerator = KeyGenerator.getInstance(SYMENCRYPTION_ALGORITHM);
		keyGenerator.init(SYMENCRYPTION_KEYSIZE, fakeRandom);
		SecretKey secretKey = keyGenerator.generateKey();
		return(new String(Base64.encodeBase64(secretKey.getEncoded())));
	}

	public String symEncrypt(String str, String key) throws Exception {

		log.debug("symEncrypt(" + str + "," + key + ")");

		SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.decodeBase64(key), SYMENCRYPTION_ALGORITHM);
		Cipher cipher = Cipher.getInstance(SYMENCRYPTION_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, fakeRandom);
		cipher.update(str.getBytes("UTF-8"));
		return(new String(Base64.encodeBase64(cipher.doFinal())));
	}

	public String symDecrypt(String str, String key) throws Exception {

		log.debug("symDecrypt(" + str + "," + key + ")");

		SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.decodeBase64(key), SYMENCRYPTION_ALGORITHM);
		Cipher cipher = Cipher.getInstance(SYMENCRYPTION_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, fakeRandom);
		cipher.update(Base64.decodeBase64(str));
		return(new String(cipher.doFinal(), "UTF-8"));
	}

	public String guid() throws Exception {

		log.debug("guid()");

		return(UUID.randomUUID().toString());
	}
}
