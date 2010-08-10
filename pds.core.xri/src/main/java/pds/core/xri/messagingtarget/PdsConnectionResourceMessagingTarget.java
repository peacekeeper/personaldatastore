package pds.core.xri.messagingtarget;

import org.eclipse.higgins.xdi4j.Literal;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.server.impl.ResourceHandler;
import org.eclipse.higgins.xdi4j.messaging.server.impl.ResourceMessagingTarget;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.PdsConnection;

public class PdsConnectionResourceMessagingTarget extends ResourceMessagingTarget {

	private PdsConnection pdsConnection;

	public PdsConnectionResourceMessagingTarget() {

		super(true);
	}

	@Override
	public ResourceHandler getResource(Message message, Subject operationSubject) throws MessagingException {

		if (operationSubject.getSubjectXri().equals(this.pdsConnection.getCanonical())) {

			return new PdsConnectionSubjectResourceHandler(message, operationSubject, this.pdsConnection);
		}

		return null;
	}

	@Override
	public ResourceHandler getResource(Message message, Subject operationSubject, Predicate operationPredicate, Literal operationLiteral) throws MessagingException {

		if (operationSubject.getSubjectXri().equals(this.pdsConnection.getCanonical()) &&
				operationPredicate.getPredicateXri().equals(new XRI3Segment("$password"))) {

			return new PdsConnectionSubjectPredicateLiteralResourceHandler(message, operationSubject, operationPredicate, operationLiteral, this.pdsConnection);
		}

		return null;
	}

	public PdsConnection getPdsConnection() {

		return this.pdsConnection;
	}

	public void setPdsConnection(PdsConnection pdsConnection) {

		this.pdsConnection = pdsConnection;
	}
}