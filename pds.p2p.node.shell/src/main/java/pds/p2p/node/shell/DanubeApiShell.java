package pds.p2p.node.shell;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;
import org.mozilla.javascript.tools.shell.Global;
import org.mozilla.javascript.tools.shell.ShellContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.Admin;
import pds.p2p.api.Orion;
import pds.p2p.api.Polaris;
import pds.p2p.api.Sirius;
import pds.p2p.api.Vega;
import pds.p2p.api.annotation.DanubeApi;
import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.api.node.client.MyWrapFactory;

public class DanubeApiShell {

	private static Logger log = LoggerFactory.getLogger(DanubeApiShell.class);

	public static void main(String[] args) throws Throwable {

		init(args);
		shell(args);
		shutdown();
	}

	private static void init(String[] args) throws Throwable {

		log.info("init()");

		DanubeApiClient.init();
	}

	private static void shutdown() {

		log.info("shutdown()");

		DanubeApiClient.shutdown();
	}

	private static void shell(String[] args) throws Throwable {

		log.info("shell()");

		// Rhino / JavaScript Shell

		final WrapFactory wrapFactory = new MyWrapFactory();

		org.mozilla.javascript.tools.shell.Main.shellContextFactory = new ShellContextFactory() {

			@Override
			protected void onContextCreated(Context cx) {

				super.onContextCreated(cx);

				log.info("Initializing context...");

				cx.setWrapFactory(wrapFactory);
			}
		};

		org.mozilla.javascript.tools.shell.Main.global = new Global () {

			private static final long serialVersionUID = -4244134739915734959L;

			public void init(Context cx) {

				super.init(cx);

				log.info("Adding JavaScript objects...");

				this.defineProperty(Admin.class.getAnnotation(DanubeApi.class).name(), Context.javaToJS(DanubeApiClient.adminObject, this), ScriptableObject.DONTENUM);
				this.defineProperty(Orion.class.getAnnotation(DanubeApi.class).name(), Context.javaToJS(DanubeApiClient.orionObject, this), ScriptableObject.DONTENUM);
				this.defineProperty(Vega.class.getAnnotation(DanubeApi.class).name(), Context.javaToJS(DanubeApiClient.vegaObject, this), ScriptableObject.DONTENUM);
				this.defineProperty(Sirius.class.getAnnotation(DanubeApi.class).name(), Context.javaToJS(DanubeApiClient.siriusObject, this), ScriptableObject.DONTENUM);
				this.defineProperty(Polaris.class.getAnnotation(DanubeApi.class).name(), Context.javaToJS(DanubeApiClient.polarisObject, this), ScriptableObject.DONTENUM);
			}
		};

		log.info("Running JavaScript shell...");
		org.mozilla.javascript.tools.shell.Main.main(args);
		log.info("JavaScript shell exited...");
	}
}
