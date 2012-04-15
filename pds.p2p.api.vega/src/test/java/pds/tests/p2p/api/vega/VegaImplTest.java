package pds.tests.p2p.api.vega;

import java.io.File;

import junit.framework.TestCase;
import pds.p2p.api.Orion;
import pds.p2p.api.Vega;
import pds.p2p.api.orion.OrionFactory;
import pds.p2p.api.vega.VegaFactory;

public class VegaImplTest extends TestCase {

	private Orion orion;
	private Vega vega;

	@Override
	public void setUp() throws Exception {

		this.orion = OrionFactory.getOrion();
		this.vega = VegaFactory.getVega(this.orion);

		this.orion.init();
		this.vega.init();
	}

	public void tearDown() throws Exception {

		assertNull(this.vega.nodeId());
		assertNull(this.vega.localHost());
		assertNull(this.vega.localPort());
		assertNull(this.vega.publicHost());
		assertNull(this.vega.publicPort());
		assertNull(this.vega.remoteHost());
		assertNull(this.vega.remotePort());
		assertNull(this.vega.parameters());

		this.vega.shutdown();
		this.orion.shutdown();

		new File("./logs/").delete();
	}

	public void testVegaImpl() throws Exception {

		this.orion.login("=markus", "xxx");
		this.vega.connect(null, null, null, null, null);

		assertNotNull(this.vega.nodeId());
		assertEquals(this.vega.lookupNeighbors("10").length, 1);
		assertNotNull(this.vega.localHost());
		assertNotNull(this.vega.localPort());
		assertNull(this.vega.publicHost());
		assertNull(this.vega.publicPort());
		assertNull(this.vega.remoteHost());
		assertNull(this.vega.remotePort());
		assertNull(this.vega.parameters());
		assertEquals(this.vega.connected(), "1");

		this.vega.put("a", "b");
		this.vega.put("c", "d");

		assertEquals(this.vega.get("a"), "b");
		assertEquals(this.vega.get("c"), "d");

		this.vega.disconnect();
		assertNull(this.vega.nodeId());
		assertEquals(this.vega.lookupNeighbors("10").length, 0);
		assertNull(this.vega.localHost());
		assertNull(this.vega.localPort());
		assertNull(this.vega.publicHost());
		assertNull(this.vega.publicPort());
		assertNull(this.vega.remoteHost());
		assertNull(this.vega.remotePort());
		assertNull(this.vega.parameters());

		this.orion.logout();
	}
}
