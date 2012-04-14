package pds.p2p.node;

import java.io.File;

import org.mozilla.javascript.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptThread extends Thread {

	private static final long SLEEP_INTERVAL = 3 * 1000;

	private static Logger log = LoggerFactory.getLogger(ScriptThread.class);

	private boolean running;
	private ScriptRegistry scriptRegistry;

	public ScriptThread() {

		super();

		this.running = true;
		this.scriptRegistry = new ScriptRegistry(new File(".", "scripts-loop"));

		this.setDaemon(true);
	}

	public void stopRunning() throws Exception {

		this.running = false;
	}

	@Override
	public void run() {

		log.info("SCRIPT Thread " + Thread.currentThread().getId() + " starting.");
		
		// enter context
		
		Context context = Context.enter();
		context.setWrapFactory(MyWrapFactory.getInstance());

		// init script registry

		this.scriptRegistry.init(context);

		// run scripts

		log.info("Running scripts..");

		while (this.running) {

			for (String scriptId : this.scriptRegistry.getScriptIds()) {

				// run script

				try {

					this.scriptRegistry.runScript(context, scriptId);
				} catch (Exception ex) {

					log.warn("Problem while running script '" + scriptId + "': " + ex.getMessage(), ex);
					continue;
				}
			}

			// wait

			try {

				Thread.sleep(SLEEP_INTERVAL);
			} catch (InterruptedException ex) { }
		}

		// shut down script registry

		this.scriptRegistry.shutdown(context);

		// exit context

		Context.exit();

		// done
		
		log.info("SCRIPT Thread " + Thread.currentThread().getId() + " stopped.");
	}

	/*
	 * Public methods
	 */

	public synchronized void loadScript(String scriptId, String script) throws Exception {

		Context context = Context.enter();
		context.setWrapFactory(MyWrapFactory.getInstance());

		this.scriptRegistry.loadScript(context, scriptId, script);

		Context.exit();
	}

	public synchronized void unloadScript(String scriptId) throws Exception {

		Context context = Context.enter();
		context.setWrapFactory(MyWrapFactory.getInstance());

		this.scriptRegistry.unloadScript(context, scriptId);

		Context.exit();
	}

	public synchronized String[] getScriptIds() {

		return this.scriptRegistry.getScriptIds();
	}
}
