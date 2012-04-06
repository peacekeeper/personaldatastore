package pds.p2p.api;

import pds.p2p.api.annotation.DanubeApi;


@DanubeApi(name="polaris", description="Personal XDI API")
public interface Polaris {

	public void init() throws Exception;
	public void shutdown();

	public String add(String xdi, String format) throws Exception;
	public String get(String xdi, String format) throws Exception;
	public String mod(String xdi, String format) throws Exception;
	public String del(String xdi, String format) throws Exception;
	public String[] getLiterals(String xdi) throws Exception;
	public String getLiteral(String xdi) throws Exception;
	public String[] getRelations(String xdi) throws Exception;
	public String getRelation(String xdi) throws Exception;
	public String execute(String message, String format) throws Exception;
}
