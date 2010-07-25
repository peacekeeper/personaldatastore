package pds.web.xdi.events;

import java.util.Date;

import org.eclipse.higgins.xdi4j.messaging.MessageEnvelope;

public class XdiTransactionEvent extends XdiEvent {

	private static final long serialVersionUID = 5301716219045375638L;

	private MessageEnvelope messageEnvelope;
	private Date beginTimestamp;
	private Date endTimestamp;

	public XdiTransactionEvent(Object source, MessageEnvelope messageEnvelope, Date beginTimestamp, Date endTimestamp) {

		super(source);

		this.messageEnvelope = messageEnvelope;
		this.beginTimestamp = beginTimestamp;
		this.endTimestamp = endTimestamp;
	}

	public MessageEnvelope getMessageEnvelope() {

		return this.messageEnvelope;
	}

	public Date getBeginTimestamp() {

		return this.beginTimestamp;
	}

	public Date getEndTimestamp() {

		return this.endTimestamp;
	}
}
