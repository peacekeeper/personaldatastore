package pds.core.graphfactories;

import javax.servlet.FilterConfig;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.impl.memory.MemoryGraphFactory;

import pds.core.PdsConnection;
import pds.core.PdsException;
import pds.core.PdsGraphFactory;

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
	public Graph getPdsConnectionGraph(PdsConnection pdsConnection) throws PdsException {

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
