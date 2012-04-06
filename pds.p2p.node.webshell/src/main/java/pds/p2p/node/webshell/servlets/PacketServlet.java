package pds.p2p.node.webshell.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;

public class PacketServlet extends HttpServlet {

	private static final long serialVersionUID = 6652751574586165970L;

	private static Logger log = LoggerFactory.getLogger(PacketServlet.class);

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// wait for packet

		try {

			while (! "1".equals(DanubeApiClient.vegaObject.hasPackets("webshell"))) {

				Thread.sleep(500);
			}

			String packet = DanubeApiClient.vegaObject.hasPackets("webshell");

			if (log.isDebugEnabled()) log.debug("Got packet: " + packet);

			response.setContentType("application/json");
			response.getWriter().print(packet);
		} catch (Exception ex) {

			throw new ServletException(ex.getMessage(), ex);
		}

		// done

		Context.exit();
	}
}
