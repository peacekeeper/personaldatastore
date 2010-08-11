package pds.core.messagingtargets;


import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractResourceHandler;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.PdsConnection;

public class ContextGraphResourceHandler extends AbstractResourceHandler {

	private PdsConnection pdsConnection;

	public ContextGraphResourceHandler(Message message, Graph operationGraph, PdsConnection pdsConnection) {

		super(message, operationGraph);

		this.pdsConnection = pdsConnection;
	}

	@Override
	public boolean executeGet(Operation operation, MessageResult messageResult, Object executionContext) throws MessagingException {

		messageResult.getGraph().createStatement(new XRI3Segment("$"), new XRI3Segment("$is$a"), new XRI3Segment("($xdi$v$1)"));
		messageResult.getGraph().createStatement(new XRI3Segment("$"), new XRI3Segment("$is$a"), new XRI3Segment("($pds$v$1)"));
		messageResult.getGraph().createStatement(new XRI3Segment("$"), new XRI3Segment("$is($xdi$v$1)"), this.pdsConnection.getCanonical());

		String[] endpoints = this.pdsConnection.getEndpoints();
		int httpCount = 1;
		int httpsCount = 1;

		for (String endpoint : endpoints) {

			if (endpoint.startsWith("https://")) {

				messageResult.getGraph().createStatement(new XRI3Segment("$"), new XRI3Segment("$https$uri$" + httpsCount), endpoint);
				httpsCount++;
			} else if (endpoint.startsWith("http://")) {

				messageResult.getGraph().createStatement(new XRI3Segment("$"), new XRI3Segment("$http$uri$" + httpCount), endpoint);
				httpCount++;
			}
		}

		return true;
	}
}
