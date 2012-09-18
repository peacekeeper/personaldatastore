package pds.web.events;

import pds.xdi.XdiEndpoint;

public class ApplicationContextClosedEvent extends ApplicationEvent {

	private static final long serialVersionUID = -2741247681465378631L;

	private XdiEndpoint endpoint;

	public ApplicationContextClosedEvent(Object source, XdiEndpoint endpoint) {

		super(source);

		this.endpoint = endpoint;
	}

	public XdiEndpoint getContext() {

		return this.endpoint;
	}
}
