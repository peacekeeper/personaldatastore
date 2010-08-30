package pds.web.xdi.events;

public interface XdiTransactionListener {

	public void onXdiTransactionSuccess(XdiTransactionSuccessEvent xdiTransactionSuccessEvent);
	public void onXdiTransactionFailure(XdiTransactionFailureEvent xdiTransactionFailureEvent);
}
