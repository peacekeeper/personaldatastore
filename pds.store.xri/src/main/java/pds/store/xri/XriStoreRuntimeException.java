package pds.store.xri;

/**
 * A RuntimeException thrown by various methods accessing the Store.
 */
public class XriStoreRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 6067388237398905564L;

	public XriStoreRuntimeException() {

		super();
	}

	public XriStoreRuntimeException(String message) {

		super(message);
	}

	public XriStoreRuntimeException(Throwable ex) {

		super(ex);
	}

	public XriStoreRuntimeException(String message, Throwable ex) {

		super(message, ex);
	}
}
