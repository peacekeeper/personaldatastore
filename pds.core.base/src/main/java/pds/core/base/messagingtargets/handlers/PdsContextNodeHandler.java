package pds.core.base.messagingtargets.handlers;


import pds.core.base.PdsInstance;
import xdi2.core.ContextNode;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;

public class PdsContextNodeHandler extends AbstractContextNodeHandler {

	private PdsInstance pdsInstance;

	public PdsContextNodeHandler(PdsInstance pdsInstance) {

		this.pdsInstance = pdsInstance;
	}

	@Override
	public boolean executeGetOnContextNode(ContextNode operationContextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// canonical, type and aliases

		if (this.pdsInstance.getCanonical() != null) {

			messageResult.getGraph().getRootContextNode().createRelation(new XRI3Segment("$is"), this.pdsInstance.getCanonical());
		}

		messageResult.getGraph().getRootContextNode().createRelation(new XRI3Segment("$is$a"), new XRI3Segment(operationContextNode.getXri().getFirstSubSegment().getGCS().toString()));

		XRI3Segment[] aliases = this.pdsInstance.getAliases();
		for (XRI3Segment alias : aliases) {

			messageResult.getGraph().getRootContextNode().createRelation(new XRI3Segment("$is"), alias);
		}

		// done

		return true;
	}
}
