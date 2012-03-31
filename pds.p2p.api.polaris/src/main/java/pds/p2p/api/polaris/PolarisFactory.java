package pds.p2p.api.polaris;

import pds.p2p.api.Orion;
import pds.p2p.api.Polaris;
import xdi2.client.http.XDIHttpClient;

public class PolarisFactory {

	private static Orion orion = null;
	private static Polaris polaris = null;

	private static Throwable ex;

	private PolarisFactory() { }

	public static Polaris getPolaris(Object orion) {

		if (polaris != null) return(polaris);

		PolarisFactory.orion = (Orion) orion;

		try {

			// create the XDI client

			XDIHttpClient client = new XDIHttpClient();

			// make Polaris

			polaris = new PolarisImpl((Orion) orion, client);

			// done

			return(polaris);
		} catch (Throwable ex) {

			PolarisFactory.ex = ex;
			return(null);
		}
	}

	public static Throwable getException() {

		return(ex);
	}

	public static Orion getOrion() {

		return(orion);
	}
}
