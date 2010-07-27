package pds.core;

public class PdsConnectionException extends Exception {

	private static final long serialVersionUID = -4979095952246925469L;

	public PdsConnectionException() {

		super();
	}

	public PdsConnectionException(String message, Throwable ex) {

		super(message, ex);
	}

	public PdsConnectionException(String message) {

		super(message);
	}

	public PdsConnectionException(Throwable ex) {

		super(ex);
	}
}
