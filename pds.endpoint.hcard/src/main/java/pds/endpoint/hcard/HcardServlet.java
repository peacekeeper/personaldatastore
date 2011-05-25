package pds.endpoint.hcard;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.addressing.Addressing;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.microformats.hCard.HCard;
import org.microformats.hCard.HCardBuilder;
import org.openxri.resolve.Resolver;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.ServletContextAware;

import pds.dictionary.PdsDictionary;
import pds.xdi.Xdi;
import pds.xdi.XdiContext;

public class HcardServlet implements HttpRequestHandler, ServletContextAware {

	private static final long serialVersionUID = 9135016266076360503L;

	private static final Log log = LogFactory.getLog(HcardServlet.class.getName());

	private static final Xdi xdi;

	private String html;

	private String rssFeedEndpoint;
	private String atomFeedEndpoint;
	private String foafEndpoint;

	static {

		try {

			xdi = new Xdi(new Resolver(null));
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize XDI: " + ex.getMessage(), ex);
		}
	}

	private ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {

		this.servletContext = servletContext;
	}

	public void init() throws Exception {

		BufferedReader reader = new BufferedReader(new FileReader(new File(this.servletContext.getRealPath("/WEB-INF/html.vm"))));
		String line;

		this.html = "";
		while ((line = reader.readLine()) != null) this.html += line + "\n";
		reader.close();
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

		String path = this.parsePath(request);
		XdiContext context = this.getContext(path);
		Subject pdsSubject = context == null ? null : this.fetch(context);

		if (pdsSubject == null) {

			response.sendError(HttpServletResponse.SC_NOT_FOUND, path + " not found.");
			return;
		}

		Properties properties = new Properties();
		HCard hCard = this.convertHCard(path, context, pdsSubject, properties);

		// determine content type

		String contentType;

		if (request.getHeader("Accept").contains("application/json")) {

			contentType = "application/json";
		} else {

			contentType = "text/html";
		}

		// output it

		response.setContentType(contentType);
		Writer writer = response.getWriter();

		if ("text/html".equals(contentType)) {

			VelocityContext velocityContext = new VelocityContext(properties);
			velocityContext.put("hcard", hCard.toHTML());
			if (this.rssFeedEndpoint != null) velocityContext.put("rssFeedEndpoint", this.rssFeedEndpoint);
			if (this.atomFeedEndpoint != null) velocityContext.put("atomFeedEndpoint", this.atomFeedEndpoint);
			if (this.foafEndpoint != null) velocityContext.put("foafEndpoint", this.foafEndpoint);

			Reader templateReader = new StringReader(this.html);
			Velocity.evaluate(velocityContext, writer, "html", templateReader);
			templateReader.close();
		} else if ("application/json".equals(contentType)) {

			writer.write(hCard.toJSON());
		}

		writer.flush();
		writer.close();
	}

	private String parsePath(HttpServletRequest request) throws Exception {

		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath(); 
		String path = requestUri.substring(contextPath.length());
		if (path.startsWith("/")) path = path.substring(1);

		log.debug("Got request for path " + path);
		return path;
	}

	private XdiContext getContext(String path) throws Exception {

		boolean xri = false;

		try {

			new XRI3Segment(path);
			xri = true;
		} catch (Exception ex) { }

		if (xri)
			return xdi.resolveContextByIname(path, null);
		else
			return xdi.resolveContextByEndpoint(path, null);
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

	private HCard convertHCard(String identifier, XdiContext context, Subject pdsSubject, Properties properties) throws Exception {

		String uid = context.getCanonical().toString();
		String name = Addressing.findLiteralData(pdsSubject, new XRI3("$" + PdsDictionary.XRI_NAME.toString()));
		String email = Addressing.findLiteralData(pdsSubject, new XRI3("$" + PdsDictionary.XRI_EMAIL.toString()));

		if (name == null) name = uid;

		HCardBuilder hCardBuilder = HCard.build(name);
		hCardBuilder.setUID(uid);
		hCardBuilder.addURL(new URI("http://xri2xrd.net/" + context.getCanonical().toString()));
		if (email != null) hCardBuilder.addEmail(new HCard.Email(email));

		properties.put("identifier", identifier);
		properties.put("canonical", context.getCanonical().toString());
		properties.put("x3simple", pdsSubject.toString("X3 Simple", null));
		properties.put("x3standard", pdsSubject.toString("X3 Simple", null));
		if (name != null) properties.put("name", name);
		if (email != null) properties.put("email", email);

		return hCardBuilder.done();
	}

	public String getRssFeedEndpoint() {

		return this.rssFeedEndpoint;
	}

	public void setRssFeedEndpoint(String rssFeedEndpoint) {

		this.rssFeedEndpoint = rssFeedEndpoint;
		if (! this.rssFeedEndpoint.endsWith("/")) this.rssFeedEndpoint += "/";
	}

	public String getAtomFeedEndpoint() {

		return this.atomFeedEndpoint;
	}

	public void setAtomFeedEndpoint(String atomFeedEndpoint) {

		this.atomFeedEndpoint = atomFeedEndpoint;
		if (! this.atomFeedEndpoint.endsWith("/")) this.atomFeedEndpoint += "/";
	}

	public String getFoafEndpoint() {

		return this.foafEndpoint;
	}

	public void setFoafEndpoint(String foafEndpoint) {

		this.foafEndpoint = foafEndpoint;
		if (! this.foafEndpoint.endsWith("/")) this.foafEndpoint += "/";
	}
}
