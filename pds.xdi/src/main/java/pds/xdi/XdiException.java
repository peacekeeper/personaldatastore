package pds.xdi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * An Exception thrown by various methods accessing the Store.
 */
public class XdiException extends Exception {

	private static final long serialVersionUID = 3274656239599696879L;

	private static Log log = LogFactory.getLog(XdiException.class);

	public XdiException() {

		super();

		log.error(null);
	}

	public XdiException(String message) {

		super(message);

		log.error(message);
	}

	public XdiException(String message, Throwable t) {

		super(message, t);

		log.error(message, t);
	}

	public XdiException(Throwable t) {

		super(t);

		log.error(t);
	}
}
