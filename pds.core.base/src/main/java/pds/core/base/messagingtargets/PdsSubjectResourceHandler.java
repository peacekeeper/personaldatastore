package pds.core.base.messagingtargets;


import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.constants.DictionaryConstants;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractResourceHandler;
import org.eclipse.higgins.xdi4j.messaging.server.impl.ExecutionContext;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.base.PdsInstance;

public class PdsSubjectResourceHandler extends AbstractResourceHandler {

	private PdsInstance pdsInstance;

	public PdsSubjectResourceHandler(Message message, Subject subject, PdsInstance pdsInstance) {

		super(message, subject);

		this.pdsInstance = pdsInstance;
	}

	@Override
	public boolean executeGet(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws MessagingException {

		// canonical, type and aliases

		if (this.pdsInstance.getCanonical() != null) {

			messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), new XRI3Segment("$$is"), this.pdsInstance.getCanonical());
		}

		messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), DictionaryConstants.XRI_IS_A, new XRI3Segment(this.operationSubject.getSubjectXri().getFirstSubSegment().getGCS().toString()));

		XRI3Segment[] aliases = this.pdsInstance.getAliases();
		for (XRI3Segment alias : aliases) {

			messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), DictionaryConstants.XRI_IS, alias);
		}

		// done

		return true;
	}
}
