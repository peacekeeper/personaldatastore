package pds.core.base.graphfactories;

import java.io.IOException;

import javax.servlet.FilterConfig;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.impl.xml.XMLGraphFactory;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.base.PdsException;
import pds.core.base.PdsGraphFactory;
import pds.core.base.PdsInstance;

public class PdsXMLGraphFactory implements PdsGraphFactory {

	private String filepath;
	private XMLGraphFactory graphFactory;

	public void init(FilterConfig filterConfig) {

		if (this.filepath == null) {

			this.filepath = ".";
		}

		this.graphFactory = new XMLGraphFactory();
	}

	@Override
	public Graph getPdsInstanceGraph(PdsInstance pdsInstance) throws PdsException {

		XRI3Segment canonical = pdsInstance.getCanonical();
		if (canonical == null) throw new PdsException("PDS Instance has no canonical identifier.");

		String filename = this.filepath;
		if (! filename.endsWith("/")) filename += "/";
		filename += canonical.toString();

		this.graphFactory.setFilename(filename);

		Graph graph;

		try {

			graph = this.graphFactory.openGraph();
		} catch (IOException ex) {

			throw new PdsException("Cannot open graph: " + ex.getMessage(), ex);
		}

		return graph;
	}

	public String getFilepath() {

		return this.filepath;
	}

	public void setFilepath(String filepath) {

		this.filepath = filepath;
	}
}
