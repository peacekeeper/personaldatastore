package pds.p2p.api;

import pds.p2p.api.annotation.DanubeApi;


@DanubeApi(name="vega", description="P2P Networking API")
public interface Vega {

	public void init() throws Exception;
	public void shutdown();

	public String connect(String localPort, String remoteHost, String remotePort, String parameters) throws Exception;
	public void disconnect() throws Exception;
	public String connected() throws Exception;

	public String nodeId() throws Exception;
	public String localHost() throws Exception;
	public String localPort() throws Exception;
	public String publicHost() throws Exception;
	public String publicPort() throws Exception;
	public String remoteHost() throws Exception;
	public String remotePort() throws Exception;
	public String parameters() throws Exception;
	public String lookupRandom() throws Exception;
	public String[] lookupNeighbors(String num) throws Exception;
	public void send(String nodeId, String ray, String content, String flags, String extension) throws Exception;
	public void subscribeTopic(String client, String topic) throws Exception;
	public void unsubscribeTopic(String client, String topic) throws Exception;
	public String[] topics(String client) throws Exception;
	public void resetTopics(String client) throws Exception;
	public void multicast(String topic, String ray, String content, String flags, String extension) throws Exception;
	public void anycast(String topic, String ray, String content, String flags, String extension) throws Exception;
	public String get(String key) throws Exception;
	public void put(String key, String value) throws Exception;

	public void multiPut(String key, String value) throws Exception;
	public String[] multiGet(String key) throws Exception;
	public String multiGetIndex(String key, String index) throws Exception;
	public String multiGetCount(String key) throws Exception;
	public String multiGetRandom(String key) throws Exception;
	public void multiDelete(String key, String value) throws Exception;

	public void subscribeRay(String client, String ray) throws Exception;
	public void unsubscribeRay(String client, String ray) throws Exception;
	public String[] rays(String client) throws Exception;
	public void resetRays(String client) throws Exception;
	public String hasPackets(String client) throws Exception;
	public String fetchPacket(String client) throws Exception;
}
