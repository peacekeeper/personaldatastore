package pds.web.logger;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pds.web.logger.events.LogEvent;
import pds.web.logger.events.LogListener;

/**
 * Logger system for CardGears.
 * 
 * Every log entry has the following:
 * - A level (INFO, WARNING or PROBLEM)
 * - An arbitrary message
 * - A stacktrace (if the level is PROBLEM)
 * - An arbitrary type -optional-
 * - An application instance ID of the application instance where the event occurred -optional-
 *
 * Every log entry is saved in the Store and remains associated with the storeObject if one was
 * provided.
 * 
 * Every log entry also gets distributed as an event in the Events system, if an applicationInstanceId
 * was provided.
 */
public class Logger implements Serializable {

	private static final long serialVersionUID = 7234006165333916570L;

	private static final Log log = LogFactory.getLog(Logger.class.getName());

	private List<LogListener> logListeners;

	public Logger() {

		this.logListeners = new ArrayList<LogListener> ();
	}

	public void info(String message, String type) {

		LogEntry logEntry = null;

		try {

			logEntry = new LogEntry();
			logEntry.setLevel("INFO");
			logEntry.setType(type);
			logEntry.setMessage(message);
		} catch (Exception ex) {

			log.error("Cannot save INFO log event: " + message + ": " + ex.getMessage(), ex);
			if (logEntry == null) return;
		}

		this.fireLogEvent(new LogEvent(this, logEntry));

		log.info(message);
	}

	public void warning(String message, String type) {

		LogEntry logEntry = null;

		try {

			logEntry = new LogEntry();
			logEntry.setLevel("WARNING");
			logEntry.setType(type);
			logEntry.setMessage(message);
		} catch (Exception ex) {

			log.error("Cannot save WARNING log event: " + message + ": " + ex.getMessage(), ex);
			if (logEntry == null) return;
		}

		this.fireLogEvent(new LogEvent(this, logEntry));

		log.warn(message);
	}

	public void problem(String message, String type, Throwable ex) {

		LogEntry logEntry = null;

		try {

			logEntry = new LogEntry();
			logEntry.setLevel("PROBLEM");
			logEntry.setType(type);
			logEntry.setMessage(message);

			if (ex != null) {

				StringWriter writer = new StringWriter();
				ex.printStackTrace(new PrintWriter(writer));
				logEntry.setStacktrace(writer.getBuffer().toString());
			}
		} catch (Exception ex2) {

			log.error("Cannot save PROBLEM log event: " + message + ": " + ex2.getMessage(), ex2);
			if (logEntry == null) return;
		}

		this.fireLogEvent(new LogEvent(this, logEntry));

		if (ex != null) log.error(message, ex); else log.error(message);
	}

	/*
	 * Events
	 */

	public void addLogListener(LogListener logListener) {

		if (this.logListeners.contains(logListener)) return;
		this.logListeners.add(logListener);
	}

	public void removeLogListener(LogListener logListener) {

		this.logListeners.remove(logListener);
	}

	public void fireLogEvent(LogEvent logEvent) {

		for (LogListener logListener : this.logListeners) {

			logListener.onLogEvent(logEvent);
		}
	}
}
