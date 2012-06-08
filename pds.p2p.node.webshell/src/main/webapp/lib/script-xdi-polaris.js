//*** request sample ***
//mass:werk, N.Landsteiner 2007

//path to server script goes here
var xdiPolarisRemotePath = '/DanubeApiShell';

var xdiPolarisTerm;

var xdiPolarisHelp = [
            '%+r **** Personal XDI Shell **** %-r',
            ' ',
            '* commands:',
            '* "$get <xdi>"',
            '* "$add <xdi>"',
            '* "$mod <xdi>"',
            '* "$del <xdi>"',
            '* "clear" to clear the screen.',
            '* "help" for this page.',
            '* "exit" to quit.',
            ' '
            ];

function xdiPolarisTermOpen() {

	if ((! xdiPolarisTerm) || (xdiPolarisTerm.closed)) {

		xdiPolarisTerm = new Terminal({
/*			x: 220,
			y: 70,*/
			cols: 50,
			rows: 40,
			termDiv: 'xdiPolarisTermDiv',
			bgColor: '#1c2e3f',
			greeting: xdiPolarisHelp.join('\n'),
			handler: xdiPolarisTermHandler,
			exitHandler: null
		});

		xdiPolarisTerm.open();
	}
}

function xdiPolarisTermHandler() {

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
		this.write(xdiPolarisHelp);
		break;

	case 'exit':
		this.close();
		return;

	case '$get':
	case '$add':
	case '$mod':
	case '$del':
		if (! argv[1]) {
			this.write("Need 1 argument");
		} else {
			command = "polaris." + cmd.substring(1) + "(\"" + argv[1] + "\",null,\"STATEMENTS\")";				
			// send line to server-backend
			this.send({
				url: xdiPolarisRemotePath,
				method: 'post',
				callback: xdiPolarisSocketCallback,
				data: {
					command: command
				}
			});
			// leave without prompt (this will come from the callback)
			return;
		}
		break;
	}
	this.prompt();
}

function xdiPolarisSocketCallback() {

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

	xdiPolarisTermOpen();

	$("#xdiPolarisTermDiv").click(function() {
		xdiPolarisTerm.focus();
	});
});
