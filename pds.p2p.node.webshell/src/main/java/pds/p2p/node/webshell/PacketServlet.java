package pds.p2p.node.webshell;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;

public class PacketServlet extends HttpServlet {

	private static final long serialVersionUID = 6652751574586165970L;

	private static Logger log = LoggerFactory.getLogger(PacketServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// wait for packet

		String packet;
		
		try {

			while (! "1".equals(DanubeApiClient.vegaObject.hasPackets("webshell"))) {

				Thread.sleep(500);
			}

			packet = DanubeApiClient.vegaObject.fetchPacket("webshell");

			if (log.isDebugEnabled()) log.debug("Got packet: " + packet);
		} catch (Exception ex) {

			throw new ServletException(ex.getMessage(), ex);
		}

		// done
		
		response.setContentType("application/json");
		response.getWriter().println(packet);
		response.getWriter().flush();
		response.getWriter().close();
	}
}
