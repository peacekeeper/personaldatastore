package pds.discovery;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.openxri.resolve.Resolver;

public class Discovery {

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

	public String resolveXriToInumber(String xri) throws Exception {

		return org.eclipse.higgins.xdi4j.discovery.Discovery.discoverInumber(new XRI3Segment(xri), this.resolver);
	}

	public String resolveXriToEndpoint(String xri) throws Exception {

		return org.eclipse.higgins.xdi4j.discovery.Discovery.discoverEndpoint(new XRI3Segment(xri), this.resolver);
	}

	public Resolver getResolver() {

		return this.resolver;
	}

	public void setResolver(Resolver resolver) {

		this.resolver = resolver;
	}
}
