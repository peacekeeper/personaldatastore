// *** request sample ***
// mass:werk, N.Landsteiner 2007

// path to server script goes here
var packetRemotePath = '/Packet';

var packetTerm;

function packetTermOpen() {

	if ((!packetTerm) || (packetTerm.closed)) {
	
		packetTerm = new Terminal({
				x: 800,
				y: 70,
				cols:160,
				rows:48,
				termDiv: 'packetTermDiv',
				bgColor: '#452e23',
				greeting: "",
				handler: null,
				exitHandler: null
			});

		packetTerm.open();
	}
}

function packetSocketCallback() {

	var response = this.socket;

	if (response.success) {

		this.write(response.responseText);
	} else {

		var s='Request failed: ' + response.status + ' ' + response.statusText;
		if (response.errno) s +=  '\n' + response.errstring;
		this.write(s);
	}

	packetListen();
}

function packetListen() {

	// send line to server-backend
	packetTerm.send({
		url: packetRemotePath,
		method: 'get',
		callback: packetSocketCallback,
		data: {
			"client": "webshell"
		}
	});
};

$(document).ready(function() {

	packetTermOpen();
	packetListen();

	$("#packetTermDiv").click(function() {
		packetTerm.focus();
	});
});
