package pds.endpoint.feed;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.addressing.Addressing;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.types.Timestamps;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.openxri.resolve.Resolver;
import org.springframework.web.HttpRequestHandler;

import pds.xdi.Xdi;
import pds.xdi.XdiContext;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;

public class FeedServlet implements HttpRequestHandler {

	private static final long serialVersionUID = 9135016266076360503L;

	private static final Log log = LogFactory.getLog(FeedServlet.class.getName());

	private static final Xdi xdi;

	private String endpoint;
	private String format;

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

		try {

			if ("GET".equals(request.getMethod())) this.doGet(request, response);
			else response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	private void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {

		SyndFeed feed = this.getFeed(request);

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

		if (this.endpoint != null) {

			String endpoint = this.endpoint;
			if (! this.endpoint.endsWith("/")) this.endpoint += "/";
			this.endpoint += xri + "/";

			return xdi.resolveContextManually(endpoint, xri, new XRI3Segment(xri), null);
		} else {

			return xdi.resolveContextByIname(xri, null);
		}
	}

	private Iterator<Subject> fetch(XdiContext context) throws Exception {

		Operation operation = context.prepareOperation(MessagingConstants.XRI_GET);
		Graph operationGraph = operation.createOperationGraph(null);
		operationGraph.createStatement(context.getCanonical(), new XRI3Segment("+feed"));

		MessageResult messageResult = context.send(operation);

		Subject subject = messageResult.getGraph().getSubject(context.getCanonical());
		if (subject == null) throw new RuntimeException("User " + context.getCanonical() + " not found.");

		Predicate predicate = subject.getPredicate(new XRI3Segment("+feed"));
		if (predicate == null) throw new RuntimeException("Feed not found.");

		Graph innerGraph = predicate.getInnerGraph();
		if (innerGraph == null) throw new RuntimeException("Feed not found.");

		return innerGraph.getSubjects();
	}

	private SyndFeed getFeed(HttpServletRequest request) throws Exception {

		String xri = this.parseXri(request);
		XdiContext context = this.getContext(xri);
		Iterator<Subject> subjects = this.fetch(context);

		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType(this.format);

		feed.setTitle(xri);
		feed.setLink("http://xri.net/" + xri);
		feed.setDescription("Feed for " + xri);

		List<org.jdom.Element> foreignElements = new ArrayList<org.jdom.Element> ();
		foreignElements.add(makeLinkElement("hub", "http://pubsubhubbub.appspot.com/", null, null));
		foreignElements.add(makeLinkElement("salmon-reply", "http://pubsubhubbub.appspot.com/", null, null));
		foreignElements.add(makeLinkElement("salmon-mention", "http://pubsubhubbub.appspot.com/", null, null));
		foreignElements.add(makeLinkElement("alternate", "http://xri.net/" + context.getCanonical() + "/", "text/html", null));
		feed.setForeignMarkup(foreignElements);

		List<SyndEntry> entries = new ArrayList<SyndEntry> ();
		SyndEntry entry;
		SyndContent description;

		while (subjects.hasNext()) {

			Subject subject = subjects.next();

			try {

				Date timestamp = Timestamps.xriToDate(Addressing.findReferenceXri(subject, new XRI3("$d")));
				String content = Addressing.findLiteralData(subject, new XRI3("$a$xsd$string"));

				entry = new SyndEntryImpl();
				entry.setPublishedDate(timestamp);
				entry.setTitle(content);
				entry.setLink("http://xri.net/" + xri);
				description = new SyndContentImpl();
				description.setType("text/plain");
				description.setValue(content);
				entry.setDescription(description);
				entries.add(entry);

				log.debug("Added entry for " + subject.getSubjectXri());
			} catch (Exception ex) {

				log.warn("Skipping entry " + subject.getSubjectXri() + ": " + ex.getMessage(), ex);
			}
		}

		feed.setEntries(entries);

		return feed;
	}

	private static org.jdom.Element makeLinkElement(String rel, String href, String type, String title) {

		org.jdom.Element element;
		element = new org.jdom.Element("link");
		if (rel != null) element.setAttribute("rel", rel);
		if (href != null) element.setAttribute("href", href);
		if (type != null) element.setAttribute("type", type);
		if (title != null) element.setAttribute("title", title);

		return element;
	}

	public String getEndpoint() {

		return this.endpoint;
	}

	public void setEndpoint(String endpoint) {

		this.endpoint = endpoint;
	}

	public String getFormat() {

		return this.format;
	}

	public void setFormat(String format) {

		this.format = format;
	}
}
