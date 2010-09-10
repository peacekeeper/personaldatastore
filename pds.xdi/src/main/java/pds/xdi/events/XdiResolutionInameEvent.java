package pds.xdi.events;


public class XdiResolutionInameEvent extends XdiResolutionEvent {

	private static final long serialVersionUID = 6116027745663660332L;

	private String iname;
	private String inumber;
	
	public XdiResolutionInameEvent(Object source, String iname, String inumber) {

		super(source);

		this.iname = iname;
		this.inumber = inumber;
	}

	public String getIname() {
		
		return this.iname;
	}

	public void setIname(String iname) {
		
		this.iname = iname;
	}

	public String getInumber() {
		
		return this.inumber;
	}

	public void setInumber(String inumber) {

		this.inumber = inumber;
	}
}
