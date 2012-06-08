package pds.profile;

import javax.servlet.http.HttpServlet;

import org.openxri.resolve.Resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.xdi.XdiClient;

public class ProfileServlet extends HttpServlet {

	private static final long serialVersionUID = 9135016266076360503L;

	private static final Logger log = LoggerFactory.getLogger(ProfileServlet.class.getName());

	private static final XdiClient xdi;

	static {

		try {

			xdi = new XdiClient(new Resolver(null));
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize XDI: " + ex.getMessage(), ex);
		}
	}
}
