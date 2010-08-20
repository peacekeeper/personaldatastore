package pds.core.graphfactories;

import java.io.IOException;

import javax.servlet.FilterConfig;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.impl.xml.XMLGraphFactory;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.PdsConnection;
import pds.core.PdsException;
import pds.core.PdsGraphFactory;

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
	public Graph getPdsConnectionGraph(PdsConnection pdsConnection) throws PdsException {

		String identifier = pdsConnection.getIdentifier();
		XRI3Segment canonical = pdsConnection.getCanonical();

		String filename = this.filepath;
		if (! filename.endsWith("/")) filename += "/";
		filename += (canonical != null) ? canonical.toString() : identifier;

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
