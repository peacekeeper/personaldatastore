// *** request sample ***
// mass:werk, N.Landsteiner 2007

// path to server script goes here
var remotePath = '/Shell';

var term;

var help = [
	'%+r **** Project Danube WebShell **** %-r',
	' ',
	'* type "clear" to clear the screen.',
	'* type "api" to list APIs.',
	'* type "api ..." to get help for an API.',
	'* type "help" for this page',
	'* type "exit" to quit.',
	' '
];

function termOpen() {
	if ((!term) || (term.closed)) {
		term = new Terminal(
			{
				x: 220,
				y: 70,
				cols:160,
				rows:48,
				termDiv: 'termDiv',
				bgColor: '#232e45',
				greeting: help.join('\n'),
				handler: termHandler,
				exitHandler: termExitHandler
			}
		);
		term.open();
		
		// dimm UI text
		var mainPane = (document.getElementById)?
			document.getElementById('mainPane') : document.all.mainPane;
		if (mainPane) mainPane.className = 'lh15 dimmed';
	}
}

function termExitHandler() {
	// reset the UI
	var mainPane = (document.getElementById)?
		document.getElementById('mainPane') : document.all.mainPane;
	if (mainPane) mainPane.className = 'lh15';
}

function pasteCommand(text) {
	// insert given text into the command line and execute
	var termRef = TermGlobals.activeTerm;
	if ((!termRef) || (termRef.closed)) {
		alert('Please open the terminal first.');
		return;
	}
	if ((TermGlobals.keylock) || (termRef.lock)) return;
	termRef.cursorOff();
	termRef._clearLine();
	for (var i=0; i<text.length; i++) {
		TermGlobals.keyHandler({which: text.charCodeAt(i), _remapped:true});
	}
	TermGlobals.keyHandler({which: termKey.CR, _remapped:true});
}

function termHandler() {
	this.newLine();
	
	this.lineBuffer = this.lineBuffer.replace(/^\s+/, '');
	var argv = this.lineBuffer.split(/\s+/);
	var cmd = argv[0];
	
	switch (cmd) {

		case 'clear':
			this.clear();
			break;

		case 'help':
			this.clear();
			this.write(help);
			break;

		case 'exit':
			this.close();
			return;

		case 'api':
			var cmd;
			if (argv[1]) {
				cmd = "admin.helpApi(\"" + argv[1] + "\")";				
			} else {
				cmd = "admin.helpApis()";
			}
			// send line to server-backend
			this.send(
				{
					url: remotePath,
					method: 'post',
					callback: socketCallback,
					data: {
						command: cmd
					}
				}
			);
			// leave without prompt (this will come from the callback)
			return;
			break;

		default:
			// no local command
			if (this.lineBuffer != '') {
				// send (unparsed) line to server-backend
				this.send(
					{
						url: remotePath,
						method: 'post',
						callback: socketCallback,
						data: {
							command: this.lineBuffer
						}
					}
				);
				// leave without prompt (this will come from the callback)
				return;
			}
	}
	this.prompt();
}

function socketCallback() {
	var response=this.socket;
	if (response.success) {
		var func=null;
		try {
			func=eval(response.responseText);
		}
		catch (e) {
		}
		if (typeof func=='function') {
			try {
				func.apply(this);
			}
			catch(e) {
				this.write('An error occured within the imported function: '+e);
			}
		}
		else {
			this.write(response.responseText);
		}
	}
	else {
		var s='Request failed: ' + response.status + ' ' + response.statusText;
		if (response.errno) s +=  '\n' + response.errstring;
		this.write(s);
	}
	this.prompt();
}
