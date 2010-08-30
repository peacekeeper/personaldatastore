package pds.web;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class PDSSpringServlet extends HttpServlet {

	private static final long serialVersionUID = 6840300824458871386L;

	private PDSServlet pdsServlet;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {

		super.init(servletConfig);

		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		this.pdsServlet = (PDSServlet) wac.getBean("PDSServlet", PDSServlet.class);
		this.pdsServlet.init(servletConfig);
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		this.pdsServlet.service(request, response);
	}
}
