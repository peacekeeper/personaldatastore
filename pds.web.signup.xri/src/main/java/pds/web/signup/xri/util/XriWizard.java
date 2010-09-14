package pds.web.signup.xri.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.openxri.xml.CertificateService;
import org.openxri.xml.SEPUri;
import org.openxri.xml.Service;
import org.openxri.xml.XDIService;

import pds.store.xri.Xri;
import pds.web.signup.xri.AbstractXriSignUpMethod;

public class XriWizard {

	private XriWizard() { }

	private static KeyPairGenerator keyPairGenerator;
	private static X509V3CertificateGenerator certificateGenerator;

	static {

		Security.addProvider(new BouncyCastleProvider());

		try {

			keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
			keyPairGenerator.initialize(2048);

			certificateGenerator = new X509V3CertificateGenerator();
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	public static void configure(AbstractXriSignUpMethod xriSignUpMethod, Xri xri) throws Exception {

		xri.deleteAllServices();
		List<Service> services = new ArrayList<Service> ();

		// set up XRDS SEP

		Service xrdsService = new Service();
		xrdsService.addMediaType("application/xrds+xml", null, Boolean.FALSE);
		xrdsService.addURI("http://xri.net/" + xri.getCanonicalID().getValue() + "?_xrd_r=application/xrds+xml");
		services.add(xrdsService);

		// set up XDI SEP(s)

		String[] endpoints = xriSignUpMethod.getEndpoints();
		URI[] uris = new URI[endpoints.length];

		for (int i=0; i<endpoints.length; i++) {

			String endpoint = (String) endpoints[i];
			if (! endpoint.endsWith("/")) endpoint += "/";
			endpoint += xri.getCanonicalID().getValue() + "/";

			uris[i] = URI.create(endpoint);		
		}

		services.add(
				new XDIService(
						uris));

		// set up PDS SEPs

		Service hcardService = new Service();
		hcardService.addType("http://microformats.org/profile/hcard", null, Boolean.TRUE);
		hcardService.addMediaType("text/html", null, Boolean.FALSE);
		hcardService.addURI(xriSignUpMethod.getHcardEndpoint() + xri.getCanonicalID().getValue(), null, SEPUri.APPEND_NONE);
		services.add(hcardService);

		Service pocosonService = new Service();
		pocosonService.addType("http://portablecontacts.net/spec/1.0#me", null, Boolean.TRUE);
		pocosonService.addURI(xriSignUpMethod.getPocoEndpoint() + xri.getCanonicalID().getValue(), null, SEPUri.APPEND_NONE);
		services.add(pocosonService);

		Service feedAtomService = new Service();
		feedAtomService.addType("http://schemas.google.com/g/2010#updates-from", null, Boolean.TRUE);
		feedAtomService.addMediaType("application/atom+xml", null, Boolean.FALSE);
		feedAtomService.addURI(xriSignUpMethod.getFeedEndpoint() + "atom/" + xri.getCanonicalID().getValue(), null, SEPUri.APPEND_NONE);
		services.add(feedAtomService);

		Service salmonService = new Service();
		salmonService.addType("salmon", null, Boolean.TRUE);
		salmonService.addURI(xriSignUpMethod.getSalmonEndpoint() + xri.getCanonicalID().getValue(), null, SEPUri.APPEND_NONE);
		services.add(salmonService);

		Service salmonRepliesService = new Service();
		salmonRepliesService.addType("http://salmon-protocol.org/ns/salmon-replies", null, Boolean.TRUE);
		salmonRepliesService.addURI(xriSignUpMethod.getSalmonEndpoint() + xri.getCanonicalID().getValue(), null, SEPUri.APPEND_NONE);
		services.add(salmonRepliesService);

		Service salmonMentionsService = new Service();
		salmonMentionsService.addType("http://salmon-protocol.org/ns/salmon-mention", null, Boolean.TRUE);
		salmonMentionsService.addURI(xriSignUpMethod.getSalmonEndpoint() + xri.getCanonicalID().getValue(), null, SEPUri.APPEND_NONE);
		services.add(salmonMentionsService);

		Service foafService = new Service();
		foafService.addType("describedby", null, Boolean.FALSE);
		foafService.addMediaType("application/rdf+xml", null, Boolean.FALSE);
		foafService.addURI(xriSignUpMethod.getFoafEndpoint() + xri.getCanonicalID().getValue(), null, SEPUri.APPEND_NONE);
		services.add(foafService);

		// set up keys/certificate and SEP 

		X509Certificate userCertificate;

		if (! xri.hasAuthorityAttribute("publickey") ||
				! xri.hasAuthorityAttribute("privatekey") ||
				! xri.hasAuthorityAttribute("certificate")) {

			X509Certificate brokerCertificate = xriSignUpMethod.getBrokerCertificate();
			PrivateKey brokerPrivateKey = xriSignUpMethod.getBrokerPrivateKey();

			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			PublicKey userPublicKey = keyPair.getPublic();
			PrivateKey userPrivateKey = keyPair.getPrivate();
			Date userCertificateDate = new Date();

			certificateGenerator.setPublicKey(userPublicKey);
			certificateGenerator.setSubjectDN(new X509Name("cn=" + xri.getCanonicalID().getValue()));
			certificateGenerator.setIssuerDN(brokerCertificate.getIssuerX500Principal());
			certificateGenerator.setNotBefore(userCertificateDate);
			certificateGenerator.setNotAfter(new Date(userCertificateDate.getTime() + Long.parseLong(xriSignUpMethod.getUserCertificateValidity())));
			certificateGenerator.setSerialNumber(BigInteger.valueOf(userCertificateDate.getTime()));
			certificateGenerator.setSignatureAlgorithm(xriSignUpMethod.getUserCertificateSignatureAlgorithm());

			userCertificate = certificateGenerator.generate(brokerPrivateKey);

			xri.setAuthorityAttribute("publickey", new String(Base64.encodeBase64(userPublicKey.getEncoded()), "UTF-8"));
			xri.setAuthorityAttribute("privatekey", new String(Base64.encodeBase64(userPrivateKey.getEncoded()), "UTF-8"));
			xri.setAuthorityAttribute("certificate", new String(Base64.encodeBase64(userCertificate.getEncoded()), "UTF-8"));
		} else {

			String userCertificateStr = xri.getAuthorityAttribute("certificate");

			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
			InputStream userCertificateInputStream = new ByteArrayInputStream(Base64.decodeBase64(userCertificateStr.getBytes("UTF-8"))); 
			userCertificate = (X509Certificate) certificateFactory.generateCertificate(userCertificateInputStream);
		}

		services.add(
				new CertificateService(
						userCertificate));

		// add service endpoints

		xri.addServices(services.toArray(new Service[services.size()]));
	}
}
