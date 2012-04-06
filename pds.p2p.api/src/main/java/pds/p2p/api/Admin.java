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
	public void stop() throws Exception;
}
