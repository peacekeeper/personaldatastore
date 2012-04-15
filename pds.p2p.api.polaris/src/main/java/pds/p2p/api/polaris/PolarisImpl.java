package pds.p2p.api.polaris;

import java.io.StringReader;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pds.p2p.api.Orion;
import pds.p2p.api.Polaris;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.util.XDIMessagingConstants;

public class PolarisImpl implements Polaris {

	private static Log log = LogFactory.getLog(PolarisImpl.class);

	private Orion orion;
	private XDIHttpClient client;

	PolarisImpl(Orion orion, XDIHttpClient client) {

		this.orion = orion;
		this.client = client;
	}

	public void init() throws Exception {

		log.info("init()");
	}

	public void shutdown() {

		log.info("shutdown()");
	}

	/*
	 * Actions
	 */

	public String get(String xdi, String xdiUrl, String format) throws Exception {

		log.debug("get(" + xdi + "," + xdiUrl + "," + format + ")");

		if (! "1".equals(this.orion.loggedin())) throw new RuntimeException("Not signed in.");
		this.client.setUrl(xdiUrl != null ? new URL(xdiUrl) : new URL(this.orion.xdiUri()));

		MessageEnvelope messageEnvelope = MessageEnvelope.fromOperationXriAndXdi(XDIMessagingConstants.XRI_S_GET, xdi);
		MessageResult messageResult = this.client.send(messageEnvelope, null);
		if (messageResult == null) throw new RuntimeException("No result");
		if (log.isDebugEnabled()) log.debug(messageResult.getGraph().toString());

		return messageResult.getGraph().toString(format, null);
	}

	public String add(String xdi, String xdiUrl, String format) throws Exception {

		log.debug("add(" + xdi + "," + xdiUrl + "," + format + ")");

		if (! "1".equals(this.orion.loggedin())) throw new RuntimeException("Not signed in.");
		this.client.setUrl(xdiUrl != null ? new URL(xdiUrl) : new URL(this.orion.xdiUri()));

		MessageEnvelope messageEnvelope = MessageEnvelope.fromOperationXriAndXdi(XDIMessagingConstants.XRI_S_ADD, xdi);
		MessageResult messageResult = this.client.send(messageEnvelope, null);
		if (messageResult == null) throw new RuntimeException("No result");
		if (log.isDebugEnabled()) log.debug(messageResult.getGraph().toString());

		return messageResult.getGraph().toString(format, null);
	}

	public String mod(String xdi, String xdiUrl, String format) throws Exception {

		log.debug("mod(" + xdi + "," + xdiUrl + "," + format + ")");

		if (! "1".equals(this.orion.loggedin())) throw new RuntimeException("Not signed in.");
		this.client.setUrl(xdiUrl != null ? new URL(xdiUrl) : new URL(this.orion.xdiUri()));

		MessageEnvelope messageEnvelope = MessageEnvelope.fromOperationXriAndXdi(XDIMessagingConstants.XRI_S_MOD, xdi);
		MessageResult messageResult = this.client.send(messageEnvelope, null);
		if (messageResult == null) throw new RuntimeException("No result");
		if (log.isDebugEnabled()) log.debug(messageResult.getGraph().toString());

		return messageResult.getGraph().toString(format, null);
	}

	public String del(String xdi, String xdiUrl, String format) throws Exception {

		log.debug("del(" + xdi + "," + xdiUrl + "," + format + ")");

		if (! "1".equals(this.orion.loggedin())) throw new RuntimeException("Not signed in.");
		this.client.setUrl(xdiUrl != null ? new URL(xdiUrl) : new URL(this.orion.xdiUri()));

		MessageEnvelope messageEnvelope = MessageEnvelope.fromOperationXriAndXdi(XDIMessagingConstants.XRI_S_DEL, xdi);
		MessageResult messageResult = this.client.send(messageEnvelope, null);
		if (messageResult == null) throw new RuntimeException("No result");
		if (log.isDebugEnabled()) log.debug(messageResult.getGraph().toString());

		return messageResult.getGraph().toString(format, null);
	}

	public String[] getLiterals(String xdi, String xdiUrl) throws Exception {

		log.debug("getLiterals(" + xdi + "," + xdiUrl + ")");

		if (! "1".equals(this.orion.loggedin())) throw new RuntimeException("Not signed in.");
		this.client.setUrl(xdiUrl != null ? new URL(xdiUrl) : new URL(this.orion.xdiUri()));

		MessageEnvelope messageEnvelope = MessageEnvelope.fromOperationXriAndXdi(XDIMessagingConstants.XRI_S_GET, xdi);
		MessageResult messageResult = this.client.send(messageEnvelope, null);
		if (messageResult == null) throw new RuntimeException("No result");
		if (log.isDebugEnabled()) log.debug(messageResult.getGraph().toString());

		List<String> literals = new Vector<String> ();
		for (Iterator<Literal> i = messageResult.getGraph().getRootContextNode().getAllLiterals(); i.hasNext(); ) literals.add((i.next()).getLiteralData());

		return literals.toArray(new String[literals.size()]);
	}

	public String getLiteral(String xdi, String xdiUrl) throws Exception {

		log.debug("getLiteral(" + xdi + "," + xdiUrl + ")");

		String[] literals = this.getLiterals(xdi, xdiUrl);
		if (literals == null || literals.length < 1) return(null);

		return literals[0];
	}

	public String[] getRelations(String xdi, String xdiUrl) throws Exception {

		log.debug("getRelations(" + xdi + "," + xdiUrl + ")");

		if (! "1".equals(this.orion.loggedin())) throw new RuntimeException("Not signed in.");
		this.client.setUrl(xdiUrl != null ? new URL(xdiUrl) : new URL(this.orion.xdiUri()));

		MessageEnvelope messageEnvelope = MessageEnvelope.fromOperationXriAndXdi(XDIMessagingConstants.XRI_S_GET, xdi);
		MessageResult messageResult = this.client.send(messageEnvelope, null);
		if (messageResult == null) throw new RuntimeException("No result");
		if (log.isDebugEnabled()) log.debug(messageResult.getGraph().toString());

		List<String> relations = new Vector<String> ();
		for (Iterator<Relation> i = messageResult.getGraph().getRootContextNode().getAllRelations(); i.hasNext(); ) relations.add((i.next()).getRelationXri().toString());

		return relations.toArray(new String[relations.size()]);
	}

	public String getRelation(String xdi, String xdiUrl) throws Exception {

		log.debug("getRelation(" + xdi + "," + xdiUrl + ")");

		String[] relations = this.getRelations(xdi, xdiUrl);
		if (relations == null) return(null);

		return relations[0];
	}

	public String execute(String message, String xdiUrl, String format) throws Exception {

		log.debug("execute(" + message + "," + xdiUrl + "," + format + ")");

		if (! "1".equals(this.orion.loggedin())) throw new RuntimeException("Not signed in.");
		this.client.setUrl(xdiUrl != null ? new URL(xdiUrl) : new URL(this.orion.xdiUri()));

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		XDIReaderRegistry.getAuto().read(graph, new StringReader(message), null);
		MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(graph);

		MessageResult messageResult = this.client.send(messageEnvelope, null);
		if (messageResult == null) throw new RuntimeException("No result");
		if (log.isDebugEnabled()) log.debug(messageResult.getGraph().toString());

		if (format == null) format = "XDI/JSON";

		return messageResult.getGraph().toString(format, null);
	}
}
