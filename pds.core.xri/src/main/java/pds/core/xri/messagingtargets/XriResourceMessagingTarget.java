package pds.core.xri.messagingtargets;

import org.eclipse.higgins.xdi4j.Literal;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.server.impl.ResourceHandler;
import org.eclipse.higgins.xdi4j.messaging.server.impl.ResourceMessagingTarget;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.xri.XriPdsConnection;

public class XriResourceMessagingTarget extends ResourceMessagingTarget {

	private XriPdsConnection pdsConnection;

	public XriResourceMessagingTarget() {

		super(true);
	}

	@Override
	public ResourceHandler getResource(Message message, Subject operationSubject) throws MessagingException {

		if (operationSubject.getSubjectXri().equals(this.pdsConnection.getCanonical())) {

			return new XriSubjectResourceHandler(message, operationSubject, this.pdsConnection);
		}

		return null;
	}

	@Override
	public ResourceHandler getResource(Message message, Subject operationSubject, Predicate operationPredicate, Literal operationLiteral) throws MessagingException {

		if (operationSubject.getSubjectXri().equals(this.pdsConnection.getCanonical()) &&
				operationPredicate.getPredicateXri().equals(new XRI3Segment("$password"))) {

			return new XriSubjectPredicateLiteralResourceHandler(message, operationSubject, operationPredicate, operationLiteral, this.pdsConnection);
		}

		return null;
	}

	public XriPdsConnection getPdsConnection() {

		return this.pdsConnection;
	}

	public void setPdsConnection(XriPdsConnection pdsConnection) {

		this.pdsConnection = pdsConnection;
	}
}
