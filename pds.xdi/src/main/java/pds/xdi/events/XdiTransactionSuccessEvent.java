package pds.xdi.events;

import java.util.Date;

import org.eclipse.higgins.xdi4j.messaging.MessageEnvelope;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;

public class XdiTransactionSuccessEvent extends XdiTransactionEvent {

	private static final long serialVersionUID = -547735780296539623L;

	private MessageResult messageResult;

	public XdiTransactionSuccessEvent(Object source, MessageEnvelope messageEnvelope, Date beginTimestamp, Date endTimestamp, MessageResult messageResult) {

		super(source, messageEnvelope, beginTimestamp, endTimestamp);

		this.messageResult = messageResult;
	}

	public MessageResult getMessageResult() {

		return this.messageResult;
	}
}
