package pds.p2p.api.sirius.graph;

import java.io.IOException;

import pds.p2p.api.Vega;
import xdi2.core.GraphFactory;
import xdi2.core.impl.keyvalue.AbstractKeyValueGraphFactory;
import xdi2.core.impl.keyvalue.KeyValueStore;


public class VegaGraphFactory extends AbstractKeyValueGraphFactory implements GraphFactory {

	private Vega vega;

	public VegaGraphFactory() {

		super(false, false);
	}

	@Override
	protected KeyValueStore openKeyValueStore(String identifier) throws IOException {

		return new VegaKeyValueStore(this.vega);
	}

	public Vega getVega() {

		return this.vega;
	}

	public void setVega(Vega vega) {

		this.vega = vega;
	}
}
