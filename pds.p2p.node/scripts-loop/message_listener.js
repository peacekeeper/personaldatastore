//-- private functions

var loggedin = false;
var connected = false;

function checkInitialized() {

	if ((! loggedin) && orion.loggedin()) {

		loggedin = true;
	}	

	if (loggedin && (! orion.loggedin())) {

		loggedin = false;
	}

	if ((! connected) && vega.connected()) {

		vega.subscribeRay("message_listener", "message");
		connected = true;
	}

	if (connected && (! vega.connected())) {

		connected = false;
	}
}

function processMessage(rawpacket, packet) {

	var content = packet.content;
	var message = eval("(" + content + ")");

	polaris.add(orion.inumber() + "+message!" + message.messageid + "/!/(data:," + encodeURIComponent(rawpacket) + ")", null, null);
}

//-- main functions

function loadScript() {

	vega.resetRays("message_listener");

	return { interval: 5 };
}

function runScript() {

	checkInitialized();
	if ((! loggedin) || (! connected)) return;

	while (vega.hasPackets("message_listener")) {

		var rawpacket = vega.fetchPacket("message_listener");
		var packet = eval("(" + rawpacket + ")");

		if (packet.ray == "message") {

			processMessage(rawpacket, packet);
		} else {

			continue;
		}
	}
}

function unloadScript() {

	vega.resetRays("message_listener");
}
 
