package pds.core.messagingtargets.pds;

import org.eclipse.higgins.xdi4j.Literal;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.server.impl.ResourceHandler;
import org.eclipse.higgins.xdi4j.messaging.server.impl.ResourceMessagingTarget;
import org.eclipse.higgins.xdi4j.messaging.server.interceptor.impl.ReadOnlyAddressInterceptor;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.PdsConnection;

public class PdsResourceMessagingTarget extends ResourceMessagingTarget {

	private ReadOnlyAddressInterceptor readOnlyAddressInterceptor;

	private PdsConnection pdsConnection;

	public PdsResourceMessagingTarget() {

		super(true);

		this.readOnlyAddressInterceptor = new ReadOnlyAddressInterceptor();
		this.getAddressInterceptors().add(this.readOnlyAddressInterceptor);
	}

	@Override
	public ResourceHandler getResource(Message message, Subject operationSubject) throws MessagingException {

		if (operationSubject.getSubjectXri().equals(this.pdsConnection.getCanonical())) {

			return new PdsSubjectResourceHandler(message, operationSubject, this.pdsConnection);
		}

		return null;
	}

	@Override
	public ResourceHandler getResource(Message message, Subject operationSubject, Predicate operationPredicate, Literal operationLiteral) throws MessagingException {

		if (operationSubject.getSubjectXri().equals(this.pdsConnection.getCanonical()) &&
				operationPredicate.getPredicateXri().equals(new XRI3Segment("$password"))) {

			return new PdsSubjectPredicateLiteralResourceHandler(message, operationSubject, operationPredicate, operationLiteral, this.pdsConnection);
		}

		return null;
	}

	public PdsConnection getPdsConnection() {

		return this.pdsConnection;
	}

	public void setPdsConnection(PdsConnection pdsConnection) {

		this.pdsConnection = pdsConnection;

		this.readOnlyAddressInterceptor.setReadOnlyAddresses(new XRI3[] {

				new XRI3(pdsConnection.getCanonical() + "/$is"),
				new XRI3(pdsConnection.getCanonical() + "/$$is"),
				new XRI3(pdsConnection.getCanonical() + "/$is$a")
		});
	}
}