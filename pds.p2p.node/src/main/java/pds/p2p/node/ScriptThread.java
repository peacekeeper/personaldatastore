package pds.p2p.node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.Admin;
import pds.p2p.api.Orion;
import pds.p2p.api.Polaris;
import pds.p2p.api.Sirius;
import pds.p2p.api.Vega;
import pds.p2p.api.annotation.DanubeApi;

public class ScriptThread extends Thread {

	private static Logger log = LoggerFactory.getLogger(ScriptThread.class);

	private boolean running;
	private Map<String, ScriptableObject> scopes;
	private WrapFactory wrapFactory;

	public ScriptThread() {

		super();

		this.running = true;
		this.scopes = new HashMap<String, ScriptableObject> ();
		this.wrapFactory = new MyWrapFactory();
	}

	public void stopRunning() {
		
		this.running = false;
	}
	
	@Override
	public void run() {

		// prepare Rhino context

		Context context = Context.enter();
		context.setWrapFactory(this.wrapFactory);

		// load scripts

		log.info("Loading scripts..");

		File[] scriptFiles = new File(".", "scripts/").listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				return name.endsWith(".js");
			}
		});

		for (File scriptFile : scriptFiles) {

			String scriptId = scriptFile.getName();
			String script;

			try {

				script = loadFile(scriptFile);
				this.loadScript(context, scriptId, script);
			} catch (Exception ex) {

				log.warn("Problem while loading script '" + scriptId + "': " + ex.getMessage(), ex);
				continue;
			}
		}

		// run scripts

		log.info("Running scripts..");

		while (this.running) {

			for (String scriptId : this.scopes.keySet()) {

				// run script

				try {

					this.runScript(context, scriptId);
				} catch (Exception ex) {

					log.warn("Problem while running script '" + scriptId + "': " + ex.getMessage(), ex);
					continue;
				}
			}
			
			// wait
			
			try {

				Thread.sleep(2000);
			} catch (InterruptedException ex) { }
		}

		// unload scripts

		log.info("Unloading scripts..");

		for (String scriptId : this.scopes.keySet()) {

			try {

				this.unloadScript(context, scriptId);
			} catch (Exception ex) {

				log.warn("Problem while unloading script '" + scriptId + "': " + ex.getMessage(), ex);
				continue;
			}
		}

		// done

		Context.exit();
	}

	/*
	 * Public methods
	 */

	public synchronized void loadScript(String scriptId, String script) throws Exception {

		Context context = Context.enter();
		context.setWrapFactory(this.wrapFactory);
		
		this.loadScript(context, scriptId, script);
		
		Context.exit();
	}

	public synchronized void unloadScript(String scriptId) throws Exception {

		Context context = Context.enter();
		context.setWrapFactory(this.wrapFactory);

		this.unloadScript(context, scriptId);
		
		Context.exit();
	}

	public synchronized String[] getScriptIds() {

		return this.scopes.keySet().toArray(new String[this.scopes.size()]);
	}

	/*
	 * Private methods
	 */

	public synchronized void loadScript(Context context, String scriptId, String script) throws Exception {

		String result;

		// prepare script scope

		ScriptableObject scope = context.initStandardObjects();

		log.debug("Adding JavaScript objects...");

		scope.defineProperty(Admin.class.getAnnotation(DanubeApi.class).name(), Context.javaToJS(DanubeApiServer.adminObject, scope), ScriptableObject.DONTENUM);
		scope.defineProperty(Orion.class.getAnnotation(DanubeApi.class).name(), Context.javaToJS(DanubeApiServer.orionObject, scope), ScriptableObject.DONTENUM);
		scope.defineProperty(Vega.class.getAnnotation(DanubeApi.class).name(), Context.javaToJS(DanubeApiServer.vegaObject, scope), ScriptableObject.DONTENUM);
		scope.defineProperty(Sirius.class.getAnnotation(DanubeApi.class).name(), Context.javaToJS(DanubeApiServer.siriusObject, scope), ScriptableObject.DONTENUM);
		scope.defineProperty(Polaris.class.getAnnotation(DanubeApi.class).name(), Context.javaToJS(DanubeApiServer.polarisObject, scope), ScriptableObject.DONTENUM);

		// load script

		log.debug("Loading script '" + scriptId + "'...");
		result = Context.toString(context.evaluateString(scope, script, scriptId, 1, null));
		log.debug("Loaded script '" + scriptId + "'. Result: " + result);

		// execute loadScript() function

		log.debug("Executing loadScript() for '" + scriptId + "'...");
		result = Context.toString(context.evaluateString(scope, "loadScript()", scriptId, 1, null));
		log.debug("Executed loadScript() for '" + scriptId + "'. Result: " + result);

		// add script

		this.scopes.put(scriptId, scope);
	}

	public synchronized void unloadScript(Context context, String scriptId) throws Exception {

		String result;

		// get script

		ScriptableObject scope = this.scopes.get(scriptId);

		if (scope == null) {

			log.warn("Cannot unload script '" + scriptId + "' (not found)");
			return;
		}

		// remove script

		this.scopes.remove(scriptId);

		// execute unloadScript() function

		log.debug("Executing unloadScript() for '" + scriptId + "'...");
		result = Context.toString(context.evaluateString(scope, "unloadScript()", scriptId, 1, null));
		log.debug("Executed unloadScript() for '" + scriptId + "'. Result: " + result);
	}

	private synchronized void runScript(Context context, String scriptId) throws Exception {

		String result;

		// get script

		ScriptableObject scope = this.scopes.get(scriptId);

		if (scope == null) {

			log.warn("Cannot run script '" + scriptId + "' (not found)");
			return;
		}

		// execute runScript() function

		log.debug("Executing runScript() for '" + scriptId + "'...");
		result = Context.toString(context.evaluateString(scope, "runScript()", scriptId, 1, null));
		log.debug("Executed runScript() for '" + scriptId + "'. Result: " + result);
	}

	/*
	 * Helper methods
	 */

	private static final String loadFile(File file) throws IOException {

		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(file));

		String line;

		while ((line = reader.readLine()) != null) buffer.append(line + "\n");

		reader.close();
		return buffer.toString();
	}
}
