package pds.endpoint.hcard;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
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

	private String format;
	private String contentType;

	private String html;

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

		if (this.format == null) throw new Exception("Please specify a format in the servlet's init parameters.");

		BufferedReader reader = new BufferedReader(new FileReader(new File(this.servletContext.getRealPath("/WEB-INF/html.vm"))));
		String line;

		this.html = "";
		while ((line = reader.readLine()) != null) this.html += line;
		reader.close();
	}

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {

			if ("GET".equals(request.getMethod())) this.doGet(request, response);
			else response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	private void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Properties properties = new Properties();
		HCard hCard = this.getHCard(request, properties);

		if (this.contentType != null) response.setContentType(this.contentType);
		Writer writer = response.getWriter();

		if ("html".equals(this.format)) {

			VelocityContext context = new VelocityContext(properties);
			context.put("hcard", hCard.toHTML());

			Reader templateReader = new StringReader(this.html);
			Velocity.evaluate(context, writer, "html", templateReader);
			templateReader.close();
		} else if ("json".equals(this.format)) {

			writer.write(hCard.toJSON());
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
		if (subject == null) throw new RuntimeException("User " + context.getCanonical() + " not found.");

		return subject;
	}

	private HCard getHCard(HttpServletRequest request, Properties properties) throws Exception {

		String xri = this.parseXri(request);
		XdiContext context = this.getContext(xri);
		Subject subject = this.fetch(context);

		String uid = context.getCanonical().toString();
		String name = Addressing.findLiteralData(subject, new XRI3("$" + PdsDictionary.XRI_NAME.toString()));
		String email = Addressing.findLiteralData(subject, new XRI3("$" + PdsDictionary.XRI_EMAIL.toString()));

		HCardBuilder hCardBuilder = HCard.build(name);
		hCardBuilder.setUID(uid);

		if (email != null) hCardBuilder.addEmail(new HCard.Email(email));

		properties.put("xri", xri);
		properties.put("inumber", context.getCanonical().toString());
		properties.put("x3simple", subject.toString("X3 Simple", null));
		properties.put("x3standard", subject.toString("X3 Simple", null));
		if (name != null) properties.put("name", name);
		if (email != null) properties.put("email", email);

		return hCardBuilder.done();
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
