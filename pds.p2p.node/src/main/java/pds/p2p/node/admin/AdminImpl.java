package pds.p2p.node.admin;

import java.lang.reflect.Method;
import java.util.Date;

import pds.p2p.api.Admin;
import pds.p2p.api.annotation.DanubeApi;
import pds.p2p.node.DanubeApiServer;
import pds.p2p.node.ScriptThread;

public class AdminImpl implements Admin {

	private Date startTime;
	private ScriptThread scriptThread;

	public AdminImpl(Date startTime, ScriptThread scriptThread) {

		this.startTime = startTime;
		this.scriptThread = scriptThread;
	}

	public void init() throws Exception {

	}

	public void shutdown() {

	}

	public String hello() {

		return "Hello World";
	}

	public String uptime() {

		return "" + (new Date().getTime() - this.startTime.getTime());
	}

	public String helpApis() throws Exception {

		StringBuffer buffer = new StringBuffer();

		// list API interfaces

		for (Class<?> apiInterface : DanubeApiServer.apiInterfaces()) {

			// print help

			DanubeApi apiInterfaceAnnotation = apiInterface.getAnnotation(DanubeApi.class);

			buffer.append(apiInterfaceAnnotation.name() + " - " + apiInterfaceAnnotation.description() + "\n");
		}

		return buffer.toString();
	}

	public String helpApi(String apiName) throws Exception {

		// find API interface

		Class<?> apiInterface = DanubeApiServer.apiInterface(apiName);

		// print help

		StringBuffer buffer = new StringBuffer();

		for (Method method : apiInterface.getMethods()) {

			buffer.append(apiName + "." + method.getName() + "(");

			Class<?> parameterTypes[] = method.getParameterTypes();
			for (int i=0; i<parameterTypes.length; i++) {

				if (i > 0) buffer.append(",");
				buffer.append(parameterTypes[i].getSimpleName().toLowerCase());
			}

			buffer.append(")\n");
		}

		return buffer.toString();
	}

	@Override
	public synchronized void loadScript(String scriptId, String script) throws Exception {

		this.scriptThread.loadScript(scriptId, script);
	}

	@Override
	public synchronized void unloadScript(String scriptId) throws Exception {

		this.scriptThread.unloadScript(scriptId);
	}

	@Override
	public synchronized String[] getScriptIds() {

		return this.scriptThread.getScriptIds();
	}
}
