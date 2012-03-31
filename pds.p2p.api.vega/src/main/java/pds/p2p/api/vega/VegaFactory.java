package pds.p2p.api.vega;

import pds.p2p.api.Orion;
import pds.p2p.api.Vega;

public class VegaFactory {

	private static Orion orion = null;
	private static Vega vega = null;

	private static Throwable ex;

	private VegaFactory() { }

	public static Vega getVega(Object orion) {

		if (vega != null) return(vega);

		VegaFactory.orion = (Orion) orion;

		try {

			// make Vega

			vega = new VegaImpl((Orion) orion);

			// done

			return(vega);
		} catch (Throwable ex) {

			VegaFactory.ex = ex;
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
