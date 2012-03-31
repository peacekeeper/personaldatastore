package pds.p2p.api.vega.util;

import rice.Continuation;

public class BlockingContinuation<R, E extends Exception> implements Continuation<R, E> {

	private R result = null;
	private E ex = null;
	private boolean notified = false;

	public synchronized void receiveResult(R result) {

		this.result = result;
		this.notified = true;
		this.notifyAll();
	}

	public synchronized void receiveException(E ex) {

		this.ex = ex;
		this.notified = true;
		this.notifyAll();
	}

	public synchronized void block() throws InterruptedException {

		if (! this.notified) this.wait();
	}

	public boolean hasException() {

		return(this.ex != null);
	}

	public boolean hasResult() {

		return(this.result != null);
	}

	public R getResult() {

		return(this.result);
	}

	public E getException() {

		return(this.ex);
	}
}