package pds.xdi.events;

import xdi2.core.xri3.impl.XRI3Segment;

public interface XdiGraphListener {

	public void onXdiGraphEvent(XdiGraphEvent xdiGraphEvent);
	public XRI3Segment[] xdiGetAddresses();
	public XRI3Segment[] xdiAddAddresses();
	public XRI3Segment[] xdiModAddresses();
	public XRI3Segment[] xdiDelAddresses();
}
