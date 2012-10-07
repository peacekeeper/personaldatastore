package pds.xdi.events;

import java.util.Date;

import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;

public class XdiTransactionEvent extends XdiEvent {

	private static final long serialVersionUID = 5301716219045375638L;

	private MessageEnvelope messageEnvelope;
	private MessageResult messageResult;
	private Date beginTimestamp;
	private Date endTimestamp;

	public XdiTransactionEvent(Object source, MessageEnvelope messageEnvelope, MessageResult messageResult, Date beginTimestamp, Date endTimestamp) {

		super(source);

		this.messageEnvelope = messageEnvelope;
		this.messageResult = messageResult;
		this.beginTimestamp = beginTimestamp;
		this.endTimestamp = endTimestamp;
	}

	public MessageEnvelope getMessageEnvelope() {

		return this.messageEnvelope;
	}

	public MessageResult getMessageResult() {

		return this.messageResult;
	}

	public Date getBeginTimestamp() {

		return this.beginTimestamp;
	}

	public Date getEndTimestamp() {

		return this.endTimestamp;
	}
}
