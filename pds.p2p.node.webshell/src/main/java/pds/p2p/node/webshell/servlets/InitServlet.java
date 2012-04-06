package pds.p2p.node.webshell.servlets;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import pds.p2p.api.node.client.DanubeApiClient;

public class InitServlet extends HttpServlet {

	private static final long serialVersionUID = 6075303789675940878L;

	@Override
	public void init(ServletConfig config) throws ServletException {

		super.init(config);

		try {

			DanubeApiClient.init();
		} catch (Exception ex) {

			throw new ServletException(ex.getMessage(), ex);
		}
	}

	@Override
	public void destroy() {

		super.destroy();

		DanubeApiClient.shutdown();
	}
}
