// *** request sample ***
// mass:werk, N.Landsteiner 2007

// path to server script goes here
var shellRemotePath = '/DanubeApiShell';

var shellTerm;

var shellHelp = [
	'%+r **** Project Danube WebShell **** %-r',
	' ',
	'* commands:',
	'* "api" to list APIs.',
	'* "api ..." to get help for an API.',
	'* "clear" to clear the screen.',
	'* "help" for this page.',
	'* "exit" to quit.',
	' '
];

function shellTermOpen() {

	if ((!shellTerm) || (shellTerm.closed)) {

		shellTerm = new Terminal({
			x: 220,
			y: 70,
			cols:100,
			rows:40,
			termDiv: 'shellTermDiv',
			bgColor: '#28425a',
			greeting: shellHelp.join('\n'),
			handler: shellTermHandler,
			exitHandler: null
		});

		shellTerm.open();
	}
}

function shellTermHandler() {

	this.newLine();
	
	this.lineBuffer = this.lineBuffer.replace(/^\s+/, '');
	var argv = this.lineBuffer.split(/\s+/);
	var cmd = argv[0];
	
	var command;

	switch (cmd) {

		case 'clear':
			this.clear();
			break;

		case 'help':
			this.clear();
			this.write(shellHelp);
			break;

		case 'exit':
			this.close();
			return;

		case 'api':
			if (argv[1]) {
				command = "admin.helpApi(\"" + argv[1] + "\")";				
			} else {
				command = "admin.helpApis()";
			}
			// fall-through

		default:
			if (! command) command = this.lineBuffer;
			if (command) {
				// send line to server-backend
				this.send({
					url: shellRemotePath,
					method: 'post',
					callback: shellSocketCallback,
					data: {
						"command": command
					}
				});
				// leave without prompt (this will come from the callback)
				return;
			}
			break;
	}

	this.prompt();
}

function shellSocketCallback() {

	var response = this.socket;

	if (response.success) {

		this.write(response.responseText);
	} else {

		var s = 'Request failed: ' + response.status + ' ' + response.statusText;
		if (response.errno) s +=  '\n' + response.errstring;
		this.write(s);
	}

	this.prompt();
}

$(document).ready(function() {

	shellTermOpen();

	$("#shellTermDiv").click(function() {
		shellTerm.focus();
	});
});
