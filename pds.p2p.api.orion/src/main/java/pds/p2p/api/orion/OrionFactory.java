package pds.p2p.api.orion;

import pds.p2p.api.Orion;


public class OrionFactory {

	private static Orion orion = null;

	private static Throwable ex;

	private OrionFactory() { }

	public static Orion getOrion() {

		if (orion != null) return(orion);

		try {

			// make Orion

			orion = new OrionImpl();

			// done

			return(orion);
		} catch (Throwable ex) {

			OrionFactory.ex = ex;
			return(null);
		}
	}

	public static Throwable getException() {

		return(ex);
	}
}
