package pds.p2p.node.shell;

import java.net.URL;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.shell.Global;
import org.mozilla.javascript.tools.shell.ShellContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.Orion;
import pds.p2p.api.Polaris;
import pds.p2p.api.Sirius;
import pds.p2p.api.Vega;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;

public class DanubeShell {

	private static Logger log = LoggerFactory.getLogger(DanubeShell.class);

	private static final String SERVER_URL = "http://localhost:8080/";

	public static void setup() throws Throwable {

		final Admin adminObject = ProxyUtil.createProxy(DanubeShell.class.getClassLoader(), Admin.class, new JsonRpcHttpClient(new URL(SERVER_URL + "admin")));
		final Orion orionObject = ProxyUtil.createProxy(DanubeShell.class.getClassLoader(), Orion.class, new JsonRpcHttpClient(new URL(SERVER_URL + "orion")));
		final Vega vegaObject = ProxyUtil.createProxy(DanubeShell.class.getClassLoader(), Vega.class, new JsonRpcHttpClient(new URL(SERVER_URL + "vega")));
		final Sirius siriusObject = ProxyUtil.createProxy(DanubeShell.class.getClassLoader(), Sirius.class, new JsonRpcHttpClient(new URL(SERVER_URL + "sirius")));
		final Polaris polarisObject = ProxyUtil.createProxy(DanubeShell.class.getClassLoader(), Polaris.class, new JsonRpcHttpClient(new URL(SERVER_URL + "polaris")));
		
		Global scope = org.mozilla.javascript.tools.shell.Main.getGlobal();
		org.mozilla.javascript.tools.shell.Main.getGlobal().put("admin", scope, Context.javaToJS(adminObject, scope));
		org.mozilla.javascript.tools.shell.Main.getGlobal().put("orion", scope, Context.javaToJS(orionObject, scope));
		org.mozilla.javascript.tools.shell.Main.getGlobal().put("vega", scope, Context.javaToJS(vegaObject, scope));
		org.mozilla.javascript.tools.shell.Main.getGlobal().put("sirius", scope, Context.javaToJS(siriusObject, scope));
		org.mozilla.javascript.tools.shell.Main.getGlobal().put("polaris", scope, Context.javaToJS(polarisObject, scope));
	}
	
	public static void main(String[] args) throws Throwable {

		// JSON-RPC

		final Admin adminObject = ProxyUtil.createProxy(DanubeShell.class.getClassLoader(), Admin.class, new JsonRpcHttpClient(new URL(SERVER_URL + "admin")));
		final Orion orionObject = ProxyUtil.createProxy(DanubeShell.class.getClassLoader(), Orion.class, new JsonRpcHttpClient(new URL(SERVER_URL + "orion")));
		final Vega vegaObject = ProxyUtil.createProxy(DanubeShell.class.getClassLoader(), Vega.class, new JsonRpcHttpClient(new URL(SERVER_URL + "vega")));
		final Sirius siriusObject = ProxyUtil.createProxy(DanubeShell.class.getClassLoader(), Sirius.class, new JsonRpcHttpClient(new URL(SERVER_URL + "sirius")));
		final Polaris polarisObject = ProxyUtil.createProxy(DanubeShell.class.getClassLoader(), Polaris.class, new JsonRpcHttpClient(new URL(SERVER_URL + "polaris")));

		// Rhino / JavaScript Shell

/*		Context context = Context.enter();
		Scriptable scope = context.initStandardObjects();
*/
/*		Context context = Context.enter();
		Global global = org.mozilla.javascript.tools.shell.Main.getGlobal();
		Global.putProperty(global, "admin", Context.javaToJS(adminObject, global));
		Global.putProperty(global, "orion", Context.javaToJS(orionObject, global));
		Global.putProperty(global, "vega", Context.javaToJS(vegaObject, global));
		Global.putProperty(global, "sirius", Context.javaToJS(siriusObject, global));
		Global.putProperty(global, "polaris", Context.javaToJS(polarisObject, global));*/

/*		final Context context = Context.enter();

		org.mozilla.javascript.tools.shell.Main.shellContextFactory = new ShellContextFactory() {

			@Override
			protected Context makeContext() {

				Scriptable scope = context.initStandardObjects();

				ScriptableObject.putProperty(scope, "admin", Context.javaToJS(adminObject, scope));
				ScriptableObject.putProperty(scope, "orion", Context.javaToJS(orionObject, scope));
				ScriptableObject.putProperty(scope, "vega", Context.javaToJS(vegaObject, scope));
				ScriptableObject.putProperty(scope, "sirius", Context.javaToJS(siriusObject, scope));
				ScriptableObject.putProperty(scope, "polaris", Context.javaToJS(polarisObject, scope));
				
				return context;
			}
		};*/
		
		org.mozilla.javascript.tools.shell.Main.global = new Global () {

			private static final long serialVersionUID = -4244134739915734959L;

			public void init(Context cx) {
		    	
		    	super.init(cx);

		    	log.info("Adding objects...");

				defineProperty("admin", Context.javaToJS(adminObject, this), ScriptableObject.DONTENUM);
				defineProperty("orion", Context.javaToJS(orionObject, this), ScriptableObject.DONTENUM);
				defineProperty("vega", Context.javaToJS(vegaObject, this), ScriptableObject.DONTENUM);
				defineProperty("sirius", Context.javaToJS(siriusObject, this), ScriptableObject.DONTENUM);
				defineProperty("polaris", Context.javaToJS(polarisObject, this), ScriptableObject.DONTENUM);
		    }
		};
/*		org.mozilla.javascript.tools.shell.Main.shellContextFactory.call(new ContextAction() {

			@Override
			public Object run(Context context) {


				return null;
			}
		});*/
		
		log.info("Running JavaScript shell...");
		org.mozilla.javascript.tools.shell.Main.main(args);
		log.info("JavaScript shell exited...");
	}
}
