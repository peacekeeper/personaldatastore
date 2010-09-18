package pds.discovery.xrd;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.parse.BasicParserPool;
import org.openxrd.DefaultBootstrap;
import org.openxrd.discovery.DiscoveryManager;
import org.openxrd.discovery.impl.BasicDiscoveryManager;
import org.openxrd.discovery.impl.HostMetaDiscoveryMethod;
import org.openxrd.discovery.impl.HtmlLinkDiscoveryMethod;
import org.openxrd.discovery.impl.HttpHeaderDiscoveryMethod;
import org.openxrd.xrd.core.Link;
import org.openxrd.xrd.core.XRD;

import com.cliqset.salmon.KeyFinder;
import com.cliqset.salmon.MagicKey;
import com.cliqset.salmon.SalmonException;

public class LRDDOpenXRDKeyFinder implements KeyFinder {

	private static final String REL_MAGIC_KEY = "magic-public-key";
	private static final String SCHEME_DATA = "data";
	private static final String SCHEME_HTTP = "http";
	private static final String SCHEME_HTTPS = "https";
	
	private static DiscoveryManager discoveryManager;
	private static final HttpClient httpClient = new DefaultHttpClient();
	
	static {
		try {
			DefaultBootstrap.bootstrap();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		
		discoveryManager = new BasicDiscoveryManager();
		
		LRDDDiscoveryMethod hostMeta = new LRDDDiscoveryMethod(new HostMetaDiscoveryMethod());
	    hostMeta.setHttpClient(httpClient);
	    hostMeta.setParserPool(new BasicParserPool());
		discoveryManager.getDiscoveryMethods().add(hostMeta);
		
		LRDDDiscoveryMethod header = new LRDDDiscoveryMethod(new HttpHeaderDiscoveryMethod());
		header.setHttpClient(httpClient);
	    header.setParserPool(new BasicParserPool());
		
		discoveryManager.getDiscoveryMethods().add(header);
		
		LRDDDiscoveryMethod link = new LRDDDiscoveryMethod(new HtmlLinkDiscoveryMethod());
		link.setHttpClient(httpClient);
	    link.setParserPool(new BasicParserPool());
		
	    discoveryManager.getDiscoveryMethods().add(link);
	}

	public List<MagicKey> findKeys(URI signerUri) throws SalmonException {
		XRD xrd = discoveryManager.discover(signerUri);

		List<MagicKey> magicKeys = new LinkedList<MagicKey>();

		for (Link link : xrd.getLinks()) {
			if (REL_MAGIC_KEY.equals(link.getRel())) {
				magicKeys.add(fetchKey(URI.create(link.getHref())));
			}
		}
		
		return magicKeys;
	}
	
	private MagicKey fetchKey(URI uri) throws SalmonException {
		if (SCHEME_DATA.equals(uri.getScheme())) {
			String data = uri.getSchemeSpecificPart();
			
			if (data.contains(",")) {
				String[] split = data.split(",");
				byte[] dataBytes = null;
				try {
					dataBytes = split[1].getBytes("ASCII");
				} catch (UnsupportedEncodingException e) {}
				return new MagicKey(dataBytes);
				//work around for status.net 
			} else {
				String[] split = data.split(";");
				byte[] dataBytes = null;
				try {
					dataBytes = split[1].getBytes("ASCII");
				} catch (UnsupportedEncodingException e) {}
				return new MagicKey(dataBytes);
			}
		} else if (SCHEME_HTTP.equals(uri.getScheme()) || SCHEME_HTTPS.equals(uri.getScheme())) {
			HttpGet get = new HttpGet(uri);
			try {
				HttpResponse response = httpClient.execute(get);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				response.getEntity().writeTo(baos);
				return new MagicKey(baos.toByteArray());
			} catch (ClientProtocolException cpe) {
				throw new SalmonException(cpe);
			} catch (IOException ioe) {
				throw new SalmonException(ioe);
			}
		} else {
			throw new SalmonException("URI Scheme " + uri.getScheme() + " is not supported when resolving magic key.");
		}
	}
}
