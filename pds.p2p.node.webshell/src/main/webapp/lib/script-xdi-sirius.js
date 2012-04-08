//*** request sample ***
//mass:werk, N.Landsteiner 2007

//path to server script goes here
var xdiSiriusRemotePath = '/DanubeApiShell';

var xdiSiriusTerm;

var xdiSiriusHelp = [
            '%+r **** Distributed XDI Shell **** %-r',
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

function xdiSiriusTermOpen() {

	if ((! xdiSiriusTerm) || (xdiSiriusTerm.closed)) {

		xdiSiriusTerm = new Terminal({
/*			x: 520,
			y: 70,
			cols: 80,
			rows: 48,*/
			termDiv: 'xdiSiriusTermDiv',
			bgColor: '#312f61',
			greeting: xdiSiriusHelp.join('\n'),
			handler: xdiSiriusTermHandler,
			exitHandler: null
		});

		xdiSiriusTerm.open();
	}
}

function xdiSiriusTermHandler() {

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
		this.write(xdiSiriusHelp);
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
			command = "sirius." + cmd.substring(1) + "(\"" + argv[1] + "\",\"STATEMENTS\")";				
			// send line to server-backend
			this.send({
				url: xdiSiriusRemotePath,
				method: 'post',
				callback: xdiSiriusSocketCallback,
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

function xdiSiriusSocketCallback() {

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

	xdiSiriusTermOpen();

	$("#xdiSiriusTermDiv").click(function() {
		xdiSiriusTerm.focus();
	});
});
