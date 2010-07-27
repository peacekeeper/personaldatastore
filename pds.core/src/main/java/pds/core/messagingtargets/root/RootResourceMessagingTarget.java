package pds.core.messagingtargets.root;

import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.server.impl.ResourceHandler;
import org.eclipse.higgins.xdi4j.messaging.server.impl.ResourceMessagingTarget;

import pds.core.PdsConnectionFactory;

public class RootResourceMessagingTarget extends ResourceMessagingTarget {

	private PdsConnectionFactory pdsConnectionFactory;

	public RootResourceMessagingTarget() {

		super(true);
	}

	@Override
	public ResourceHandler getResource(Message message, Subject subject) throws MessagingException {

		return new RootSubjectResourceHandler(message, subject, this.pdsConnectionFactory);
	}

	public PdsConnectionFactory getPdsConnectionFactory() {

		return this.pdsConnectionFactory;
	}

	public void setPdsConnectionFactory(PdsConnectionFactory pdsConnectionFactory) {

		this.pdsConnectionFactory = pdsConnectionFactory;
	}
}
