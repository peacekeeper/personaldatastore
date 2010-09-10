package pds.xdi.events;


public class XdiResolutionEndpointEvent extends XdiResolutionEvent {

	private static final long serialVersionUID = 824251490724932897L;

	private String endpoint;
	private String inumber;

	public XdiResolutionEndpointEvent(Object source, String endpoint, String inumber) {

		super(source);

		this.endpoint = endpoint;
		this.inumber = inumber;
	}

	public String getEndpoint() {

		return this.endpoint;
	}

	public void setEndpoint(String endpoint) {

		this.endpoint = endpoint;
	}

	public String getInumber() {

		return this.inumber;
	}

	public void setInumber(String inumber) {

		this.inumber = inumber;
	}
}
