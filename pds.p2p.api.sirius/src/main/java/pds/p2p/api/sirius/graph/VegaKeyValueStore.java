package pds.p2p.api.sirius.graph;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import pds.p2p.api.Vega;
import xdi2.core.impl.keyvalue.AbstractKeyValueStore;


class VegaKeyValueStore extends AbstractKeyValueStore {

	private Vega vega;

	VegaKeyValueStore(Vega vega) {

		this.vega = vega;
	}

	@Override
	public void init() throws IOException {

	}

	public void put(String key, String value) {

		try {

			this.vega.multiPut(key, value);
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	public Iterator<String> getAll(String key) {

		try {

			Iterator<String> ret = Arrays.asList(this.vega.multiGet(key)).iterator();

			return(ret);
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	@Override
	public void delete(String key) {

		try {

			this.vega.multiDelete(key, null);
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	public void delete(String key, String value) {

		try {

			this.vega.multiDelete(key, value);
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	@Override
	public int count(String key) {

		try {

			int ret = Integer.valueOf(this.vega.multiGetCount(key)).intValue();

			return(ret);
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	public void clear() {

		throw new RuntimeException("Deleting all keys not supported.");
	}

	public void close() {

		this.vega = null;
	}

	public Vega getVega() {

		return(this.vega);
	}
}
