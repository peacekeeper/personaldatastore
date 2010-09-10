package pds.xdi.events;


public class XdiResolutionInumberEvent extends XdiResolutionEvent {

	private static final long serialVersionUID = -5175322176882267482L;

	private String inumber;
	private String endpoint;

	public XdiResolutionInumberEvent(Object source, String inumber, String endpoint) {

		super(source);

		this.inumber = inumber;
		this.endpoint = endpoint;
	}

	public String getInumber() {

		return this.inumber;
	}

	public void setInumber(String inumber) {

		this.inumber = inumber;
	}

	public String getEndpoint() {

		return this.endpoint;
	}

	public void setEndpoint(String endpoint) {

		this.endpoint = endpoint;
	}
}
