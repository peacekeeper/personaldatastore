package pds.store.xri;

/**
 * An Exception thrown by various methods accessing the XriStore.
 */
public class XriStoreException extends Exception {

	private static final long serialVersionUID = -1947618206676561636L;

	public XriStoreException() {

		super();
	}

	public XriStoreException(String message) {

		super(message);
	}

	public XriStoreException(Throwable ex) {

		super(ex);
	}

	public XriStoreException(String message, Throwable ex) {

		super(message, ex);
	}
}
