package pds.p2p.api;

public interface Polaris {

	public void init() throws Exception;
	public void shutdown();

	public String add(String xdi, String format) throws Exception;
	public String get(String xdi, String format) throws Exception;
	public String mod(String xdi, String format) throws Exception;
	public String set(String xdi, String format) throws Exception;
	public String del(String xdi, String format) throws Exception;
	public String[] getLiterals(String xdi) throws Exception;
	public String getLiteral(String xdi) throws Exception;
	public String[] getReferences(String xdi) throws Exception;
	public String getReference(String xdi) throws Exception;
	public String execute(String message, String format) throws Exception;
}
