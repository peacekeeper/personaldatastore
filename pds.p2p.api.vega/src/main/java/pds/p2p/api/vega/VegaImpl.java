package pds.p2p.api.vega;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Queue;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmlpull.v1.XmlPullParserFactory;

import pds.p2p.api.Orion;
import pds.p2p.api.Vega;
import pds.p2p.api.vega.comm.VegaMessage;
import pds.p2p.api.vega.comm.VegaPastContent;
import pds.p2p.api.vega.comm.VegaScribeContent;
import pds.p2p.api.vega.util.BlockingContinuation;
import pds.p2p.api.vega.util.HashCash;
import pds.p2p.api.vega.util.Nonce;
import pds.p2p.api.vega.util.NonceUtil;
import pds.p2p.api.vega.util.XriUtil;
import rice.environment.Environment;
import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.NodeHandleSet;
import rice.p2p.commonapi.RouteMessage;
import rice.p2p.past.Past;
import rice.p2p.past.PastContent;
import rice.p2p.past.PastImpl;
import rice.p2p.past.PastPolicy;
import rice.p2p.past.PastPolicy.DefaultPastPolicy;
import rice.p2p.scribe.Scribe;
import rice.p2p.scribe.ScribeContent;
import rice.p2p.scribe.ScribeImpl;
import rice.p2p.scribe.ScribeMultiClient;
import rice.p2p.scribe.Topic;
import rice.persistence.Cache;
import rice.persistence.LRUCache;
import rice.persistence.MemoryStorage;
import rice.persistence.PersistentStorage;
import rice.persistence.Storage;
import rice.persistence.StorageManager;
import rice.persistence.StorageManagerImpl;

public class VegaImpl implements Vega, Application, ScribeMultiClient {

	private static final String NAT_APP_NAME = "versionvega";
	private static final String STORAGE_DIRECTORY = "./storage";
	private static final long STORAGE_SIZE = 4 * 1024 * 1024;
	private static final int CACHE_SIZE = 2 * 1024 * 1024;
	private static final String INSTANCE_ENDPOINT = "vegaendpoint";
	private static final String INSTANCE_SCRIBE = "vegascribe";
	private static final String INSTANCE_PAST = "vegapast";

	private static final int DEFAULT_VEGA_PORT = 15020;

	private static Log log = LogFactory.getLog(VegaImpl.class);

	private static Random random = new Random();

	private Orion orion;

	private String localHost;
	private String localPort;
	private String publicHost;
	private String publicPort;
	private String remoteHost;
	private String remotePort;
	private String parameters;
	private Environment environment;
	private rice.pastry.PastryNode pastryNode;
	private rice.pastry.commonapi.PastryIdFactory pastryIdFactory;
	private Endpoint endpoint;
	private Scribe scribe;
	private Past past;
	private Storage pastStorage;
	private Cache pastCache;

	private Map<String, List<String>> topics = new HashMap<String, List<String>> ();
	private Map<String, List<String>> rays = new HashMap<String, List<String>> ();
	private Map<String, Queue<String>> packets = new HashMap<String, Queue<String>> ();

	VegaImpl(Orion orion) {

		this.orion = orion;
	}

	public void init() throws Exception {

		System.setProperty(XmlPullParserFactory.PROPERTY_NAME, "org.xmlpull.mxp1.MXParserFactory");
	}

	public void shutdown() {

	}

