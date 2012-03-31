package pds.xdi.sync;

import java.util.HashSet;
import java.util.Set;

import xdi2.client.XDIClient;
import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.impl.XRI3;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;

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
	public MessageResult send(MessageEnvelope messageEnvelope, MessageResult messageResult) throws Xdi2MessagingException {

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
