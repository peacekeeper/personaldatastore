package pds.web.xdi.events;

public interface XdiListener {

	public void onXdiTransactionSuccess(XdiTransactionSuccessEvent xdiTransactionSuccessEvent);
	public void onXdiTransactionFailure(XdiTransactionFailureEvent xdiTransactionFailureEvent);
}
