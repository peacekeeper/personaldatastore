package pds.core.base.messagingtargets;


import pds.core.base.PdsInstance;
import xdi2.core.ContextNode;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.impl.AbstractResourceHandler;

public class ContextGraphResourceHandler extends AbstractResourceHandler {

	private PdsInstance pdsInstance;

	public ContextGraphResourceHandler(Operation operation, ContextNode operationContextNode, PdsInstance pdsInstance) {

		super(operation, operationContextNode);

		this.pdsInstance = pdsInstance;
	}

	@Override
	public boolean executeGet(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		messageResult.getGraph().getRootContextNode().createRelation(new XRI3Segment("$is$a"), new XRI3Segment("($xdi$v$1)"));
		messageResult.getGraph().getRootContextNode().createRelation(new XRI3Segment("$is$a"), new XRI3Segment("($pds$v$1)"));
		messageResult.getGraph().getRootContextNode().createRelation(new XRI3Segment("$is($xdi$v$1)"), this.pdsInstance.getCanonical());

		String[] endpoints = this.pdsInstance.getEndpoints();
		int httpCount = 1;
		int httpsCount = 1;

		if (endpoints != null) {

			for (String endpoint : endpoints) {

				if (endpoint.startsWith("https://")) {

					messageResult.getGraph().findContextNode(new XRI3Segment("$https$uri$" + httpsCount), true).createLiteral(endpoint);
					httpsCount++;
				} else if (endpoint.startsWith("http://")) {

					messageResult.getGraph().findContextNode(new XRI3Segment("$http$uri$" + httpCount), true).createLiteral(endpoint);
					httpCount++;
				}
			}
		}

		return true;
	}
}
