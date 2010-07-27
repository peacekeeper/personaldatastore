package pds.core.messagingtargets.root;


import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.constants.DictionaryConstants;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractResourceHandler;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.PdsConnection;
import pds.core.PdsConnectionException;
import pds.core.PdsConnectionFactory;

public class RootSubjectResourceHandler extends AbstractResourceHandler {

	private PdsConnectionFactory pdsConnectionFactory;

	public RootSubjectResourceHandler(Message message, Subject subject, PdsConnectionFactory pdsConnectionFactory) {

		super(message, subject);

		this.pdsConnectionFactory = pdsConnectionFactory;
	}

	@Override
	public boolean executeGet(Operation operation, MessageResult messageResult, Object executionContext) throws MessagingException {

		// read information from the message

		String identifier = this.operationSubject.getSubjectXri().toString();

		// retrieve the PDS connection

		PdsConnection pdsConnection;

		try {

			pdsConnection = this.pdsConnectionFactory.getPdsConnection(identifier);
		} catch (PdsConnectionException ex) {

			throw new MessagingException("Cannot retrieve PDS connection: " + ex.getMessage(), ex);
		}

		// anyone can check if a PDS exists or not

		if (pdsConnection == null) return false;

		messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), DictionaryConstants.XRI_INHERITANCE, new XRI3Segment(this.operationSubject.getSubjectXri().getFirstSubSegment().getGCS().toString()));

		String[] aliases = pdsConnection.getAliases();
		for (String alias : aliases) {

			messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), DictionaryConstants.XRI_EQUIVALENCE, new XRI3Segment(alias));
		}

		if (pdsConnection.getCanonical() != null) {

			messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), new XRI3Segment("$$is"), pdsConnection.getCanonical());
		}

		return true;
	}
}
