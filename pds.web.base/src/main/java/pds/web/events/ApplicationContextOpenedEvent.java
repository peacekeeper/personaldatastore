package pds.web.events;

import pds.xdi.XdiEndpoint;

public class ApplicationContextOpenedEvent extends ApplicationEvent {

	private static final long serialVersionUID = -7396499183586044715L;

	private XdiEndpoint endpoint;

	public ApplicationContextOpenedEvent(Object source, XdiEndpoint endpoint) {

		super(source);

		this.endpoint = endpoint;
	}

	public XdiEndpoint getEndpoint() {

		return this.endpoint;
	}
}
