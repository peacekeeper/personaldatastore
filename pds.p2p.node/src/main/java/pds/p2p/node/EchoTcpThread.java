package pds.p2p.node;

import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EchoTcpThread extends Thread {

	private static Log log = LogFactory.getLog(EchoTcpThread.class);

	private int ipPort;
	private boolean running;

	private ServerSocket serverSocket;

	public EchoTcpThread(int ipPort) {

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

		log.info("ECHO TCP Thread " + Thread.currentThread().getId() + " starting.");

		// open socket
		
		try {

			this.serverSocket = new ServerSocket();
			this.serverSocket.bind(new InetSocketAddress(this.ipPort));
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		// listen on socket
		
		while (this.running) {

			try {

				Socket socket = this.serverSocket.accept();
				PrintWriter writer = new java.io.PrintWriter(socket.getOutputStream(),true);
				log.info("ECHO TCP Thread got connection from " + socket.getInetAddress().getHostAddress() + ".");

				writer.println(socket.getInetAddress().getHostAddress());
				writer.flush();
				writer.close();
				socket.close();
			} catch (Exception ex) {

				if (! this.running) break;
				
				log.error("ECHO TCP Thread " + Thread.currentThread().getId() + " had exception: " + ex.getMessage(), ex);
			}
		}

		// close socket
		
		try {

			this.serverSocket.close();
		} catch (Exception ex) { 
			
			log.error(ex.getMessage(), ex);
		}

		// done
		
		log.info("ECHO TCP Thread " + Thread.currentThread().getId() + " stopped.");
	}
}