package pds.p2p.node;

import java.util.Date;

import org.mortbay.jetty.Server;

public class AdminImpl implements Admin {

	private Date startTime;
	private Server server;

	public AdminImpl(Date startTime, Server server) {

		this.startTime = startTime;
		this.server = server;
	}

	public String hello() {

		return "Hello World";
	}

	public String uptime() {

		return "" + (new Date().getTime() - this.startTime.getTime());
	}

	public void stop() throws Exception {

		this.server.stop();
	}
}
