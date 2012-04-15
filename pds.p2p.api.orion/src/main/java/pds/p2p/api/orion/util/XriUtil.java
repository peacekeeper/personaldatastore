package pds.p2p.api.orion.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.x509.X509V1CertificateGenerator;

public class XriUtil {

	private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
	private static final String ENCRYPTION_ALGORITHM = "RSA";

	private static Log log = LogFactory.getLog(XriUtil.class);

	private static CacheManager cacheManager;
	private static Cache canonicalIdCache;
	private static Cache certificateCache;
	private static Cache xdiUriCache;

	private static KeyPairGenerator keyPairGenerator;
	private static X509V1CertificateGenerator certificateGenerator;

	private static KeyPair fakeKeyPair;
	
	static {

		URL configurationFileURL = XriUtil.class.getResource("vv-ehcache.xml");
		if (configurationFileURL == null) throw new ExceptionInInitializerError("vv-ehcache.xml");
		cacheManager = CacheManager.create(configurationFileURL);
		canonicalIdCache = cacheManager.getCache("canonicalIdCache");
		certificateCache = cacheManager.getCache("certificateCache");
		xdiUriCache = cacheManager.getCache("xdiUriCache");

		try {

			keyPairGenerator = KeyPairGenerator.getInstance(ENCRYPTION_ALGORITHM, "BC");
			certificateGenerator = new X509V1CertificateGenerator();
		} catch (NoSuchAlgorithmException ex) {

			throw new RuntimeException(ex);
		} catch (NoSuchProviderException ex) {

			throw new RuntimeException(ex);
		}

		SecureRandom random = new SecureRandom(new byte[] { 0 });
		keyPairGenerator.initialize(1024, random);
		fakeKeyPair = keyPairGenerator.generateKeyPair();
	}

	private XriUtil() { }

	public static String discoverCanonicalId(String xri) throws UnsupportedEncodingException {

		// get it from cache?

		Element element = canonicalIdCache.get(xri);
		log.debug("discoverCanonicalId(" + xri + "): CACHE " + (element != null ? "HIT" : "MISS"));
		if (element != null) return (String) element.getValue();

		// resolve it!

		MessageDigest messageDigest;
		StringBuffer canonicalId = new StringBuffer(xri.charAt(0) + "!");

		try {

			messageDigest = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException ex) {

			throw new RuntimeException(ex);
		}

		messageDigest.update(xri.getBytes("UTF-8"));

		byte[] hash = messageDigest.digest();
		canonicalId.append(String.format("%02x%02x", hash[0], hash[1]));
		canonicalId.append(".");
		canonicalId.append(String.format("%02x%02x", hash[2], hash[3]));
		canonicalId.append(".");
		canonicalId.append(String.format("%02x%02x", hash[4], hash[5]));
		canonicalId.append(".");
		canonicalId.append(String.format("%02x%02x", hash[6], hash[7]));

		// put it into cache

		if (canonicalId != null) canonicalIdCache.put(new Element(xri, canonicalId.toString()));

		// done

		return canonicalId.toString();
	}

	public static Certificate discoverCertificate(String inumber) throws UnsupportedEncodingException, CertificateEncodingException, InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, SignatureException {

		// get it from cache?

		Element element = certificateCache.get(inumber);
		log.debug("discoverCertificate(" + inumber + "): CACHE " + (element != null ? "HIT" : "MISS"));
		if (element != null) return (Certificate) element.getValue();

		// resolve it!

		certificateGenerator.setSerialNumber(BigInteger.ONE);
		certificateGenerator.setNotBefore(new Date());
		certificateGenerator.setNotAfter(new Date());
		certificateGenerator.setIssuerDN(new X500Principal("CN=" + inumber));
		certificateGenerator.setSubjectDN(new X500Principal("CN=" + inumber));
		certificateGenerator.setPublicKey(fakeKeyPair.getPublic());
		certificateGenerator.setSignatureAlgorithm(SIGNATURE_ALGORITHM);

		X509Certificate certificate = certificateGenerator.generate(fakeKeyPair.getPrivate());

		// put it into cache

		if (certificate != null) certificateCache.put(new Element(inumber, certificate));

		// done

		return certificate;
	}

	public static String discoverXdiUri(String xri) throws IOException {

		// get it from cache?

		Element element = xdiUriCache.get(xri);
		log.debug("discoverXdiUri(" + xri + "): CACHE " + (element != null ? "HIT" : "MISS"));
		if (element != null) return (String) element.getValue();

		// resolve it!

		InetAddress localAddr = InetAddress.getLocalHost();
		String xdiUri = "http://" + localAddr.getHostName() + ":10100/";

		// put it into cache

		if (xdiUri != null) xdiUriCache.put(new Element(xri, xdiUri));

		// done

		return xdiUri;
	}

	public static KeyPair retrieveKeyPair(String inumber, String password) {

		return fakeKeyPair;
	}
}
