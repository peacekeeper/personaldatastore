package pds.p2p.node;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.jsonrpc4j.JsonRpcServer;

public class MyJsonRpcServlet extends HttpServlet {

	private static final long serialVersionUID = 7453275488406497744L;

	private static final Logger log = LoggerFactory.getLogger(MyJsonRpcServlet.class);

	private Object jsonRpcObject;
	private JsonRpcServer jsonRpcServer;

	public MyJsonRpcServlet(Object jsonRpcObject) {

		this.jsonRpcObject = jsonRpcObject;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {

		super.init(config);

		this.jsonRpcServer = new JsonRpcServer(this.jsonRpcObject, this.jsonRpcObject.getClass());
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		log.debug(this.jsonRpcObject.getClass() + ": service()");

		this.jsonRpcServer.handle(request, response);
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}

	public Object getJsonRpcObject() {

		return this.jsonRpcObject;
	}
}