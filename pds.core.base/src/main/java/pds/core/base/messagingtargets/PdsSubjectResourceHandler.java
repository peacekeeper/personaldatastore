package pds.core.base.messagingtargets;


import pds.core.base.PdsInstance;
import xdi2.core.ContextNode;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.impl.AbstractResourceHandler;

public class PdsSubjectResourceHandler extends AbstractResourceHandler {

	private PdsInstance pdsInstance;

	public PdsSubjectResourceHandler(Operation operation, ContextNode operationContextNode, PdsInstance pdsInstance) {

		super(operation, operationContextNode);

		this.pdsInstance = pdsInstance;
	}

	@Override
	public boolean executeGet(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// canonical, type and aliases

		if (this.pdsInstance.getCanonical() != null) {

			messageResult.getGraph().getRootContextNode().createRelation(new XRI3Segment("$is"), this.pdsInstance.getCanonical());
		}

		messageResult.getGraph().getRootContextNode().createRelation(new XRI3Segment("$is$a"), new XRI3Segment(this.operationContextNode.getXri().getFirstSubSegment().getGCS().toString()));

		XRI3Segment[] aliases = this.pdsInstance.getAliases();
		for (XRI3Segment alias : aliases) {

			messageResult.getGraph().getRootContextNode().createRelation(new XRI3Segment("$is"), alias);
		}

		// done

		return true;
	}
}
