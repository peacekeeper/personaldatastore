package org.pds.p2p.node.shell;

import java.net.URL;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.tools.shell.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.Orion;
import pds.p2p.api.Polaris;
import pds.p2p.api.Sirius;
import pds.p2p.api.Vega;
import pds.p2p.node.shell.Admin;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;

public class SetupRhinoObjects {

	private static final String SERVER_URL = "http://localhost:8080/";

	public static void setup() throws Throwable {

		final Admin adminObject = ProxyUtil.createProxy(SetupRhinoObjects.class.getClassLoader(), Admin.class, new JsonRpcHttpClient(new URL(SERVER_URL + "admin")));
		final Orion orionObject = ProxyUtil.createProxy(SetupRhinoObjects.class.getClassLoader(), Orion.class, new JsonRpcHttpClient(new URL(SERVER_URL + "orion")));
		final Vega vegaObject = ProxyUtil.createProxy(SetupRhinoObjects.class.getClassLoader(), Vega.class, new JsonRpcHttpClient(new URL(SERVER_URL + "vega")));
		final Sirius siriusObject = ProxyUtil.createProxy(SetupRhinoObjects.class.getClassLoader(), Sirius.class, new JsonRpcHttpClient(new URL(SERVER_URL + "sirius")));
		final Polaris polarisObject = ProxyUtil.createProxy(SetupRhinoObjects.class.getClassLoader(), Polaris.class, new JsonRpcHttpClient(new URL(SERVER_URL + "polaris")));
		
		Global scope = org.mozilla.javascript.tools.shell.Main.getGlobal();
		org.mozilla.javascript.tools.shell.Main.getGlobal().put("admin", scope, Context.javaToJS(adminObject, scope));
		org.mozilla.javascript.tools.shell.Main.getGlobal().put("orion", scope, Context.javaToJS(orionObject, scope));
		org.mozilla.javascript.tools.shell.Main.getGlobal().put("vega", scope, Context.javaToJS(vegaObject, scope));
		org.mozilla.javascript.tools.shell.Main.getGlobal().put("sirius", scope, Context.javaToJS(siriusObject, scope));
		org.mozilla.javascript.tools.shell.Main.getGlobal().put("polaris", scope, Context.javaToJS(polarisObject, scope));
	}
}