	public String connect(String localPort, String remoteHost, String remotePort, String parameters) throws Exception {

		log.debug("connect(" + localPort + "," + remoteHost + "," + remotePort + ",<parameters>)");

		try {

			// disconnect first if necessary

			if ("1".equals(this.connected())) this.disconnect();

			// if the remote host is an XRI, resolve it

			if (remoteHost != null && (remoteHost.startsWith("@") || remoteHost.startsWith("="))) {

				remoteHost = XriUtil.discoverConnectUri(remoteHost);
				if (remoteHost == null) throw new IOException("Cannot discover network from " + remoteHost);	
			}

			// if the remote host is a vega:// URI, parse it

			if (remoteHost != null && remoteHost.startsWith("vega://")) {

				remoteHost = remoteHost.substring("vega://".length());

				if (remoteHost.contains(":")) {

					remotePort = remoteHost.split(":")[1];
					remoteHost = remoteHost.split(":")[0];
				}
			}

			// if no local or remote port is given, use default

			if (localPort == null) localPort = Integer.toString(DEFAULT_VEGA_PORT);
			if (remotePort == null) remotePort = Integer.toString(DEFAULT_VEGA_PORT);

			// loads pastry settings

			this.environment = new Environment(new String[] { "vega" }, null);

			// set custom parameters

			this.environment.getParameters().setString("nat_app_name", NAT_APP_NAME);

			if (parameters != null) {

				Properties properties = new Properties();
				properties.load(new ByteArrayInputStream(parameters.getBytes("UTF-8")));

				for (Map.Entry<Object, Object> property : properties.entrySet()) {

					log.debug("Setting parameter " + (String) property.getKey() + " --> " + (String) property.getValue());
					this.environment.getParameters().setString((String) property.getKey(), (String) property.getValue());
				}
			}

			// build the boot address

			InetAddress remoteAddr;
			InetSocketAddress remoteSockAddr;

			if (remoteHost != null && remotePort != null && ! remoteHost.equals("localhost") && ! remoteHost.startsWith("127.") && Integer.valueOf(remotePort).intValue() > 0) {

				remoteAddr = InetAddress.getByName(remoteHost);
				remoteSockAddr = new InetSocketAddress(remoteAddr, Integer.valueOf(remotePort).intValue());
				log.debug("Boot address is " + remoteSockAddr.toString());
			} else {

				remoteAddr = null;
				remoteSockAddr = null;
				log.debug("No boot address.");
			}

			// figure out local address

			InetAddress localAddr = InetAddress.getLocalHost();
			InetSocketAddress localSockAddr = new InetSocketAddress(localAddr, Integer.valueOf(localPort).intValue());
			log.debug("Local address is " + localSockAddr.toString());

			// figure out public address and port

			InetAddress publicAddr;
			InetSocketAddress publicSockAddr;

			if (remoteHost != null && remotePort != null && ! remoteHost.equals("localhost") && ! remoteHost.startsWith("127.") && Integer.valueOf(remotePort).intValue() > 0) {

				DatagramSocket clientSocket = new DatagramSocket(null);
				clientSocket.setReuseAddress(true);
				clientSocket.setSoTimeout(10000);
				clientSocket.bind(new InetSocketAddress(localAddr, Integer.valueOf(localPort).intValue()));

				byte[] sendBuffer;
				byte[] receiveBuffer = new byte[256];
				String sendString;
				String receiveString;

				sendString = ":)";
				sendBuffer = sendString.getBytes();

				DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName(remoteHost), Integer.valueOf(remotePort).intValue() - 1);
				log.debug("Sending probe packet...");
				clientSocket.send(sendPacket);

				DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
				log.debug("Receiving probe packet...");
				clientSocket.receive(receivePacket);
				receiveString = new String(receiveBuffer).substring(0, receivePacket.getLength());

				String publicHost = receiveString.split(" ")[0];
				String publicPort = receiveString.split(" ")[1];
				publicAddr = InetAddress.getByName(publicHost);
				publicSockAddr = new InetSocketAddress(publicAddr, Integer.valueOf(publicPort).intValue());
				log.debug("Public address is " + publicSockAddr.toString());

				clientSocket.close();
			} else {

				publicAddr = null;
				publicSockAddr = null;
				log.debug("No public address.");
			}

			if (publicAddr != null && localAddr.equals(publicAddr)) {

				publicAddr = null;
				publicSockAddr = null;
				log.debug("Local address is public address.");
			}

			// construct pastry ID factory

			this.pastryIdFactory = new rice.pastry.commonapi.PastryIdFactory(this.environment);

			// remember connection parameters

			this.localHost = localSockAddr.getAddress().getHostAddress();
			this.localPort = Integer.toString(localSockAddr.getPort());
			this.publicHost = publicSockAddr == null ? null : publicSockAddr.getAddress().getHostAddress();
			this.publicPort = publicSockAddr == null ? null : Integer.toString(publicSockAddr.getPort());
			this.remoteHost = remoteSockAddr == null ? null : remoteSockAddr.getAddress().getHostAddress();
			this.remotePort = remoteSockAddr == null ? null : Integer.toString(remoteSockAddr.getPort());
			this.parameters = parameters;

			// create node

			log.debug("Instantiating node...");

			this.createNode(publicSockAddr);

			// create endpoint

			this.createEndpoint();

			// create SCRIBE

			this.createScribe();

			// create PAST

			this.createPast();

			// register the endpoint

			this.endpoint.register();

			// boot the node

			this.pastryNode.addObserver(new Observer() {

				public void update(Observable object, Object arg) {

					log.debug("Observed " + arg + " to " + object);

					if (arg instanceof RuntimeException) {

						throw (RuntimeException) arg;
					} else if (arg instanceof Exception ) {

						throw new RuntimeException((Exception) arg);
					}
				}
			});

			if (remoteSockAddr == null) {

				log.debug("Booting node into new network...");
			} else {

				log.debug("Booting node into " + remoteSockAddr.toString());
			}

			this.pastryNode.boot(Collections.singleton(remoteSockAddr));

			// the node may require sending several messages to fully boot into the network

			int counter = 0;

			while(! this.pastryNode.isReady()) {

				// abort if can't join

				if (this.pastryNode.joinFailed()) throw new IOException("Could not join the network: " + this.pastryNode.joinFailedReason()); 
				if (counter > 180) throw new IOException("Could not join the network (timeout)."); 

				// delay so we don't busy-wait

				synchronized(this.pastryNode) {

					log.debug("Waiting for node to boot...");
					this.pastryNode.wait(500);
					counter++;
				}
			}

			log.debug("Booting complete! Our node ID: " + this.pastryNode.getNodeId().toStringFull() + ". Our node handle: " + this.pastryNode.getLocalNodeHandle().toString() + ". Our node: " + this.pastryNode.toString());
		} catch (Exception ex) {

			this.disconnect();

			throw ex;
		}

