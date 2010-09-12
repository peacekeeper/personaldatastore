package pds.xdi.events;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;

public interface XdiGraphListener {

	public void onXdiGraphEvent(XdiGraphEvent xdiGraphEvent);
	public XRI3[] xdiGetAddresses();
	public XRI3[] xdiAddAddresses();
	public XRI3[] xdiModAddresses();
	public XRI3[] xdiSetAddresses();
	public XRI3[] xdiDelAddresses();
}
