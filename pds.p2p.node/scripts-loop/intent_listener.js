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

		vega.subscribeTopic("intent_listener", "intent");
		vega.subscribeRay("intent_listener", "intent");
		connected = true;
	}

	if (connected && (! vega.connected())) {

		vega.unsubscribeTopic("intent_listener", "intent");
		vega.unsubscribeRay("intent_listener", "intent");
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

	vega.resetTopics("intent_listener");
	vega.resetRays("intent_listener");

	return { interval: 5 };
}

function runScript() {

	checkInitialized();
	if ((! loggedin) || (! connected)) return;

	while (vega.hasPackets("intent_listener")) {

		var rawpacket = vega.fetchPacket("intent_listener");
		var packet = eval("(" + rawpacket + ")");

		if (packet.ray == "intent") {

			processIntent(rawpacket, packet);
		} else {

			continue;
		}
	}
}

function unloadScript() {

	vega.resetTopics("intent_listener");
	vega.resetRays("intent_listener");
}
 
