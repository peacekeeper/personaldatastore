package pds.p2p.node;

import java.io.File;

import org.mozilla.javascript.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoopScriptThread extends Thread {

	private static Logger log = LoggerFactory.getLogger(LoopScriptThread.class);

	private boolean running;
	private ScriptRegistry scriptRegistry;

	public LoopScriptThread() {

		super();

		this.running = true;
		this.scriptRegistry = new ScriptRegistry(new File(".", "scripts-loop"));
	}

	public void stopRunning() {
		
		this.running = false;
	}
	
	@Override
	public void run() {

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

				Thread.sleep(2000);
			} catch (InterruptedException ex) { }
		}

		// shut down script registry

		this.scriptRegistry.shutdown(context);
		
		// done

		Context.exit();
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
