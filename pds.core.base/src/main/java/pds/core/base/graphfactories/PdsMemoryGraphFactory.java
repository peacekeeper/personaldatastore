package pds.core.base.graphfactories;

import javax.servlet.FilterConfig;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.impl.memory.MemoryGraphFactory;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.base.PdsException;
import pds.core.base.PdsGraphFactory;
import pds.core.base.PdsInstance;

public class PdsMemoryGraphFactory implements PdsGraphFactory {

	private int sortmode;
	private MemoryGraphFactory graphFactory;
	
	public void init(FilterConfig filterConfig) {

		if (this.sortmode < 0) {
			
			this.sortmode = MemoryGraphFactory.SORTMODE_NONE;
		}
		
		this.graphFactory = new MemoryGraphFactory();
	}

	@Override
	public Graph getPdsInstanceGraph(PdsInstance pdsInstance) throws PdsException {

		XRI3Segment canonical = pdsInstance.getCanonical();
		if (canonical == null) throw new PdsException("PDS Instance has no canonical identifier.");

		this.graphFactory.setSortmode(this.sortmode);

		return this.graphFactory.openGraph();
	}

	public int getSortmode() {

		return this.sortmode;
	}

	public void setSortmode(int sortmode) {
		
		this.sortmode = sortmode;
	}
}
