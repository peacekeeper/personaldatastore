package pds.p2p.node;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EchoUdpThread extends Thread {

	private static Log log = LogFactory.getLog(EchoUdpThread.class);

	private int ipPort;
	private boolean running;

	private DatagramSocket serverSocket;

	public EchoUdpThread(int ipPort) {

		this.ipPort = ipPort;
		this.running = true;

		this.setDaemon(true);
	}

	public void stopRunning() throws Exception {

		this.running = false;

		if (this.serverSocket != null && (! this.serverSocket.isClosed())) this.serverSocket.close();
	}

	@Override
	public void run() {

		log.info("ECHO UDP Thread " + Thread.currentThread().getId() + " starting on port " + this.ipPort + ".");

		// open socket

		try {

			this.serverSocket = new DatagramSocket(null);
			this.serverSocket.setReuseAddress(true);
			this.serverSocket.bind(new InetSocketAddress(this.ipPort));
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			throw new RuntimeException(ex.getMessage(), ex);
		}

		// listen on socket

		while (this.running) {

			try {

				byte[] receiveBuffer = new byte[256];
				byte[] sendBuffer;
				String receiveString;
				String sendString;

				DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
				this.serverSocket.receive(receivePacket);
				receiveString = new String(receiveBuffer).substring(0, receivePacket.getLength());

				if (! receiveString.equals(":)")) {

					log.info("ECHO UDP Thread " + Thread.currentThread().getId() + " got unexpected UDP data: " + receiveString);
					continue;
				}

				sendString = receivePacket.getAddress().getHostAddress() + " " + Integer.toString(receivePacket.getPort());
				sendBuffer = sendString.getBytes();
				log.info("ECHO UDP Thread " + Thread.currentThread().getId() + " sending response: " + sendString);

				DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, receivePacket.getAddress(), receivePacket.getPort());
				this.serverSocket.send(sendPacket);
			} catch (Exception ex) {

				if (! this.running) break;

				log.error("ECHO UDP Thread " + Thread.currentThread().getId() + " had exception: " + ex.getMessage(), ex);
			}
		}

		// close socket

		try {

			this.serverSocket.close();
		} catch (Exception ex) { 

			log.error(ex.getMessage(), ex);
		}

		// done

		log.info("ECHO UDP Thread " + Thread.currentThread().getId() + " stopped.");
	}
}