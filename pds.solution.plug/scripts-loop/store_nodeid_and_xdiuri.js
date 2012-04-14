//-- main functions

function loadScript() {

	return { interval: 5 };
}

var loggedin_and_connected = false;

function runScript() {

	if ((! loggedin_and_connected) && orion.loggedin() && vega.connected()) {

		loggedin_and_connected = true;

		var inumber = orion.inumber();
		var nodeId = vega.nodeId();
		var xdiUri = orion.xdiUri();

		sirius.del(inumber + "$nodeid", null);
		sirius.del(inumber + "$xdiuri", null);
		sirius.add(inumber + "$nodeid/!/(data:," + encodeURIComponent(nodeId) + ")", null);
		sirius.add(inumber + "$xdiuri/!/(data:," + encodeURIComponent(xdiUri) + ")", null);
	}

	if (loggedin_and_connected && ((! orion.loggedin()) || (! vega.connected()))) {

		loggedin_and_connected = false;
	}
}

function unloadScript() {

}
