package pds.web.tools.util;


import java.security.PublicKey;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Decrypts a set of SAML assertions and returns an unencrypted @{link Document} object.
 * @author hummel
 */
public class CryptoUtils {

	private static final Logger log = LoggerFactory.getLogger(CryptoUtils.class);

	/**
	 * Initializes the @{link org.apache.xml.security.Init Apache XML Security} infrastructure.
	 */
	static {

		org.apache.xml.security.Init.init();
	}

	public static boolean verifyXmlSignature(Document doc) {

		Element signatureElement = (Element) doc.getElementsByTagNameNS(Constants.SignatureSpecNS, "Signature").item(0);
		XMLSignature signature;

		try {

			signature = new XMLSignature(signatureElement, "");
		} catch (XMLSecurityException ex) {

			log.warn("error verifying digital signature: " + ex.getMessage(), ex);
			return false;
		}

		KeyInfo keyInfo = signature.getKeyInfo();
		log.debug("keyInfo is: " + keyInfo);

		PublicKey publicKey;

		try {

			publicKey = keyInfo.getPublicKey();
			log.debug("Public key is: " + publicKey);
		} catch (KeyResolverException ex) {

			log.warn("Signature did not contain public key data: " + ex.getMessage(), ex);
			return false;
		}

		try {

			boolean valid = signature.checkSignatureValue(publicKey);
			if (valid) {

				log.debug("Signature was valid");
			} else {

				log.warn("Signature was invalid");
			}
			return valid;
		} catch (XMLSignatureException ex) {

			log.warn("Problem while checking signature validity: " + ex.getMessage(), ex);
			return false;
		}
	}

	public static PublicKey extractSignaturePublicKey(Document doc) {

		Element signatureElement = (Element) doc.getElementsByTagNameNS(Constants.SignatureSpecNS, "Signature").item(0);
		XMLSignature signature;

		try {

			signature = new XMLSignature(signatureElement, "");
		} catch (XMLSecurityException ex) {

			log.warn("Problem finding digital signature: " + ex.getMessage(), ex);
			return null;
		}

		KeyInfo keyInfo = signature.getKeyInfo();
		log.debug("keyInfo is: " + keyInfo);

		PublicKey publicKey;

		try {

			publicKey = keyInfo.getPublicKey();
			log.debug("Public key is: " + publicKey);
		} catch (KeyResolverException ex) {

			log.warn("Signature did not contain public key data: " + ex.getMessage(), ex);
			return null;
		}

		return publicKey;
	}
}
