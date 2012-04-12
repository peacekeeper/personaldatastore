package pds.p2p.node.servlets;

import java.io.EOFException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.node.DanubeApiServer;

public class PacketServlet extends HttpServlet {

	private static final long serialVersionUID = 6652751574586165970L;

	private static Logger log = LoggerFactory.getLogger(PacketServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// read parameters

		String client = request.getParameter("client");

		if (client == null) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing 'client' parameter");
			return;
		}

		// stream packets to client

		String packet;

		try {

			response.setBufferSize(0);

			do {

				if ("1".equals(DanubeApiServer.vegaObject.hasPackets(client))) {

					packet = DanubeApiServer.vegaObject.fetchPacket(client);
					if (log.isDebugEnabled()) log.debug("Got packet: " + packet);
				} else {

					packet = " ";
				}

				try {

					response.getOutputStream().print(packet);
					response.getOutputStream().flush();
				} catch (EOFException ex) {

					break;
				}

				Thread.sleep(1000);
			} while (packet.equals(" "));
		} catch (Exception ex) {

			log.warn(ex.getMessage(), ex);
			throw new ServletException(ex.getMessage(), ex);
		}
	}
}
