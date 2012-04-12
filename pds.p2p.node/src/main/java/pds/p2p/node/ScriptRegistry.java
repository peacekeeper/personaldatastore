package pds.p2p.node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.Admin;
import pds.p2p.api.Orion;
import pds.p2p.api.Polaris;
import pds.p2p.api.Sirius;
import pds.p2p.api.Vega;
import pds.p2p.api.annotation.DanubeApi;

public class ScriptRegistry {

	private static Logger log = LoggerFactory.getLogger(ScriptRegistry.class);

	private File path;
	private Map<String, ScriptableObject> scopes;
	private Map<String, NativeObject> configuration;

	public ScriptRegistry(File path) {

		super();

		this.path = path;
		this.scopes = new HashMap<String, ScriptableObject> ();
		this.configuration = new HashMap<String, NativeObject> ();
	}

	public void init(Context context) {

		// load scripts

		log.info("Loading scripts from " + this.path.getAbsolutePath() + "..");

		File[] scriptFiles = this.path.listFiles(new FilenameFilter() {

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

		// done

		log.info("" + this.scopes.size() + " files loaded.");
	}

	public void shutdown(Context context) {

		// unload scripts

		log.info("Unloading scripts from " + this.path.getAbsolutePath() + "..");

		for (String scriptId : this.scopes.keySet()) {

			try {

				this.unloadScript(context, scriptId);
			} catch (Exception ex) {

				log.warn("Problem while unloading script '" + scriptId + "': " + ex.getMessage(), ex);
				continue;
			}
		}
	}

	public String[] getScriptIds() {

		return this.scopes.keySet().toArray(new String[this.scopes.size()]);
	}

	public synchronized void loadScript(Context context, String scriptId, String script) throws Exception {

		Object result;

		// prepare script scope

		ScriptableObject scope = context.initStandardObjects();

		log.debug("Adding JavaScript objects for script '" + scriptId + "'...");

		scope.defineProperty("log", Context.javaToJS(LoggerFactory.getLogger(scriptId), scope), ScriptableObject.DONTENUM);
		scope.defineProperty(Admin.class.getAnnotation(DanubeApi.class).name(), Context.javaToJS(DanubeApiServer.adminObject, scope), ScriptableObject.DONTENUM);
		scope.defineProperty(Orion.class.getAnnotation(DanubeApi.class).name(), Context.javaToJS(DanubeApiServer.orionObject, scope), ScriptableObject.DONTENUM);
		scope.defineProperty(Vega.class.getAnnotation(DanubeApi.class).name(), Context.javaToJS(DanubeApiServer.vegaObject, scope), ScriptableObject.DONTENUM);
		scope.defineProperty(Sirius.class.getAnnotation(DanubeApi.class).name(), Context.javaToJS(DanubeApiServer.siriusObject, scope), ScriptableObject.DONTENUM);
		scope.defineProperty(Polaris.class.getAnnotation(DanubeApi.class).name(), Context.javaToJS(DanubeApiServer.polarisObject, scope), ScriptableObject.DONTENUM);

		// load script

		log.debug("Loading script '" + scriptId + "'...");
		result = context.evaluateString(scope, script, scriptId, 1, null);
		log.debug("Loaded script '" + scriptId + "'. Result: " + Context.toString(result));

		// execute loadScript() function

		log.debug("Executing loadScript() for '" + scriptId + "'...");
		result = context.evaluateString(scope, "loadScript()", scriptId, 1, null);
		log.debug("Executed loadScript() for '" + scriptId + "'. Result: " + result.getClass().getName() + "  " + Context.toString(result));

		// add script and configuration

		this.scopes.put(scriptId, scope);
		this.configuration.put(scriptId, result instanceof NativeObject ? (NativeObject) result : null);
	}

	public synchronized void unloadScript(Context context, String scriptId) throws Exception {

		Object result;

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
		result = context.evaluateString(scope, "unloadScript()", scriptId, 1, null);
		log.debug("Executed unloadScript() for '" + scriptId + "'. Result: " + Context.toString(result));
	}

	public synchronized String runScript(Context context, String scriptId) throws Exception {

		Object result;

		// get script

		ScriptableObject scope = this.scopes.get(scriptId);

		if (scope == null) throw new IllegalArgumentException("Cannot run script '" + scriptId + "' (not found)");

		// execute runScript() function

		log.debug("Executing runScript() for '" + scriptId + "'...");
		result = context.evaluateString(scope, "runScript()", scriptId, 1, null);
		log.debug("Executed runScript() for '" + scriptId + "'. Result: " + Context.toString(result));
		
		// done
		
		return Context.toString(result);
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
