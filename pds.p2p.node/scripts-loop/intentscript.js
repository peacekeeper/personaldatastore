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

		vega.subscribeTopic("intentscript", "intent");
		vega.subscribeRay("intentscript", "intent");
		connected = true;
	}

	if (connected && (! vega.connected())) {

		vega.unsubscribeTopic("intentscript", "intent");
		vega.unsubscribeRay("intentscript", "intent");
		connected = false;
	}
}

function processIntent(rawpacket, packet) {

	var content = packet.content;
	var intent = eval("(" + content + ")");

	polaris.add(orion.inumber() + "+intent!" + intent.id + "/!/(data:," + encodeURIComponent(rawpacket) + ")", null);
}

//-- main functions

function loadScript() {

	vega.resetTopics("intentscript");
	vega.resetRays("intentscript");

	return { interval: 5 };
}

function runScript() {

	checkInitialized();
	if ((! loggedin) || (! connected)) return;

	while (vega.hasPackets("intentscript")) {

		var rawpacket = vega.fetchPacket("intentscript");
		var packet = eval("(" + rawpacket + ")");

		if (packet.ray == "intent") {

			processIntent(rawpacket, packet);
		} else {

			continue;
		}
	}
}

function unloadScript() {

	vega.resetTopics("intentscript");
	vega.resetRays("intentscript");
}
 
