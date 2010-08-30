package pds.core.base.graphfactories;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.FilterConfig;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.impl.hibernate.HibernateGraphFactory;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.base.PdsException;
import pds.core.base.PdsGraphFactory;
import pds.core.base.PdsInstance;

public class PdsHibernateGraphFactory implements PdsGraphFactory {

	private Properties properties;;
	private HibernateGraphFactory graphFactory;

	public void init(FilterConfig filterConfig) throws PdsException {

		if (this.properties == null) {

			throw new PdsException("Please configure properties for the PdsHibernateGraphFactory!");
		}

		this.graphFactory = new HibernateGraphFactory();
	}

	@Override
	public Graph getPdsInstanceGraph(PdsInstance pdsInstance) throws PdsException {

		XRI3Segment canonical = pdsInstance.getCanonical();
		if (canonical == null) throw new PdsException("PDS Instance has no canonical identifier.");

		String graphName = canonical.toString();

		this.graphFactory.setProperties(this.properties);
		this.graphFactory.setGraphName(graphName);

		Graph graph;

		try {

			graph = this.graphFactory.openGraph();
		} catch (IOException ex) {

			throw new PdsException("Cannot open graph: " + ex.getMessage(), ex);
		}

		return graph;
	}

	public Properties getProperties() {

		return this.properties;
	}

	public void setProperties(Properties properties) {

		this.properties = properties;
	}
}
