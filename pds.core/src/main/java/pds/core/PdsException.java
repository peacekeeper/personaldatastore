package pds.core;

public class PdsException extends Exception {

	private static final long serialVersionUID = -4979095952246925469L;

	public PdsException() {

		super();
	}

	public PdsException(String message, Throwable ex) {

		super(message, ex);
	}

	public PdsException(String message) {

		super(message);
	}

	public PdsException(Throwable ex) {

		super(ex);
	}
}
