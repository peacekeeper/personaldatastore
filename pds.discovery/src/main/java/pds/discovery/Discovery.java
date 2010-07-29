package pds.discovery;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxri.XRI;
import org.openxri.resolve.Resolver;
import org.openxri.resolve.ResolverFlags;
import org.openxri.resolve.ResolverState;
import org.openxri.xml.Service;
import org.openxri.xml.XDIService;
import org.openxri.xml.XRD;

public class Discovery {

	private static final Log log = LogFactory.getLog(Discovery.class.getName());

	private Resolver resolver;

	public Discovery() {

		try {

			this.resolver = new Resolver(null);
		} catch (Exception ex) {
			
			throw new RuntimeException(ex);
		}
	}

	public Discovery(Resolver resolver) {

		this.resolver = resolver;
	}

	public String resolveXriToInumber(String iname) throws Exception {

		String inumber = null;

		ResolverFlags resolverFlags = new ResolverFlags();

		XRD xrd = this.resolver.resolveAuthToXRD(new XRI(iname), resolverFlags, new ResolverState());
		if (xrd.getCanonicalID() == null) return null;
		inumber = xrd.getCanonicalID().getValue();

		log.info("Resolved " + iname + " to " + inumber);
		return inumber;
	}

	public String resolveInumberToUri(String inumber) throws Exception {

		String uri = null;

		ResolverFlags resolverFlags = new ResolverFlags();

		XRD xrd = this.resolver.resolveSEPToXRD(new XRI(inumber ), XDIService.SERVICE_TYPE, null, resolverFlags, new ResolverState());
		if (! xrd.getStatus().getCode().equals("100")) throw new RuntimeException("Resultion failed: " + xrd.getStatus().getCode());

		List<?> services = xrd.getSelectedServices().getList();

		for (Object service : services) {

			if (((Service) service).getNumURIs() > 0) uri = ((Service) service).getURIAt(0).getUriString();
		}
		if (uri != null && (! uri.endsWith("/"))) uri += "/";

		log.info("Resolved " + inumber + " to " + uri);
		return uri;
	}

	public Resolver getResolver() {

		return this.resolver;
	}

	public void setResolver(Resolver resolver) {

		this.resolver = resolver;
	}
}
