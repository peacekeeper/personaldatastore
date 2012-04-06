package pds.tests.p2p.api.sirius;


import java.io.File;

import junit.framework.TestCase;
import pds.p2p.api.Orion;
import pds.p2p.api.Sirius;
import pds.p2p.api.Vega;
import pds.p2p.api.orion.OrionFactory;
import pds.p2p.api.sirius.SiriusFactory;
import pds.p2p.api.vega.VegaFactory;
import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIJSONReader;
import xdi2.core.io.XDIReader;
import xdi2.core.xri3.impl.XRI3Segment;

public class SiriusImplTest extends TestCase {

	private Orion orion;
	private Vega vega;
	private Sirius sirius;

	private static void clearDirectory(String directoryString) {

		File directory = new File(directoryString);
		if (! directory.exists()) return;

		for (String filename : directory.list()) {

			File file = new File(directory, filename);

			if (file.isDirectory()) 
				clearDirectory(file.getAbsolutePath());
			else
				new File(directory, filename).delete();
		}

		directory.delete();
	}

	@Override
	public void setUp() throws Exception {

		clearDirectory("./storage/"); new File("./storage/").mkdir();
		clearDirectory("./logs/"); new File("./logs/").mkdir();

		this.orion = OrionFactory.getOrion();
		this.vega = VegaFactory.getVega(this.orion);
		this.sirius = SiriusFactory.getSirius(this.vega);

		this.orion.init();
		this.vega.init();
		this.sirius.init();
	}

	public void tearDown() throws Exception {

		this.vega.shutdown();
		this.orion.shutdown();

		new File("./logs/").delete();
	}

	public void testSiriusImpl() throws Exception {

		this.orion.login("=markus", "xxx");
		this.vega.connect(null, null, null, null);

		String result;
		XDIReader reader = new XDIJSONReader();
		Graph graph;

		this.sirius.add("=markus/+friend/=giovanni", null);
		result = this.sirius.get("=markus", null);
		graph = MemoryGraphFactory.getInstance().openGraph();
		reader.read(graph, result, null);
		assertEquals(new XRI3Segment("=giovanni"), graph.findRelation(new XRI3Segment("=markus"), new XRI3Segment("+friend")).getRelationXri());

		this.vega.disconnect();
		this.orion.logout();
	}
}
