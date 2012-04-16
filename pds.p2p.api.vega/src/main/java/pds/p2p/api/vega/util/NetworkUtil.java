package pds.p2p.api.vega.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.util.SubnetUtils;

public class NetworkUtil {

	private static Log log = LogFactory.getLog(NetworkUtil.class);

	public static InetSocketAddress detectLocalAddress(String localHost, String localPort) throws UnknownHostException, SocketException {

		InetAddress localAddr = null;

		// check if the local host is a wildcard

		if (localHost.endsWith("/")) {

			localHost = localHost.substring(0, localHost.length() - 1);
			
			// find out if there is a preferred interface for the local address

			for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {

				NetworkInterface networkInterface = e.nextElement();

				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {

					InetAddress interfaceAddr = interfaceAddress.getAddress();
					short interfaceNetworkPrefixLength = interfaceAddress.getNetworkPrefixLength();

					String interfaceAddrString = interfaceAddr.toString().substring(interfaceAddr.toString().indexOf('/') + 1);
					String interfaceNetworkPrefixLengthString = Short.toString(interfaceNetworkPrefixLength);
					String subnetString = interfaceAddrString + "/" + interfaceNetworkPrefixLengthString;

					log.debug("Checking local " + localHost + " in range of " + subnetString);

					if (! (interfaceAddr instanceof Inet4Address)) {

						log.debug("Not an IPv4 address: " + interfaceAddrString);
						continue;
					}

					if (interfaceNetworkPrefixLength < 1 || interfaceNetworkPrefixLength > 32) {

						log.debug("Not an valid network prefix: " + interfaceNetworkPrefixLengthString);
						continue;
					}

					SubnetUtils subnetUtils = new SubnetUtils(subnetString);
					SubnetUtils.SubnetInfo subnetInfo = subnetUtils.getInfo();

					boolean inRange = subnetInfo.isInRange(localHost);
					log.debug("Checked local " + localHost + " in range of " + subnetString + " - " + Boolean.toString(inRange));

					if (inRange) localAddr = interfaceAddress.getAddress();
				}
			}
		}

		// resolve local address

		if (localAddr == null) {

			localAddr = InetAddress.getByName(localHost);
		}

		// done

		return new InetSocketAddress(localAddr, Integer.valueOf(localPort).intValue());
	}

	public static InetSocketAddress detectRemoteAddress(String remoteHost, String remotePort) throws IOException {

		// resolve remote address

		InetAddress remoteAddr = InetAddress.getByName(remoteHost);

		// check if the remote address is local

		for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {

			NetworkInterface networkInterface = e.nextElement();

			for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {

				if (remoteAddr.equals(interfaceAddress.getAddress())) {

					throw new RuntimeException("Remote address is local. To start a new network, please pass null as remote address.");
				}
			}
		}

		// check if the remote address is reachable

		if (! remoteAddr.isReachable(1000)) {

			throw new RuntimeException("Remote address " + remoteAddr.toString() + " is not reachable.");
		}		

		// done

		return new InetSocketAddress(remoteAddr, Integer.valueOf(remotePort).intValue());
	}

	public static InetSocketAddress detectPublicAddress(InetSocketAddress localSockAddr, InetSocketAddress remoteSockAddr) throws IOException {

		// ask remote address for our public address

		DatagramSocket clientSocket = new DatagramSocket(null);
		clientSocket.setReuseAddress(true);
		clientSocket.setSoTimeout(10000);
		clientSocket.bind(localSockAddr);

		byte[] sendBuffer;
		byte[] receiveBuffer = new byte[256];
		String sendString;
		String receiveString;

		sendString = ":)";
		sendBuffer = sendString.getBytes();

		InetSocketAddress remoteEchoSockAddr = new InetSocketAddress(remoteSockAddr.getAddress(), remoteSockAddr.getPort() - 1);

		DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, remoteEchoSockAddr);
		log.debug("Sending probe packet...");
		clientSocket.send(sendPacket);

		DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
		log.debug("Receiving probe packet...");
		clientSocket.receive(receivePacket);
		receiveString = new String(receiveBuffer).substring(0, receivePacket.getLength());

		String publicHost = receiveString.split(" ")[0];
		String publicPort = receiveString.split(" ")[1];

		clientSocket.close();

		// resolve public address

		InetAddress publicAddr = InetAddress.getByName(publicHost);

		// check if the public address is local

		for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {

			NetworkInterface networkInterface = e.nextElement();

			for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {

				if (publicAddr.equals(interfaceAddress.getAddress())) {

					log.info("Public address is local.");

					return null;
				}
			}
		}

		// done

		return new InetSocketAddress(publicAddr, Integer.valueOf(publicPort).intValue());
	}

	public static InetSocketAddress detectBestLocalAddress(InetSocketAddress remoteSockAddr, String localPort) throws SocketException, UnknownHostException {

		InetAddress bestLocalAddr = null;

		// no remote address?

		if (remoteSockAddr != null) {

			String remoteHost = remoteSockAddr.getAddress().toString().substring(remoteSockAddr.getAddress().toString().indexOf('/') + 1);
			
			// find out if there is a preferred interface for the remote address

			for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {

				NetworkInterface networkInterface = e.nextElement();

				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {

					InetAddress interfaceAddr = interfaceAddress.getAddress();
					short interfaceNetworkPrefixLength = interfaceAddress.getNetworkPrefixLength();

					String interfaceAddrString = interfaceAddr.toString().substring(interfaceAddr.toString().indexOf('/') + 1);
					String interfaceNetworkPrefixLengthString = Short.toString(interfaceNetworkPrefixLength);
					String subnetString = interfaceAddrString + "/" + interfaceNetworkPrefixLengthString;

					log.debug("Checking remote " + remoteHost + " in range of " + subnetString);

					if (! (interfaceAddr instanceof Inet4Address)) {

						log.debug("Not an IPv4 address: " + interfaceAddrString);
						continue;
					}

					if (interfaceNetworkPrefixLength < 1 || interfaceNetworkPrefixLength > 32) {

						log.debug("Not an valid network prefix: " + interfaceNetworkPrefixLengthString);
						continue;
					}

					SubnetUtils subnetUtils = new SubnetUtils(subnetString);
					SubnetUtils.SubnetInfo subnetInfo = subnetUtils.getInfo();

					boolean inRange = subnetInfo.isInRange(remoteHost);
					log.debug("Checked remote " + remoteHost + " in range of " + subnetString + " - " + Boolean.toString(inRange));

					if (inRange) bestLocalAddr = interfaceAddress.getAddress();
				}
			}
		}

		// fall back to default local address

		if (bestLocalAddr == null) {

			bestLocalAddr = InetAddress.getLocalHost();
		}

		// done

		return new InetSocketAddress(bestLocalAddr, Integer.valueOf(localPort).intValue());
	}
}
