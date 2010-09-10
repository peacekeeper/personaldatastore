package pds.endpoint.pubsubhubbub;

import java.util.ArrayList;

public class Web extends Thread {
	
	static Web webserver;
	static int port = 8080;
	String contextPath = "/push";
	
	static ArrayList<String> approvedActions = new ArrayList<String>();

	public static ArrayList<String> getApprovedActions() {
		return approvedActions;
	}

	public void addAction(String hubmode, String hubtopic, String hubverify) {
		String action=hubmode + ":" + hubtopic + ":" + hubverify;
		approvedActions.add(action);
	}

	public void run() {
	}

}