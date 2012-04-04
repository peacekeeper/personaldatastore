package pds.p2p.node.shell;

import pds.p2p.api.annotations.ApiInterface;

@ApiInterface(name="admin", description="Admin API")
public interface Admin {

	public String hello();
	public String uptime();
	public String helpApis() throws Exception;
	public String helpApi(String apiName) throws Exception;
	public void stop() throws Exception;
}
