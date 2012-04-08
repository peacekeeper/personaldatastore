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

		vega.subscribeTopic("prfpscript", "prfp");
		vega.subscribeRay("prfpscript", "prfp");
		connected = true;
	}

	if (connected && (! vega.connected())) {

		vega.unsubscribeTopic("prfpscript", "prfp");
		vega.unsubscribeRay("prfpscript", "prfp");
		connected = false;
	}
}

function processPrfp(rawpacket, packet) {

	var content = packet.content;
	var prfp = eval(content);

	polaris.add(orion.inumber() + "+prfp!" + prfp.id + "/!/(data:," + encodeURIComponent(rawpacket) + ")", null);
}

//-- main functions

function loadScript() {

}

function runScript() {

	checkInitialized();
	if ((! loggedin) || (! connected)) return;

	while (vega.hasPackets("prfpscript")) {

		var rawpacket = vega.fetchPacket("prfpscript");
		var packet = eval(rawpacket);

		if (packet.ray == "prfp") {

			processPrfp(rawpacket, packet);
		} else {

			continue;
		}
	}
}

function unloadScript() {

	vega.unsubscribeRay("testscript", "r");
}
