package pds.p2p.api.orion.util;

import java.security.cert.Certificate;
import java.util.List;

import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.openxri.XRI;
import org.openxri.resolve.Resolver;
import org.openxri.resolve.ResolverFlags;
import org.openxri.resolve.ResolverState;
import org.openxri.resolve.exception.PartialResolutionException;
import org.openxri.xml.CanonicalID;
import org.openxri.xml.Service;
import org.openxri.xml.Status;
import org.openxri.xml.XRD;

public class OpenxriXriUtil {

	private static Resolver resolver;

	static {

		try {

			resolver = new Resolver(null);
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	private OpenxriXriUtil() { }

	public static String discoverCanonicalId(String xri) throws PartialResolutionException {

		ResolverFlags resolverFlags = new ResolverFlags();
		ResolverState resolverState = new ResolverState();

		XRD xrd = resolver.resolveAuthToXRD(new XRI(xri), resolverFlags, resolverState);
		if (! xrd.getStatus().getCode().equals(Status.SUCCESS)) throw new RuntimeException(xrd.getStatus().getCode() + " " + xrd.getStatus().getText());

		CanonicalID canonicalID = xrd.getCanonicalID();
		return canonicalID == null ? null : canonicalID.getValue();
	}

	public static Certificate discoverCertificate(String xri) throws PartialResolutionException, KeyResolverException {

		ResolverFlags resolverFlags = new ResolverFlags();
		ResolverState resolverState = new ResolverState();

		resolverFlags.setNoDefaultT(true);

		XRD xrd = resolver.resolveSEPToXRD(new XRI(xri), "xri://$certificate*($x.509)", null, resolverFlags, resolverState);
		if (! xrd.getStatus().getCode().equals(Status.SUCCESS)) throw new RuntimeException(xrd.getStatus().getCode() + " " + xrd.getStatus().getText());

		if (xrd.getNumServices() < 1) return null;
		Service service = xrd.getServiceAt(0);
		KeyInfo keyInfo = service.getKeyInfo();
		if (keyInfo == null) return null;
		return keyInfo.getX509Certificate();
	}

	@SuppressWarnings("unchecked")
	public static String discoverXdiUri(String xri) throws PartialResolutionException {

		ResolverFlags resolverFlags = new ResolverFlags();
		ResolverState resolverState = new ResolverState();

		resolverFlags.setNoDefaultT(true);

		List<String> uris = (List<String>) resolver.resolveSEPToURIList(new XRI(xri), "xri://$xdi!($v!1)", null, resolverFlags, resolverState);

		if (uris == null || uris.size() < 1) return null;
		return(uris.get(0));
	}
}
