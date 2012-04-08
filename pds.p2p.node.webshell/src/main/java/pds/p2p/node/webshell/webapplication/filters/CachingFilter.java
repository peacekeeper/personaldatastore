package pds.p2p.node.webshell.webapplication.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CachingFilter implements Filter {

	public static final int DEFAULT_SECONDS = 360;

	private int seconds;

	public void init(FilterConfig config) throws ServletException {

		try {

			this.seconds = Integer.parseInt(config.getInitParameter("seconds"));
		} catch (Exception ex) {

			this.seconds = DEFAULT_SECONDS;
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		if (httpRequest.getProtocol().equals("HTTP/1.0")) {

			httpResponse.setDateHeader("Expires", System.currentTimeMillis()/1000 + this.seconds);
		} else if (httpRequest.getProtocol().equals("HTTP/1.1")) {

			httpResponse.setHeader("Cache-Control", "max-age=" + this.seconds);
		}

		chain.doFilter(request, response);
	}

	public void destroy() {

	}
}
