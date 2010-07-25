package pds.web.logger.events;

import java.util.EventObject;

import pds.web.logger.LogEntry;

public class LogEvent extends EventObject {

	private static final long serialVersionUID = 6967018544221137210L;

	private LogEntry logEntry;

	public LogEvent(Object source, LogEntry logEntry) {

		super(source);

		this.logEntry = logEntry;
	}
	
	public LogEntry getLogEntry() {

		return this.logEntry;
	}
}
