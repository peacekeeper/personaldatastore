package pds.p2p.api;

import pds.p2p.api.annotation.DanubeApi;


@DanubeApi(name="orion", description="Node and User Identity API")
public interface Orion {

	public void init() throws Exception;
	public void shutdown();

	public void login(String iname, String password) throws Exception;
	public void logout() throws Exception;
	public String loggedin() throws Exception;
	public String iname() throws Exception;
	public String inumber() throws Exception;
	public String resolve(String iname) throws Exception;
	public String sign(String str) throws Exception;
	public String verify(String str, String signature, String inumber) throws Exception;
	public String encrypt(String str, String inumber) throws Exception;
	public String decrypt(String str) throws Exception;
	public String symGenerateKey() throws Exception;
	public String symEncrypt(String str, String key) throws Exception;
	public String symDecrypt(String str, String key) throws Exception;
	public String guid() throws Exception;
}
