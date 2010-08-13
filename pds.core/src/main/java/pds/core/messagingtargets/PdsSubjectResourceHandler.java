package pds.core.messagingtargets;


import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.constants.DictionaryConstants;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractResourceHandler;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.PdsConnection;

public class PdsSubjectResourceHandler extends AbstractResourceHandler {

	private PdsConnection pdsConnection;

	public PdsSubjectResourceHandler(Message message, Subject subject, PdsConnection pdsConnection) {

		super(message, subject);

		this.pdsConnection = pdsConnection;
	}

	@Override
	public boolean executeGet(Operation operation, MessageResult messageResult, Object executionContext) throws MessagingException {

		// canonical, type and aliases

		if (this.pdsConnection.getCanonical() != null) {

			messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), new XRI3Segment("$$is"), this.pdsConnection.getCanonical());
		}

		messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), DictionaryConstants.XRI_INHERITANCE, new XRI3Segment(this.operationSubject.getSubjectXri().getFirstSubSegment().getGCS().toString()));

		XRI3Segment[] aliases = this.pdsConnection.getAliases();
		for (XRI3Segment alias : aliases) {

			messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), DictionaryConstants.XRI_EQUIVALENCE, alias);
		}

		// done

		return true;
	}
}
