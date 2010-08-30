package pds.web.events;

import pds.web.xdi.objects.XdiContext;

public class ApplicationContextOpenedEvent extends ApplicationEvent {

	private static final long serialVersionUID = -7396499183586044715L;

	private XdiContext context;

	public ApplicationContextOpenedEvent(Object source, XdiContext context) {

		super(source);

		this.context = context;
	}

	public XdiContext getContext() {

		return this.context;
	}
}
