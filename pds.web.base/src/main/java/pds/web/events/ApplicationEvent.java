package pds.web.events;

import java.util.EventObject;

public abstract class ApplicationEvent extends EventObject {

	private static final long serialVersionUID = 4023510206569406429L;
	
	public ApplicationEvent(Object source) {

		super(source);
	}
}
