package pds.web.logger;

import java.io.Serializable;
import java.util.Date;

public class LogEntry implements Serializable {

	private static final long serialVersionUID = -5331544256502104099L;

	private Date timestamp;
	private String level;
	private String type;
	private String applicationInstanceId;
	private String message;
	private String stacktrace;

	public LogEntry() { 

		this.timestamp = new Date();
	}

	public Date getTimestamp() {

		return (this.timestamp);
	}

	void setTimestamp(Date timestamp) {

		this.timestamp = timestamp;
	}

	public String getLevel() {

		return this.level;
	}

	public void setLevel(String level) {

		this.level = level;
	}

	public String getType() {

		return this.type;
	}

	public void setType(String type) {

		this.type = type;
	}

	public String getApplicationInstanceId() {

		return this.applicationInstanceId;
	}

	public void setApplicationInstanceId(String applicationInstanceId) {

		this.applicationInstanceId = applicationInstanceId;
	}

	public String getMessage() {

		return this.message;
	}

	public void setMessage(String message) {

		this.message = message;
	}

	public String getStacktrace() {

		return this.stacktrace;
	}

	public void setStacktrace(String stacktrace) {

		this.stacktrace = stacktrace;
	}
}