		return "1";
	}

	protected void createNode(InetSocketAddress publicSockAddr) throws IOException {

		rice.pastry.NodeIdFactory nodeIdFactory = new rice.pastry.standard.RandomNodeIdFactory(this.environment);
		rice.pastry.socket.SocketPastryNodeFactory nodeFactory;

		/*if (this.isInternetRoutablePrefix(localAddr) || (remoteAddr != null && this.isInternetRoutablePrefix(remoteAddr))) {
		if (false) {

			log.debug("Constructing InternetPastryNodeFactory...");
			nodeFactory = new rice.pastry.socket.internet.InternetPastryNodeFactory(nodeIdFactory, localAddr, Integer.valueOf(localPort).intValue(), this.environment, null, Collections.singletonList(remoteSockAddr), null);
		} else*/ {

			log.debug("Constructing SocketPastryNodeFactory...");
			nodeFactory = new rice.pastry.socket.SocketPastryNodeFactory(nodeIdFactory, Integer.valueOf(localPort).intValue(), this.environment);
		}

		this.pastryNode = nodeFactory.newNode(nodeIdFactory.generateNodeId(), publicSockAddr);

		log.debug("Local node: " + this.pastryNode.getClass().getName());
		log.debug("Local port is " + localPort);
	}

	protected void createEndpoint() {

		this.endpoint = this.pastryNode.buildEndpoint(this, INSTANCE_ENDPOINT);
	}

	protected void createScribe() {

		this.scribe = new ScribeImpl(this.pastryNode, INSTANCE_SCRIBE);
	}

	protected void createPast() throws IOException {

		this.pastStorage = new PersistentStorage(this.pastryIdFactory, STORAGE_DIRECTORY, STORAGE_SIZE, this.environment);
		//		Storage storage = new MemoryStorage(this.idFactory);
		this.pastCache = new LRUCache(new MemoryStorage(this.pastryIdFactory), CACHE_SIZE, this.pastryNode.getEnvironment());
		//		Cache cache = new EmptyCache(this.idFactory);
		StorageManager storageManager = new StorageManagerImpl(this.pastryIdFactory, this.pastStorage, this.pastCache);
		PastPolicy pastPolicy = new DefaultPastPolicy();

		//this.past = new GCPastImpl(this.pastryNode, storageManager, 3, INSTANCE_PAST, pastPolicy, 1 * 60 * 1000);
		this.past = new PastImpl(this.pastryNode, storageManager, 3, INSTANCE_PAST, pastPolicy);
	}

	public void disconnect() throws Exception {

		log.debug("disconnect()");

		try {

			if (this.pastStorage != null) this.pastStorage.flush(null);
			if (this.pastCache != null) this.pastCache.flush(null);

			if (this.pastryNode != null) this.pastryNode.destroy();
			if (this.environment != null) this.environment.destroy();
		} catch (Exception ex) {

		} finally {

			this.environment = null;
			this.pastryNode = null;

			this.endpoint = null;

			this.localHost = null;
			this.localPort = null;
			this.publicHost = null;
			this.publicPort = null;
			this.remoteHost = null;
			this.remotePort = null;
			this.parameters = null;
		}
	}

	public String connected() throws Exception {

		log.debug("connected()");

		if (this.environment != null && this.pastryNode != null && this.endpoint != null) {

			return "1";
		} else {

			return null;
		}
	}

	/*
	 * Actions
	 */

	public String nodeId() throws Exception {

		log.debug("nodeId()");

		if (this.endpoint == null) return null;

		return this.endpoint.getId().toStringFull();
	}

	public String localHost() throws Exception {

		log.debug("localHost()");

		return this.localHost;
	}

	public String localPort() throws Exception {

		log.debug("localPort()");

		return this.localPort;
	}

	public String publicHost() throws Exception {

		log.debug("publicHost()");

		return this.publicHost;
	}

	public String publicPort() throws Exception {

		log.debug("publicPort()");

		return this.publicPort;
	}

	public String remoteHost() throws Exception {

		log.debug("remoteHost()");

		return this.remoteHost;
	}

	public String remotePort() throws Exception {

		log.debug("remotePort()");

		return this.remotePort;
	}

	public String parameters() throws Exception {

		log.debug("parameters()");

		return this.parameters;
	}

	public String lookupRandom() throws Exception {

		log.debug("lookupRandom()");

		if (! "1".equals(this.connected())) throw new RuntimeException("Not connected.");

		Id nodeId = this.pastryIdFactory.buildRandomId(this.environment.getRandomSource());
		return nodeId.toStringFull();
	}

	public String[] lookupNeighbors(String num) throws Exception {

		log.debug("lookupNeighbors(" + num + ")");

		if (! "1".equals(this.connected())) throw new RuntimeException("Not connected.");

		NodeHandleSet nodeHandleSet = this.endpoint.neighborSet(Integer.valueOf(num).intValue());
		String[] nodeIds = new String[nodeHandleSet.size()];
		for (int i=0; i<nodeHandleSet.size(); i++) nodeIds[i] = nodeHandleSet.getHandle(i).getId().toStringFull();
		return nodeIds;
	}

	public void send(String nodeId, String ray, String content, String flags, String extension) throws Exception {

		log.debug("send(" + nodeId + "," + ray + "," + content + "," + flags + "," + extension + ")");

		if (! "1".equals(this.connected())) throw new RuntimeException("Not connected.");
		if (! "1".equals(this.orion.loggedin())) throw new RuntimeException("Not signed in.");

		Id id = this.pastryIdFactory.buildIdFromToString(nodeId);
		String nonce = new Nonce().toString();
		String signature = this.orion.sign(id.toStringFull() + " " + ray + " " + nonce + " " + content);
		String hashcash = new HashCash(new Date(), id.toStringFull()).toString();
		Message msg = new VegaMessage(ray, this.orion.iname(), this.orion.inumber(), nonce, content, signature, hashcash, flags, extension);

		this.endpoint.route(id, msg, null);
	}

	public synchronized void subscribeTopic(String client, String topic) throws Exception {

		log.debug("subscribeTopic(" + client + "," + topic + ")");

		if (! "1".equals(this.connected())) throw new RuntimeException("Not connected.");

		// are we subscribed yet?

		boolean subscribed = false;

		for (List<String> topics : this.topics.values()) {

			if (topics.contains(topic)) {

				subscribed = true;
				break;
			}
		}

		// register the topic with the client

		List<String> clientTopics = this.topics.get(client);
		if (clientTopics == null) {

			clientTopics = new ArrayList<String> ();
			this.topics.put(client, clientTopics);
		}
		clientTopics.add(topic);

		// if no one subscribed yet, subscribe

		if (! subscribed) {

			Topic t = new Topic(this.pastryIdFactory, topic);

			this.scribe.subscribe(t, this, null, null);
		}
	}

	public synchronized void unsubscribeTopic(String client, String topic) throws Exception {

		log.debug("unsubscribeTopic(" + client + "," + topic + ")");

		if (! "1".equals(this.connected())) throw new RuntimeException("Not connected.");

		// unregister the topic with the client

		List<String> clientTopics = this.topics.get(client);
		if (clientTopics == null) return;
		clientTopics.remove(topic);

		// are we still subscribed?

		boolean subscribed = false;

		for (List<String> topics : this.topics.values()) {

			if (topics.contains(topic)) {

				subscribed = true;
				break;
			}
		}

		// if no one subscribed anymore, unsubscribe

		if (! subscribed) {

			Topic t = new Topic(this.pastryIdFactory, topic);

			this.scribe.unsubscribe(t, this);
		}
	}

	public synchronized String[] topics(String client) throws Exception {

		log.debug("topics(" + client + ")");

		List<String> clientTopics = this.topics.get(client);
		if (clientTopics == null) return new String[0];
		return clientTopics.toArray(new String[clientTopics.size()]);
	}

	public synchronized void resetTopics(String client) throws Exception {

		log.debug("resetTopics(" + client + ")");

		List<String> clientTopics = this.topics.get(client);
		if (clientTopics == null) return;
		this.topics.remove(client);

		if ("1".equals(this.connected())) {

			for (String clientTopic : clientTopics) {

				// are we still subscribed?

				boolean subscribed = false;

				for (List<String> topics : this.topics.values()) {

					if (topics.contains(clientTopic)) {

						subscribed = true;
						break;
					}
				}

				// if no one subscribed anymore, unsubscribe

				if (! subscribed) {

					Topic t = new Topic(this.pastryIdFactory, clientTopic);

					this.scribe.unsubscribe(t, this);
				}
			}
		}
	}

	public void multicast(String topic, String ray, String content, String flags, String extension) throws Exception {

		log.debug("multicast(" + topic + "," + ray + "," + content + "," + flags + "," + extension + ")");

		if (! "1".equals(this.connected())) throw new RuntimeException("Not connected.");
		if (! "1".equals(this.orion.loggedin())) throw new RuntimeException("Not signed in.");
		if ((topic.startsWith("=") || topic.startsWith("@")) && 
				(! topic.equals(this.orion.iname() + ray)) && 
				(! topic.equals(this.orion.inumber() + ray)) && 
				(! topic.equals(this.orion.iname())) && 
				(! topic.equals(this.orion.inumber()))) throw new RuntimeException("Cannot send to private topic.");

		Topic t = new Topic(this.pastryIdFactory, topic);
		String nonce = new Nonce().toString();
		String signature = this.orion.sign(t.getId().toStringFull() + " " + ray + " " + nonce + " " + content);
		String hashcash = new HashCash(new Date(), t.getId().toStringFull()).toString();
		ScribeContent scribeContent = new VegaScribeContent(topic, ray, this.orion.iname(), this.orion.inumber(), nonce, content, signature, hashcash, flags, extension);

		this.scribe.publish(t, scribeContent);
	}

	public void anycast(String topic, String ray, String content, String flags, String extension) throws Exception {

		log.debug("anycast(" + topic + "," + ray + "," + content + "," + flags + "," + extension + ")");

		if (! "1".equals(this.connected())) throw new RuntimeException("Not connected.");
		if (! "1".equals(this.orion.loggedin())) throw new RuntimeException("Not signed in.");
		if ((topic.startsWith("=") || topic.startsWith("@")) && 
				(! topic.equals(this.orion.iname() + ray)) && 
				(! topic.equals(this.orion.inumber() + ray)) && 
				(! topic.equals(this.orion.iname())) && 
				(! topic.equals(this.orion.inumber()))) throw new RuntimeException("Cannot send to private topic.");

		Topic t = new Topic(this.pastryIdFactory, topic);
		String nonce = new Nonce().toString();
		String signature = this.orion.sign(t.getId().toStringFull() + " " + ray + " " + nonce + " " + content);
		String hashcash = new HashCash(new Date(), t.getId().toStringFull()).toString();
		ScribeContent scribeContent = new VegaScribeContent(topic, ray, this.orion.iname(), this.orion.inumber(), nonce, content, signature, hashcash, flags, extension);

		this.scribe.anycast(t, scribeContent);
	}

	public String get(String key) throws Exception {

		log.debug("get(" + key + ")");

		if (! "1".equals(this.connected())) throw new RuntimeException("Not connected.");

		String content = this.internalGet(key);
		if (content == null) return null;

		return content;
	}

	public void put(String key, String value) throws Exception {

		log.debug("put(" + key + "," + value + ")");

		if (! "1".equals(this.connected())) throw new RuntimeException("Not connected.");
		if (! "1".equals(this.orion.loggedin())) throw new RuntimeException("Not signed in.");

		this.internalPut(key, value);
	}

	public void multiPut(String key, String value) throws Exception {

		log.debug("multiPut(" + key + "," + value + ")");

		if (! "1".equals(this.connected())) throw new RuntimeException("Not connected.");
		if (! "1".equals(this.orion.loggedin())) throw new RuntimeException("Not signed in.");

		this.internalMultiPut(key, value);
	}

	public String[] multiGet(String key) throws Exception {

		log.debug("multiGet(" + key + ")");

		if (! "1".equals(this.connected())) throw new RuntimeException("Not connected.");

		return this.internalMultiGet(key);
	}

	public String multiGetIndex(String key, String index) throws Exception {

		log.debug("multiGetIndex(" + key + "," + index + ")");

		if (! "1".equals(this.connected())) throw new RuntimeException("Not connected.");

		return this.internalMultiGetIndex(key, index);
	}

	public String multiGetCount(String key) throws Exception {

		log.debug("multiGetCount(" + key + ")");

		if (! "1".equals(this.connected())) throw new RuntimeException("Not connected.");

		return this.internalMultiGetCount(key);
	}

	public String multiGetRandom(String key) throws Exception {

		log.debug("multiGetRandom(" + key + ")");

		if (! "1".equals(this.connected())) throw new RuntimeException("Not connected.");

		return this.internalMultiGetRandom(key);
	}

	public void multiDelete(String key, String value) throws Exception {

		log.debug("multiDelete(" + key + "," + value + ")");

		if (! "1".equals(this.connected())) throw new RuntimeException("Not connected.");
		if (! "1".equals(this.orion.loggedin())) throw new RuntimeException("Not signed in.");

		this.internalMultiDelete(key, value);
	}

	public synchronized void subscribeRay(String client, String ray) throws Exception {

		log.debug("subscribeRay(" + client + "," + ray + ")");

		List<String> clientRays = this.rays.get(client);
		if (clientRays == null) {

			clientRays = new ArrayList<String> ();
			this.rays.put(client, clientRays);
		}
		clientRays.add(ray);
	}

	public synchronized void unsubscribeRay(String client, String ray) throws Exception {

		log.debug("unsubscribeRay(" + client + "," + ray + ")");

		List<String> clientRays = this.rays.get(client);
		if (clientRays == null) return;
		clientRays.remove(ray);
	}

	public synchronized String[] rays(String client) throws Exception {

		log.debug("rays(" + client + ")");

		List<String> clientRays = this.rays.get(client);
		if (clientRays == null) return new String[0];
		return clientRays.toArray(new String[clientRays.size()]);
	}

	public synchronized void resetRays(String client) throws Exception {

		log.debug("resetRays(" + client + ")");

		this.rays.remove(client);
		this.packets.remove(client);
	}

	public synchronized String hasPackets(String client) throws Exception {

		log.debug("hasPackets(" + client + ")");

		Queue<String> packets = this.packets.get(client);
		if (packets == null) return null;

		if (! packets.isEmpty()) {

			return "1";
		} else {

			return null;
		}
	}

	public synchronized String fetchPacket(String client) throws Exception {

		log.debug("fetchPacket(" + client + ")");

		Queue<String> packets = this.packets.get(client);
		if (packets == null) return null;

		if (! packets.isEmpty()) {

			return packets.poll();
		} else {
			return null;
		}
	}

	/*
	 * Internal actions
	 */

	private Boolean[] internalPut(final String key, final String value) throws Exception {

		log.debug("internalPut(" + key + "," + value + ")");

		Id id = this.pastryIdFactory.buildId(key);
		Long version = Long.valueOf(System.currentTimeMillis());
		String signature = this.orion.sign(key + value);
		String hashcash = new HashCash(new Date(), key).toString();
		PastContent pastContent = new VegaPastContent(id, version, this.orion.iname(), this.orion.inumber(), key, value, signature, hashcash);

		BlockingContinuation<Boolean[], Exception> continuation = new BlockingContinuation<Boolean[], Exception> ();

		try {

			this.past.insert(pastContent, continuation);
			continuation.block();
		} catch (Exception ex) {

			continuation.receiveException(ex);
		}

		if (continuation.hasException()) throw continuation.getException();
		if (! continuation.hasResult()) return null;

		return continuation.getResult();
	}

	private String internalGet(final String key) throws Exception {

		log.debug("internalGet(" + key + ")");

		Id id = this.pastryIdFactory.buildId(key);

		BlockingContinuation<PastContent, Exception> continuation = new BlockingContinuation<PastContent, Exception> ();

		try {

			this.past.lookup(id, continuation);
			continuation.block();
		} catch (Exception ex) {

			continuation.receiveException(ex);
		}

		if (continuation.hasException()) throw continuation.getException();
		if (! continuation.hasResult()) return null;

		VegaPastContent result = (VegaPastContent) continuation.getResult();
		if (! result.getKey().equals(key)) throw new RuntimeException("Incorrect key.");

		return result.getValue();
	}

	private void internalMultiPut(final String key, final String value) throws Exception {

		log.debug("internalMultiPut(" + key + "," + value + ")");

		String newindex = hash(value);

		this.internalPut(key + "___", "+" + newindex);
		this.internalPut(key + "___" + newindex, value);
		return;
	}

	private String[] internalMultiGet(final String key) throws Exception {

		log.debug("internalMultiGet(" + key + ")");

		String indexlist;

		try {

			indexlist = this.internalGet(key + "___");
			if (indexlist.trim().equals("")) indexlist = null;
		} catch (Exception ex) {

			indexlist = null;
		}

		String[] indices = indexlist == null ? new String[0] : indexlist.trim().split(" ");

		List<String> contents = new ArrayList<String> (indices.length);

		for (int i=0; i<indices.length; i++) {

			String content = this.internalGet(key + "___" + indices[i]);
			if (content == null) continue;

			contents.add(content);
		}

		return contents.toArray(new String[contents.size()]);
	}

	private String internalMultiGetIndex(final String key, final String index) throws Exception {

		log.debug("internalMultiGetIndex(" + key + "," + index + ")");

		String indexlist;

		try {

			indexlist = this.internalGet(key + "___");
			if (indexlist.trim().equals("")) indexlist = null;
		} catch (Exception ex) {

			indexlist = null;
		}

		String[] indices = indexlist == null ? new String[0] : indexlist.trim().split(" ");

		return this.internalGet(key + "___" + indices[Integer.valueOf(index).intValue()]);
	}

	private String internalMultiGetCount(final String key) throws Exception {

		log.debug("internalMultiGetCount(" + key + ")");

		String indexlist;

		try {

			indexlist = this.internalGet(key + "___");
			if (indexlist.trim().equals("")) indexlist = null;
		} catch (Exception ex) {

			indexlist = null;
		}

		String[] indices = indexlist == null ? new String[0] : indexlist.trim().split(" ");

		return Integer.toString(indices.length);
	}

	private String internalMultiGetRandom(final String key) throws Exception {

		log.debug("internalMultiGetRandom(" + key + ")");

		String indexlist;

		try {

			indexlist = this.internalGet(key + "___");
			if (indexlist.trim().equals("")) indexlist = null;
		} catch (Exception ex) {

			indexlist = null;
		}

		String[] indices = indexlist == null ? new String[0] : indexlist.trim().split(" ");

		int index = Math.abs(random.nextInt() % indices.length);

		String content = this.internalGet(key + "___" + indices[index]);
		if (content == null) return null;

		return content;
	}

	private void internalMultiDelete(final String key, final String value) throws Exception {

		log.debug("internalMultiDelete(" + key + "," + value + ")");

		if (value == null) {

			this.internalPut(key + "___", "");
			return;
		}

		String index = hash(value);

		this.internalPut(key + "___", "-" + index);
	}

	/*
	 * Events
	 */

	public void update(NodeHandle handle, boolean joined) {

		log.debug("--> update(" + handle.getId().toStringFull() + "," + joined + ")");
	}

	public boolean forward(RouteMessage message) {

		log.debug("--> forward(" + message + ")");

		return true;
	}

	public void childAdded(Topic topic, NodeHandle child) {

		log.debug("--> childAdded(" + topic.getId().toStringFull() + "," + child.getId().toStringFull() + ")");
	}

	public void childRemoved(Topic topic, NodeHandle child) {

		log.debug("--> childRemoved(" + topic.getId().toStringFull() + "," + child.getId().toStringFull() + ")");
	}

	public void subscribeSuccess(Collection<Topic> topics) {

		for (Topic topic : topics) log.debug("--> subscribeSuccess(" + topic.getId().toStringFull() + ")");
	}

	public void subscribeFailed(Collection<Topic> topics) {

		for (Topic topic : topics) log.debug("--> subscribeFailed(" + topic.getId().toStringFull() + ")");
	}

	@Deprecated
	public void subscribeFailed(Topic topic) {

		log.debug("--> subscribeFailed(" + topic.getId().toStringFull() + ")");
	}

	public void deliver(Id id, Message message) {

		if (! (message instanceof VegaMessage)) return;
		VegaMessage vegaMessage = (VegaMessage) message;

		log.debug("--> deliver(" + id.toStringFull() + "," + message + ")");
		log.debug("--> deliver: ray=" + vegaMessage.getRay() + " iname=" + vegaMessage.getIname() + " inumber=" + vegaMessage.getInumber() + " nonce=" + vegaMessage.getNonce() + " content=" + vegaMessage.getContent() + " signature=" + vegaMessage.getSignature() + " hashcash=" + vegaMessage.getHashcash() + " flags=" + vegaMessage.getFlags() + " extension=" + vegaMessage.getExtension());

		try {

			// check general message properties

			boolean dataOk = 
					vegaMessage.getRay() != null && 
					vegaMessage.getIname() != null && 
					vegaMessage.getInumber() != null && 
					vegaMessage.getNonce() != null &&
					vegaMessage.getSignature() != null && 
					vegaMessage.getHashcash() != null;

			boolean nonceOk = NonceUtil.checkNonce(vegaMessage.getNonce());

			boolean signatureOk = "1".equals(this.orion.verify(id.toStringFull() + " " + vegaMessage.getRay() + " " + vegaMessage.getNonce() + " " + vegaMessage.getContent(), vegaMessage.getSignature(), vegaMessage.getInumber()));

			HashCash hashcash = HashCash.fromString(vegaMessage.getHashcash());
			boolean hashcashOk = hashcash.getTo().equals(id.toStringFull()) && hashcash.isValid();

			log.debug("--> deliver: dataOk=" + dataOk + " nonceOk=" + nonceOk + " signatureOk=" + signatureOk + " hashcashOk=" + hashcashOk);
			if (! dataOk || ! nonceOk || ! signatureOk || ! hashcashOk) return;

			// queue JSON packet

			this.addPacket(
					vegaMessage.getRay(),
					null,
					"{\n" + 
							" \"type\":\"unicast\",\n" +
							" \"id\":" + escapeJSON(id.toStringFull()) + ",\n" +
							" \"ray\":" + escapeJSON(vegaMessage.getRay()) + ",\n" +
							" \"iname\":" + escapeJSON(vegaMessage.getIname()) + ",\n" +
							" \"inumber\":" + escapeJSON(vegaMessage.getInumber()) + ",\n" +
							" \"nonce\":" + escapeJSON(vegaMessage.getNonce()) + ",\n" +
							" \"content\":" + escapeJSON(vegaMessage.getContent()) + ",\n" + 
							" \"signature\":" + escapeJSON(vegaMessage.getSignature()) + ",\n" + 
							" \"hashcash\":" + escapeJSON(vegaMessage.getHashcash()) + ",\n" + 
							" \"flags\":" + escapeJSON(vegaMessage.getFlags()) + ",\n" + 
							" \"extension\":" + escapeJSON(vegaMessage.getExtension()) + "\n" + 
					"}");
		} catch (Exception ex) {

			log.error("--> deliver: " + ex.getMessage(), ex);
			throw new RuntimeException(ex);
		}
	}

	public void deliver(Topic topic, ScribeContent scribeContent) {

		if (! (scribeContent instanceof VegaScribeContent)) return;
		VegaScribeContent vegaScribeContent = (VegaScribeContent) scribeContent;

		log.debug("--> deliver(" + topic.getId().toStringFull() + "," + scribeContent + ")");
		log.debug("--> deliver: topic=" + vegaScribeContent.getTopic() + " ray=" + vegaScribeContent.getRay() + " iname=" + vegaScribeContent.getIname() + " inumber=" + vegaScribeContent.getInumber() + " nonce=" + vegaScribeContent.getNonce() + " content=" + vegaScribeContent.getContent() + " signature=" + vegaScribeContent.getSignature() + " hashcash=" + vegaScribeContent.getHashcash() + " flags=" + vegaScribeContent.getFlags() + " extension=" + vegaScribeContent.getExtension());

		try {

			// check general message properties

			boolean dataOk = 
					vegaScribeContent.getTopic() != null && 
					vegaScribeContent.getRay() != null && 
					vegaScribeContent.getIname() != null && 
					vegaScribeContent.getInumber() != null && 
					vegaScribeContent.getNonce() != null && 
					vegaScribeContent.getSignature() != null && 
					vegaScribeContent.getHashcash() != null;

			boolean nonceOk = NonceUtil.checkNonce(vegaScribeContent.getNonce());

			boolean topicOk = 
					topic.getId().equals(this.pastryIdFactory.buildId(vegaScribeContent.getTopic()))
					&&
					(
							(! vegaScribeContent.getTopic().startsWith("=")) || 
							(! vegaScribeContent.getTopic().startsWith("@")) || 
							vegaScribeContent.getTopic().equals(this.orion.iname() + vegaScribeContent.getRay()) || 
							vegaScribeContent.getTopic().equals(this.orion.inumber() + vegaScribeContent.getRay()) ||
							vegaScribeContent.getTopic().equals(this.orion.iname()) || 
							vegaScribeContent.getTopic().equals(this.orion.inumber())
							);

			boolean signatureOk = "1".equals(this.orion.verify(topic.getId().toStringFull() + " " + vegaScribeContent.getRay() + " " + vegaScribeContent.getNonce() + " " + vegaScribeContent.getContent(), vegaScribeContent.getSignature(), vegaScribeContent.getInumber()));

			HashCash hashcash = HashCash.fromString(vegaScribeContent.getHashcash());
			boolean hashcashOk = hashcash.getTo().equals(topic.getId().toStringFull()) && hashcash.isValid();

			log.debug("--> deliver: dataOk=" + dataOk + " nonceOk=" + nonceOk + " topicOk=" + topicOk + " signatureOk=" + signatureOk + " hashcashOk=" + hashcashOk);
			if (! dataOk || ! nonceOk || ! topicOk || ! signatureOk || ! hashcashOk) return;

			// queue JSON packet

			this.addPacket(
					vegaScribeContent.getRay(),
					vegaScribeContent.getTopic(),
					"{\n" + 
							" \"type\":\"multicast\",\n" +
							" \"topic\":" + escapeJSON(vegaScribeContent.getTopic()) + ",\n" +
							" \"ray\":" + escapeJSON(vegaScribeContent.getRay()) + ",\n" +
							" \"iname\":" + escapeJSON(vegaScribeContent.getIname()) + ",\n" +
							" \"inumber\":" + escapeJSON(vegaScribeContent.getInumber()) + ",\n" +
							" \"nonce\":" + escapeJSON(vegaScribeContent.getNonce()) + ",\n" + 
							" \"content\":" + escapeJSON(vegaScribeContent.getContent()) + ",\n" + 
							" \"signature\":" + escapeJSON(vegaScribeContent.getSignature()) + ",\n" + 
							" \"hashcash\":" + escapeJSON(vegaScribeContent.getHashcash()) + ",\n" + 
							" \"flags\":" + escapeJSON(vegaScribeContent.getFlags()) + ",\n" + 
							" \"extension\":" + escapeJSON(vegaScribeContent.getExtension()) + "\n" + 
					"}");
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	public boolean anycast(Topic topic, ScribeContent scribeContent) {

		if (! (scribeContent instanceof VegaScribeContent)) return false;
		VegaScribeContent vegaScribeContent = (VegaScribeContent) scribeContent;

		log.debug("--> anycast(" + topic.getId().toStringFull() + "," + scribeContent + ")");
		log.debug("--> anycast: topic=" + vegaScribeContent.getTopic() + " ray=" + vegaScribeContent.getRay() + " iname=" + vegaScribeContent.getIname() + " inumber=" + vegaScribeContent.getInumber() + " nonce=" + vegaScribeContent.getNonce() + " content=" + vegaScribeContent.getContent() + " signature=" + vegaScribeContent.getSignature() + " hashcash=" + vegaScribeContent.getHashcash() + " flags=" + vegaScribeContent.getFlags() + " extension=" + vegaScribeContent.getExtension());

		try {

			boolean dataOk = 
					vegaScribeContent.getTopic() != null && 
					vegaScribeContent.getRay() != null && 
					vegaScribeContent.getIname() != null && 
					vegaScribeContent.getInumber() != null && 
					vegaScribeContent.getNonce() != null && 
					vegaScribeContent.getSignature() != null && 
					vegaScribeContent.getHashcash() != null;

			boolean nonceOk = NonceUtil.checkNonce(vegaScribeContent.getNonce());

			boolean topicOk = 
					topic.getId().equals(this.pastryIdFactory.buildId(vegaScribeContent.getTopic()))
					&&
					(
							(! vegaScribeContent.getTopic().startsWith("=")) || 
							(! vegaScribeContent.getTopic().startsWith("@")) || 
							vegaScribeContent.getTopic().equals(this.orion.iname() + vegaScribeContent.getRay()) || 
							vegaScribeContent.getTopic().equals(this.orion.inumber() + vegaScribeContent.getRay()) ||
							vegaScribeContent.getTopic().equals(this.orion.iname()) || 
							vegaScribeContent.getTopic().equals(this.orion.inumber())
							);

			boolean signatureOk = "1".equals(this.orion.verify(topic.getId().toStringFull() + " " + vegaScribeContent.getRay() + " " + vegaScribeContent.getNonce() + " " + vegaScribeContent.getContent(), vegaScribeContent.getSignature(), vegaScribeContent.getInumber()));

			HashCash hashcash = HashCash.fromString(vegaScribeContent.getHashcash());
			boolean hashcashOk = hashcash.getTo().equals(topic.getId().toStringFull()) && hashcash.isValid();

			log.debug("--> anycast: dataOk=" + dataOk + " nonceOk=" + nonceOk + " topicOk=" + topicOk + " signatureOk=" + signatureOk + " hashcashOk=" + hashcashOk);
			if (! dataOk || ! nonceOk || ! topicOk || ! signatureOk || ! hashcashOk) return false;

			this.addPacket(
					vegaScribeContent.getRay(),
					vegaScribeContent.getTopic(),
					"{\n" + 
							" \"type\":\"anycast\",\n" +
							" \"topic\":" + escapeJSON(vegaScribeContent.getTopic()) + ",\n" +
							" \"ray\":" + escapeJSON(vegaScribeContent.getRay()) + ",\n" +
							" \"iname\":" + escapeJSON(vegaScribeContent.getIname()) + ",\n" +
							" \"inumber\":" + escapeJSON(vegaScribeContent.getInumber()) + ",\n" +
							" \"nonce\":" + escapeJSON(vegaScribeContent.getNonce()) + ",\n" +
							" \"content\":" + escapeJSON(vegaScribeContent.getContent()) + ",\n" + 
							" \"signature\":" + escapeJSON(vegaScribeContent.getSignature()) + ",\n" + 
							" \"hashcash\":" + escapeJSON(vegaScribeContent.getHashcash()) + ",\n" + 
							" \"flags\":" + escapeJSON(vegaScribeContent.getFlags()) + ",\n" + 
							" \"extension\":" + escapeJSON(vegaScribeContent.getExtension()) + "\n" + 
					"}");
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}

		return true;
	}

	/*
	 * Helper methods
	 */

	private void addPacket(String ray, String topic, String packet) {

		for (String client : this.rays.keySet()) {

			// see if the client is subscribed to the ray

			if (this.rays.get(client).contains(ray)) {

				// see if the client is subscribed to the topic (if topic is not null)

				if (topic != null) {

					List<String> clientTopics = this.topics.get(client);
					if (clientTopics == null || (! clientTopics.contains(topic))) continue;
				}

				// enqueue the packet for the client

				Queue<String> packets = this.packets.get(client);
				if (packets == null) {

					packets = new LinkedList<String> ();
					this.packets.put(client, packets);
				}
				packets.add(packet);
			}
		}
	}

	private static String escapeJSON(String str) {

		if (str == null)
			return "null";
		else
			return "\"" + str.replace("\"", "\\\"") + "\"";
	}

	private static String hash(String str) {

		String hash;

		try {

			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(str.getBytes());
			hash = new String(Base64.encodeBase64(digest.digest()));
		} catch (NoSuchAlgorithmException ex) {

			throw new RuntimeException("hash(): " + ex.getMessage(), ex);
		}

		return hash;
	}

	/*	private boolean isInternetRoutablePrefix(InetAddress address) {    

		String ip = address.getHostAddress();
		String nattedNetworkPrefixes = this.environment.getParameters().getString("nat_network_prefixes");

		String[] nattedNetworkPrefix = nattedNetworkPrefixes.split(";");

		for (int i=0; i<nattedNetworkPrefix.length; i++) {

			if (ip.startsWith(nattedNetworkPrefix[i])) return false;
		}

		return true;
	}*/
}
