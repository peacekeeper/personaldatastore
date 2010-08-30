package pds.core.base.graphfactories;

import java.io.IOException;

import javax.servlet.FilterConfig;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.impl.keyvalue.bdb.BDBGraphFactory;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.base.PdsException;
import pds.core.base.PdsGraphFactory;
import pds.core.base.PdsInstance;

public class PdsBDBGraphFactory implements PdsGraphFactory {

	private String databasePath;
	private BDBGraphFactory graphFactory;

	public void init(FilterConfig filterConfig) {

		if (this.databasePath == null) {

			this.databasePath = "./pds.core-" + filterConfig.getServletContext().getServletContextName() + "/";
		}

		this.graphFactory = new BDBGraphFactory();
	}

	@Override
	public Graph getPdsInstanceGraph(PdsInstance pdsInstance) throws PdsException {

		XRI3Segment canonical = pdsInstance.getCanonical();
		if (canonical == null) throw new PdsException("PDS Instance has no canonical identifier.");

		String databaseName = canonical.toString();

		this.graphFactory.setDatabasePath(this.databasePath);
		this.graphFactory.setDatabaseName(databaseName);

		Graph graph;

		try {

			graph = this.graphFactory.openGraph();
		} catch (IOException ex) {

			throw new PdsException("Cannot open graph: " + ex.getMessage(), ex);
		}
		
		return graph;
	}

	public String getDatabasePath() {

		return this.databasePath;
	}

	public void setDatabasePath(String databasePath) {

		this.databasePath = databasePath;
	}
}
