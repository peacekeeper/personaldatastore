package pds.core.xri.messagingtargets;

import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.constants.DictionaryConstants;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.server.impl.ResourceHandler;
import org.eclipse.higgins.xdi4j.messaging.server.impl.ResourceMessagingTarget;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.xri.XriPdsConnectionFactory;

public class RootResourceMessagingTarget extends ResourceMessagingTarget {

	private XriPdsConnectionFactory pdsConnectionFactory;

	public RootResourceMessagingTarget() {

		super(true);
	}

	@Override
	public ResourceHandler getResource(Message message, Subject subject) throws MessagingException {

		if (message.getMessageEnvelope().getGraph().containsStatement(subject.getSubjectXri(), DictionaryConstants.XRI_INHERITANCE, new XRI3Segment("=")) ||
				message.getMessageEnvelope().getGraph().containsStatement(subject.getSubjectXri(), DictionaryConstants.XRI_INHERITANCE, new XRI3Segment("@"))) {

			return new RootSubjectResourceHandler(message, subject, this.pdsConnectionFactory);
		}

		return null;
	}

	public XriPdsConnectionFactory getPdsConnectionFactory() {

		return this.pdsConnectionFactory;
	}

	public void setPdsConnectionFactory(XriPdsConnectionFactory pdsConnectionFactory) {

		this.pdsConnectionFactory = pdsConnectionFactory;
	}
}
