package pds.web.xdi.objects.sync;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.impl.memory.MemoryGraphFactory;
import org.eclipse.higgins.xdi4j.messaging.MessageEnvelope;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.client.XDIClient;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;

public class XDICachingClient implements XDIClient {

	private XDIClient xdiClient;

	private final Graph cachingGraph;
	private final Set<XRI3> cachedAddresses;
	private final Set<XRI3> dirtyAddresses;

	public XDICachingClient() {

		super();

		this.cachingGraph = MemoryGraphFactory.getInstance().openGraph();
		this.cachedAddresses = new HashSet<XRI3> ();
		this.dirtyAddresses = new HashSet<XRI3> ();
	}

	@Override
	public void close() {

		this.xdiClient.close();
	}

	@Override
	public MessageResult send(MessageEnvelope messageEnvelope, MessageResult messageResult) throws MessagingException {

		return this.xdiClient.send(messageEnvelope, messageResult);
	}

	public XDIClient getXdiClient() {

		return this.xdiClient;
	}

	public void setXdiClient(XDIClient xdiClient) {

		this.xdiClient = xdiClient;
	}

	public Graph getCachingGraph() {
		
		return this.cachingGraph;
	}

	public Set<XRI3> getCachedAddresses() {
		
		return this.cachedAddresses;
	}

	public Set<XRI3> getDirtyAddresses() {
		
		return this.dirtyAddresses;
	}
}
