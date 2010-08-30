package pds.web.events;

import pds.web.xdi.XdiContext;

public class ApplicationContextClosedEvent extends ApplicationEvent {

	private static final long serialVersionUID = -2741247681465378631L;

	private XdiContext context;

	public ApplicationContextClosedEvent(Object source, XdiContext context) {

		super(source);

		this.context = context;
	}

	public XdiContext getContext() {

		return this.context;
	}
}
