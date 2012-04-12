//-- main functions

function loadScript() {

	return { interval: 5 };
}

var loggedin_and_connected = false;

function runScript() {

	if ((! loggedin_and_connected) && orion.loggedin() && vega.connected()) {

		loggedin_and_connected = true;

		var nodeId = vega.nodeId();
		var inumber = orion.inumber();

		sirius.del(inumber + "/$nodeid/($)", null);
		sirius.add(inumber + "/$nodeid/" + "$" + nodeId, null);
	}
	
	if (loggedin_and_connected && ((! orion.loggedin()) || (! vega.connected()))) {

		loggedin_and_connected = false;
	}
}

function unloadScript() {

}
