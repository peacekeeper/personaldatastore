//*** request sample ***
//mass:werk, N.Landsteiner 2007

//path to server script goes here
var xdiRemotePath = '/DanubeApiShell';

var xdiTerm;

var xdiHelp = [
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

function xdiTermOpen() {

	if ((!xdiTerm) || (xdiTerm.closed)) {

		xdiTerm = new Terminal({
			x: 220,
			y: 70,
			cols:160,
			rows:48,
			termDiv: 'xdiTermDiv',
			bgColor: '#23452e',
			greeting: xdiHelp.join('\n'),
			handler: xdiTermHandler,
			exitHandler: null
		});

		xdiTerm.open();
	}
}

function xdiTermHandler() {

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
		this.write(xdiHelp);
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
			command = "polaris." + cmd.substring(1) + "(\"" + argv[1] + "\",\"STATEMENTS\")";				
			// send line to server-backend
			this.send({
				url: xdiRemotePath,
				method: 'post',
				callback: xdiSocketCallback,
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

function xdiSocketCallback() {

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

$(document).ready(function(){

	xdiTermOpen();

	$("#xdiTermDiv").click(function() {
		xdiTerm.focus();
	});
});
