package pds.profile;

import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxri.resolve.Resolver;

import pds.xdi.Xdi;

public class ProfileServlet extends HttpServlet {

	private static final long serialVersionUID = 9135016266076360503L;

	private static final Log log = LogFactory.getLog(ProfileServlet.class.getName());

	private static final Xdi xdi;

	static {

		try {

			xdi = new Xdi(new Resolver(null));
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize XDI: " + ex.getMessage(), ex);
		}
	}
}
