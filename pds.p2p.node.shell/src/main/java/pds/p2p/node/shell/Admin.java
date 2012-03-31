package pds.p2p.node.shell;

public interface Admin {

	public String hello();
	public String uptime();
	public void stop() throws Exception;
}
