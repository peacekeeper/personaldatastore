package pds.endpoint.poco;


import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.addressing.Addressing;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.openxri.resolve.Resolver;
import org.springframework.web.HttpRequestHandler;

import pds.dictionary.PdsDictionary;
import pds.xdi.Xdi;
import pds.xdi.XdiContext;

public class PocoServlet implements HttpRequestHandler {

	private static final long serialVersionUID = 1534867676264547673L;

	private static final Log log = LogFactory.getLog(PocoServlet.class.getName());

	private static final Xdi xdi;

	private String format;
	private String contentType;

	static {

		try {

			xdi = new Xdi(new Resolver(null));
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize XDI: " + ex.getMessage(), ex);
		}
	}

	public void init() throws Exception {

		if (this.format == null) throw new ServletException("Please specify a format in the servlet's init parameters.");
	}

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		log.trace(request.getMethod() + ": " + request.getRequestURI());

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
		XdiContext context = this.getContext(xri);
		Subject pdsSubject = this.fetch(context);

		if (pdsSubject == null) {

			response.sendError(HttpServletResponse.SC_NOT_FOUND, xri + " not found.");
			return;
		}

		Poco poco = this.convertPoco(xri, context, pdsSubject);

		// output it

		if (this.contentType != null) response.setContentType(this.contentType);
		Writer writer = response.getWriter();

		if ("xml".equals(this.format)) {

			writer.write(poco.toXML());
		} else if ("json".equals(this.format)) {

			writer.write(poco.toJSON());
		}

		writer.flush();
		writer.close();
	}

	private String parseXri(HttpServletRequest request) throws Exception {

		String xri = request.getRequestURI();
		while (xri.length() > 0 && xri.contains("/")) xri = xri.substring(xri.indexOf("/") + 1);

		log.debug("Got request for XRI " + xri);
		return xri;
	}

	private XdiContext getContext(String xri) throws Exception {

		return xdi.resolveContextByIname(xri, null);
	}

	private Subject fetch(XdiContext context) throws Exception {

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

	private Poco convertPoco(String xri, XdiContext context, Subject pdsSubject) throws Exception {

		String id = context.getCanonical().toString();
		String profileurl = "http://xri.net/" + context.getCanonical().toString();
		String displayname = Addressing.findLiteralData(pdsSubject, new XRI3("$" + PdsDictionary.XRI_NAME.toString()));
		String nameFormatted = Addressing.findLiteralData(pdsSubject, new XRI3("$" + PdsDictionary.XRI_NAME.toString()));
		String birthday = Addressing.findLiteralData(pdsSubject, new XRI3("$" + PdsDictionary.XRI_DATE_OF_BIRTH.toString()));
		String gender = Addressing.findLiteralData(pdsSubject, new XRI3("$" + PdsDictionary.XRI_GENDER.toString()));
		String email = Addressing.findLiteralData(pdsSubject, new XRI3("$" + PdsDictionary.XRI_EMAIL.toString()));

		return new Poco(id, profileurl, displayname, nameFormatted, birthday, gender, email);
	}

	public String getFormat() {

		return this.format;
	}

	public void setFormat(String format) {

		this.format = format;

		if ("json".equals(format)) this.contentType = "application/json";
		if ("html".equals(format)) this.contentType = "text/html";
	}
}
