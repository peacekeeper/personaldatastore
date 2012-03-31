package pds.endpoint.feed;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.higgins.XDI2.Graph;
import org.eclipse.higgins.XDI2.Subject;
import org.eclipse.higgins.XDI2.constants.MessagingConstants;
import org.eclipse.higgins.XDI2.messaging.MessageResult;
import org.eclipse.higgins.XDI2.messaging.Operation;
import org.eclipse.higgins.XDI2.xri3.impl.XRI3Segment;
import org.openxri.resolve.Resolver;
import org.springframework.web.HttpRequestHandler;

import pds.dictionary.PdsDictionary;
import pds.dictionary.feed.FeedDictionary;
import pds.xdi.Xdi;
import pds.xdi.XdiContext;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedOutput;

public class FeedServlet implements HttpRequestHandler {

	private static final long serialVersionUID = 9135016266076360503L;

	private static final Logger log = LoggerFactory.getLogger(FeedServlet.class.getName());

	private static final Xdi xdi;

	private String format;
	private String contentType;

	private String hub;
	private String selfEndpoint;
	private String salmonEndpoint;

	static {

		try {

			xdi = new Xdi(new Resolver(null));
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize XDI: " + ex.getMessage(), ex);
		}
	}

	public void init() throws Exception {

		if (this.format == null) throw new Exception("Please specify a format in the servlet's init parameters.");
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
		XdiContext context = this.getContext(xri);
		Subject pdsSubject = context == null ? null : this.fetch(context);

		if (pdsSubject == null) {

			response.sendError(HttpServletResponse.SC_NOT_FOUND, xri + " not found.");
			return;
		}

		String salmonEndpoint = this.salmonEndpoint + context.getCanonical();
		String selfEndpoint = this.selfEndpoint + context.getCanonical();

		SyndFeed feed = FeedDictionary.toFeed(xri, pdsSubject, this.format, this.contentType, this.hub, selfEndpoint, salmonEndpoint);

		// output it

		if (this.contentType != null) response.setContentType(this.contentType);
		Writer writer = response.getWriter();

		SyndFeedOutput output = new SyndFeedOutput();
		output.output(feed, writer);

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
		operationGraph.createStatement(context.getCanonical(), FeedDictionary.XRI_FEED);
		operationGraph.createStatement(context.getCanonical(), new XRI3Segment("$" + PdsDictionary.XRI_NAME.toString()));
		operationGraph.createStatement(context.getCanonical(), new XRI3Segment("$" + PdsDictionary.XRI_DATE_OF_BIRTH.toString()));
		operationGraph.createStatement(context.getCanonical(), new XRI3Segment("$" + PdsDictionary.XRI_GENDER.toString()));
		operationGraph.createStatement(context.getCanonical(), new XRI3Segment("$" + PdsDictionary.XRI_EMAIL.toString()));

		MessageResult messageResult = context.send(operation);

		Subject subject = messageResult.getGraph().getSubject(context.getCanonical());
		if (subject == null) return null;

		return subject;
	}

	public String getFormat() {

		return this.format;
	}

	public void setFormat(String format) {

		this.format = format;

		if ("rss_2.0".equals(format)) this.contentType = "application/rss+xml";
		if ("atom_1.0".equals(format)) this.contentType = "application/atom+xml";
	}

	public String getHub() {

		return this.hub;
	}

	public void setHub(String hub) {

		this.hub = hub;
	}

	public String getSelfEndpoint() {

		return this.selfEndpoint;
	}

	public void setSelfEndpoint(String selfEndpoint) {

		this.selfEndpoint = selfEndpoint;
		if (! this.selfEndpoint.endsWith("/")) this.selfEndpoint += "/";
	}

	public String getSalmonEndpoint() {

		return this.salmonEndpoint;
	}

	public void setSalmonEndpoint(String salmonEndpoint) {

		this.salmonEndpoint = salmonEndpoint;
		if (! this.salmonEndpoint.endsWith("/")) this.salmonEndpoint += "/";
	}
}
