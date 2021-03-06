package pds.endpoint.foaf;


import java.io.IOException;
import java.io.Writer;

import javax.security.auth.Subject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openxri.resolve.Resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestHandler;

import pds.dictionary.PdsDictionary;
import pds.xdi.XdiClient;
import pds.xdi.XdiEndpoint;
import xdi2.core.Graph;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;

public class FoafServlet implements HttpRequestHandler {

	private static final long serialVersionUID = 1534867676264547673L;

	private static final Logger log = LoggerFactory.getLogger(FoafServlet.class.getName());

	private static final XdiClient xdi;

	static {

		try {

			xdi = new XdiClient(new Resolver(null));
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize XDI: " + ex.getMessage(), ex);
		}
	}

	public void init() throws Exception {

	}

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		log.trace(request.getMethod() + ": " + request.getRequestURI() + ", Content-Type: " + request.getContentType() + ", Content-Length: " + request.getContentLength());

		try {

			if ("GET".equals(request.getMethod())) this.doGet(request, response);
			else response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	private void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// find the XDI data

		String xri = this.parseXri(request);
		XdiEndpoint context = this.getContext(xri);
		Subject pdsSubject = this.fetch(context);

		if (pdsSubject == null) {

			response.sendError(HttpServletResponse.SC_NOT_FOUND, xri + " not found.");
			return;
		}

		String foaf = this.convertFoaf(xri, context, pdsSubject);

		// output it

		response.setContentType("application/rdf+xml");
		Writer writer = response.getWriter();
		writer.write(foaf);
		writer.flush();
		writer.close();
	}

	private String parseXri(HttpServletRequest request) throws Exception {

		String xri = request.getRequestURI();
		while (xri.length() > 0 && xri.contains("/")) xri = xri.substring(xri.indexOf("/") + 1);

		log.debug("Got request for XRI " + xri);
		return xri;
	}

	private XdiEndpoint getContext(String xri) throws Exception {

		return xdi.resolveContextByIname(xri, null);
	}

	private Subject fetch(XdiEndpoint context) throws Exception {

		Operation operation = context.prepareOperation(MessagingConstants.XRI_GET);
		Graph operationGraph = operation.createOperationGraph(null);
		operationGraph.createStatement(context.getCanonical(), new XRI3Segment("$" + PdsDictionary.XRI_NAME.toString()));
		operationGraph.createStatement(context.getCanonical(), new XRI3Segment("$" + PdsDictionary.XRI_DATE_OF_BIRTH.toString()));
		operationGraph.createStatement(context.getCanonical(), new XRI3Segment("$" + PdsDictionary.XRI_GENDER.toString()));
		operationGraph.createStatement(context.getCanonical(), new XRI3Segment("$" + PdsDictionary.XRI_EMAIL.toString()));

		MessageResult messageResult = context.send(operation);

		Subject subject = messageResult.getGraph().getSubject(context.getCanonical());
		if (subject == null) return null;

		return subject;
	}

	private String convertFoaf(String xri, XdiEndpoint context, Subject pdsSubject) throws Exception {
/*
		String id = context.getCanonical().toString();
		String profileurl = "http://xri.net/" + context.getCanonical().toString();
		String displayname = Addressing.findLiteralData(pdsSubject, new XRI3("$" + PdsDictionary.XRI_NAME.toString()));
		String nameFormatted = Addressing.findLiteralData(pdsSubject, new XRI3("$" + PdsDictionary.XRI_NAME.toString()));
		String birthday = Addressing.findLiteralData(pdsSubject, new XRI3("$" + PdsDictionary.XRI_DATE_OF_BIRTH.toString()));
		String gender = Addressing.findLiteralData(pdsSubject, new XRI3("$" + PdsDictionary.XRI_GENDER.toString()));
		String email = Addressing.findLiteralData(pdsSubject, new XRI3("$" + PdsDictionary.XRI_EMAIL.toString()));
*/
		return "todo";
	}
}
