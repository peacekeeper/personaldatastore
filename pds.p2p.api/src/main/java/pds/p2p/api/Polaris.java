package pds.p2p.api;

import pds.p2p.api.annotation.DanubeApi;


@DanubeApi(name="polaris", description="Personal XDI API")
public interface Polaris {

	public void init() throws Exception;
	public void shutdown();

	public String add(String xdi, String xdiUrl, String format) throws Exception;
	public String get(String xdi, String xdiUrl, String format) throws Exception;
	public String mod(String xdi, String xdiUrl, String format) throws Exception;
	public String del(String xdi, String xdiUrl, String format) throws Exception;
	public String[] getLiterals(String xdi, String xdiUrl) throws Exception;
	public String getLiteral(String xdi, String xdiUrl) throws Exception;
	public String[] getRelations(String xdi, String xdiUrl) throws Exception;
	public String getRelation(String xdi, String xdiUrl) throws Exception;
	public String execute(String message, String xdiUrl, String format) throws Exception;
}
