package pds.p2p.api.sirius;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pds.p2p.api.Sirius;
import pds.p2p.api.Vega;
import xdi2.client.local.XDILocalClient;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.constants.XDIMessagingConstants;

public class SiriusImpl implements Sirius {

	private static Log log = LogFactory.getLog(SiriusImpl.class);

	private Vega vega;
	private XDILocalClient client;

	SiriusImpl(Vega vega, XDILocalClient client) {

		this.vega = vega;
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

	public String get(String xdi, String format) throws Exception {

		log.debug("get(" + xdi + "," + format + ")");

		if (! "1".equals(this.vega.connected())) throw new RuntimeException("Not connected.");

		MessageEnvelope messageEnvelope = messageEnvelopeFromOperationXriAndXdi(XDIMessagingConstants.XRI_S_GET, xdi);
		MessageResult messageResult = this.client.send(messageEnvelope, null);
		if (messageResult == null) throw new RuntimeException("No result");
		if (log.isDebugEnabled()) log.debug(messageResult.getGraph().toString());

		return messageResult.getGraph().toString(format, null);
	}

	public String add(String xdi, String format) throws Exception {

		log.debug("add(" + xdi + "," + format + ")");

		if (! "1".equals(this.vega.connected())) throw new RuntimeException("Not connected.");

		MessageEnvelope messageEnvelope = messageEnvelopeFromOperationXriAndXdi(XDIMessagingConstants.XRI_S_ADD, xdi);
		MessageResult messageResult = this.client.send(messageEnvelope, null);
		if (messageResult == null) throw new RuntimeException("No result");
		if (log.isDebugEnabled()) log.debug(messageResult.getGraph().toString());

		return messageResult.getGraph().toString(format, null);
	}

	public String mod(String xdi, String format) throws Exception {

		log.debug("mod(" + xdi + "," + format + ")");

		if (! "1".equals(this.vega.connected())) throw new RuntimeException("Not connected.");

		MessageEnvelope messageEnvelope = messageEnvelopeFromOperationXriAndXdi(XDIMessagingConstants.XRI_S_MOD, xdi);
		MessageResult messageResult = this.client.send(messageEnvelope, null);
		if (messageResult == null) throw new RuntimeException("No result");
		if (log.isDebugEnabled()) log.debug(messageResult.getGraph().toString());

		return messageResult.getGraph().toString(format, null);
	}

	public String del(String xdi, String format) throws Exception {

		log.debug("del(" + xdi + "," + format + ")");

		if (! "1".equals(this.vega.connected())) throw new RuntimeException("Not connected.");

		MessageEnvelope messageEnvelope = messageEnvelopeFromOperationXriAndXdi(XDIMessagingConstants.XRI_S_DEL, xdi);
		MessageResult messageResult = this.client.send(messageEnvelope, null);
		if (messageResult == null) throw new RuntimeException("No result");
		if (log.isDebugEnabled()) log.debug(messageResult.getGraph().toString());

		return messageResult.getGraph().toString(format, null);
	}

	public String[] getLiterals(String xdi) throws Exception {

		log.debug("getLiterals(" + xdi + ")");

		if (! "1".equals(this.vega.connected())) throw new RuntimeException("Not connected.");

		MessageEnvelope messageEnvelope = messageEnvelopeFromOperationXriAndXdi(XDIMessagingConstants.XRI_S_GET, xdi);
		MessageResult messageResult = this.client.send(messageEnvelope, null);
		if (messageResult == null) throw new RuntimeException("No result");
		if (log.isDebugEnabled()) log.debug(messageResult.getGraph().toString());

		List<String> literals = new Vector<String> ();
		for (Iterator<Literal> i = messageResult.getGraph().getRootContextNode().getAllLiterals(); i.hasNext(); ) literals.add((i.next()).getLiteralData());

		return literals.toArray(new String[literals.size()]);
	}

	public String getLiteral(String xri) throws Exception {

		log.debug("getLiteral(" + xri + ")");

		String[] literals = this.getLiterals(xri);
		if (literals == null || literals.length < 1) return null;

		return literals[0];
	}

	public String[] getRelations(String xdi) throws Exception {

		log.debug("getRelations(" + xdi + ")");

		if (! "1".equals(this.vega.connected())) throw new RuntimeException("Not connected.");

		MessageEnvelope messageEnvelope = messageEnvelopeFromOperationXriAndXdi(XDIMessagingConstants.XRI_S_GET, xdi);
		MessageResult messageResult = this.client.send(messageEnvelope, null);
		if (messageResult == null) throw new RuntimeException("No result");
		if (log.isDebugEnabled()) log.debug(messageResult.getGraph().toString());

		List<String> relations = new Vector<String> ();
		for (Iterator<Relation> i = messageResult.getGraph().getRootContextNode().getAllRelations(); i.hasNext(); ) relations.add((i.next()).getRelationXri().toString());

		return relations.toArray(new String[relations.size()]);
	}

	public String getRelation(String xri) throws Exception {

		log.debug("getRelation(" + xri + ")");

		String[] relations = this.getRelations(xri);
		if (relations == null || relations.length < 1) return null;

		return relations[0];
	}

	public String execute(String message, String format) throws Exception {

		log.debug("execute(" + message + "," + format + ")");

		if (! "1".equals(this.vega.connected())) throw new RuntimeException("Not connected.");

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		XDIReaderRegistry.getAuto().read(graph, new StringReader(message));
		MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(graph);

		MessageResult messageResult = this.client.send(messageEnvelope, null);
		if (messageResult == null) throw new RuntimeException("No result");
		if (log.isDebugEnabled()) log.debug(messageResult.getGraph().toString());

		if (format == null) format = "XDI/JSON";

		return messageResult.getGraph().toString(format, null);
	}

	private static final MessageEnvelope messageEnvelopeFromOperationXriAndXdi(XRI3Segment operationXri, String xdi) throws Xdi2ParseException {

		try {

			if (xdi == null) xdi = "()";

			XRI3Segment targetXri = new XRI3Segment(xdi);
			return MessageEnvelope.fromOperationXriAndTargetXri(operationXri, targetXri);
		} catch (Exception ex) {

			return MessageEnvelope.fromOperationXriAndStatement(operationXri, xdi);
		}
	}
}
