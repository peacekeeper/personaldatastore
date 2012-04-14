package pds.p2p.api;

import pds.p2p.api.annotation.DanubeApi;

@DanubeApi(name="admin", description="Admin API")
public interface Admin {

	public void init() throws Exception;
	public void shutdown();

	public String hello();
	public String uptime();
	public String helpApis() throws Exception;
	public String helpApi(String apiName) throws Exception;
	public void loadScript(String scriptId, String script) throws Exception;
	public void unloadScript(String scriptId) throws Exception;
	public String[] getScriptIds();
}
