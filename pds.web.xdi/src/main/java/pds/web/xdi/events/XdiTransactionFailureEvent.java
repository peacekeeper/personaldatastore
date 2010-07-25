package pds.web.xdi.events;

import java.util.Date;

import org.eclipse.higgins.xdi4j.messaging.MessageEnvelope;

public class XdiTransactionFailureEvent extends XdiTransactionEvent {

	private static final long serialVersionUID = -547735780296539623L;

	private Exception exception;

	public XdiTransactionFailureEvent(Object source, MessageEnvelope messageEnvelope, Date beginTimestamp, Date endTimestamp, Exception exception) {

		super(source, messageEnvelope, beginTimestamp, endTimestamp);

		this.exception = exception;
	}

	public Exception getException() {

		return this.exception;
	}
}
