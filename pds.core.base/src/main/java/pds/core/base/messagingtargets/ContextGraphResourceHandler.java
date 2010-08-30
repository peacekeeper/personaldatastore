package pds.core.base.messagingtargets;


import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractResourceHandler;
import org.eclipse.higgins.xdi4j.messaging.server.impl.ExecutionContext;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.base.PdsInstance;

public class ContextGraphResourceHandler extends AbstractResourceHandler {

	private PdsInstance pdsInstance;

	public ContextGraphResourceHandler(Message message, Graph operationGraph, PdsInstance pdsInstance) {

		super(message, operationGraph);

		this.pdsInstance = pdsInstance;
	}

	@Override
	public boolean executeGet(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws MessagingException {

		messageResult.getGraph().createStatement(new XRI3Segment("$"), new XRI3Segment("$is$a"), new XRI3Segment("($xdi$v$1)"));
		messageResult.getGraph().createStatement(new XRI3Segment("$"), new XRI3Segment("$is$a"), new XRI3Segment("($pds$v$1)"));
		messageResult.getGraph().createStatement(new XRI3Segment("$"), new XRI3Segment("$is($xdi$v$1)"), this.pdsInstance.getCanonical());

		String[] endpoints = this.pdsInstance.getEndpoints();
		int httpCount = 1;
		int httpsCount = 1;

		if (endpoints != null) {

			for (String endpoint : endpoints) {

				if (endpoint.startsWith("https://")) {

					messageResult.getGraph().createStatement(new XRI3Segment("$"), new XRI3Segment("$https$uri$" + httpsCount), endpoint);
					httpsCount++;
				} else if (endpoint.startsWith("http://")) {

					messageResult.getGraph().createStatement(new XRI3Segment("$"), new XRI3Segment("$http$uri$" + httpCount), endpoint);
					httpCount++;
				}
			}
		}

		return true;
	}
}
