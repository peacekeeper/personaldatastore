package pds.core.base.messagingtargets.handlers;


import pds.core.base.PdsInstance;
import xdi2.core.ContextNode;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.impl.AbstractContextNodeHandler;

public class PdsRootContextNodeHandler extends AbstractContextNodeHandler {

	private PdsInstance pdsInstance;

	public PdsRootContextNodeHandler(PdsInstance pdsInstance) {

		this.pdsInstance = pdsInstance;
	}

	@Override
	public boolean executeGetOnContextNode(ContextNode operationContextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode messageResultRootContextNode = messageResult.getGraph().getRootContextNode();

		messageResultRootContextNode.createRelation(new XRI3Segment("$is$a"), new XRI3Segment("($xdi$v$1)"));
		messageResultRootContextNode.createRelation(new XRI3Segment("$is$a"), new XRI3Segment("($pds$v$1)"));
		messageResultRootContextNode.createRelation(new XRI3Segment("$is($xdi$v$1)"), this.pdsInstance.getCanonical());

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
