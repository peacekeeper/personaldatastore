package pds.core.xri.messagingtargets;

import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.constants.DictionaryConstants;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.server.impl.ResourceHandler;
import org.eclipse.higgins.xdi4j.messaging.server.impl.ResourceMessagingTarget;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.xri.XriPdsInstanceFactory;

public class RootResourceMessagingTarget extends ResourceMessagingTarget {

	private XriPdsInstanceFactory pdsInstanceFactory;

	public RootResourceMessagingTarget() {

		super(true);
	}

	@Override
	public ResourceHandler getResource(Message message, Subject subject) throws MessagingException {

		if (message.getMessageEnvelope().getGraph().containsStatement(subject.getSubjectXri(), DictionaryConstants.XRI_IS_A, new XRI3Segment("=")) ||
				message.getMessageEnvelope().getGraph().containsStatement(subject.getSubjectXri(), DictionaryConstants.XRI_IS_A, new XRI3Segment("@"))) {

			return new RootSubjectResourceHandler(message, subject, this.pdsInstanceFactory);
		}

		return null;
	}

	public XriPdsInstanceFactory getPdsInstanceFactory() {

		return this.pdsInstanceFactory;
	}

	public void setPdsInstanceFactory(XriPdsInstanceFactory pdsInstanceFactory) {

		this.pdsInstanceFactory = pdsInstanceFactory;
	}
}
