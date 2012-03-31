package pds.tests.p2p.api.vega;

import junit.framework.TestCase;
import pds.p2p.api.Orion;
import pds.p2p.api.Vega;
import pds.p2p.api.orion.OrionFactory;
import pds.p2p.api.vega.VegaFactory;

public class VegaImplTest extends TestCase {

	public void testVegaImpl() throws Exception {

		Orion orion = OrionFactory.getOrion();
		Vega vega = VegaFactory.getVega(orion);

		orion.init();
		vega.init();

		orion.login("=markus", "xxx");
		vega.connect(null, null, null, null);

		assertNotNull(vega.nodeId());
		assertNotNull(vega.localHost());
		assertNotNull(vega.localPort());
		assertNull(vega.publicHost());
		assertNull(vega.publicPort());
		assertNull(vega.remoteHost());
		assertNull(vega.remotePort());
		assertNull(vega.parameters());
		assertEquals(vega.connected(), "1");

		vega.put("a", "b");
		vega.put("c", "d");

		assertEquals(vega.get("a"), "b");
		assertEquals(vega.get("c"), "d");
/*
		vega.disconnect();
		assertNull(vega.nodeId());
		assertNull(vega.localHost());
		assertNull(vega.localPort());
		assertNull(vega.publicHost());
		assertNull(vega.publicPort());
		assertNull(vega.remoteHost());
		assertNull(vega.remotePort());
		assertNull(vega.parameters());

		vega.shutdown();
		orion.shutdown();*/
	}
}
