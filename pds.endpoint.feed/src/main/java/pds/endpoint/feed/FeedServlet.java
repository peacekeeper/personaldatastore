package pds.endpoint.feed;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
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
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.util.iterators.MappingIterator;
import org.eclipse.higgins.xdi4j.util.iterators.NotNullIterator;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.openxri.resolve.Resolver;
import org.springframework.web.HttpRequestHandler;

import pds.dictionary.feed.FeedDictionary;
import pds.xdi.Xdi;
import pds.xdi.XdiContext;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;

public class FeedServlet implements HttpRequestHandler {

	private static final long serialVersionUID = 9135016266076360503L;

	private static final XRI3Segment XRI_FEED = new XRI3Segment("+ostatus+feed");

	private static final Log log = LogFactory.getLog(FeedServlet.class.getName());

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
		Iterator<Subject> pdsSubjects = context == null ? null : this.fetch(context);

		if (pdsSubjects == null) {

			response.sendError(HttpServletResponse.SC_NOT_FOUND, xri + " not found.");
			return;
		}

		SyndFeed feed = this.convertFeed(xri, context, pdsSubjects);

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

	private Iterator<Subject> fetch(XdiContext context) throws Exception {

		Operation operation = context.prepareOperation(MessagingConstants.XRI_GET);
		Graph operationGraph = operation.createOperationGraph(null);
		operationGraph.createStatement(context.getCanonical(), XRI_FEED);

		MessageResult messageResult = context.send(operation);

		Subject subject = messageResult.getGraph().getSubject(context.getCanonical());
		if (subject == null) return null;

		Predicate predicate = subject.getPredicate(XRI_FEED);
		if (predicate == null) return null;

		Graph innerGraph = predicate.getInnerGraph();
		if (innerGraph == null) return null;

		Subject innerSubject = innerGraph.getSubject(new XRI3Segment("$"));
		if (innerSubject == null) return null;

		return new NotNullIterator<Subject> (new MappingIterator<Predicate, Subject> (innerSubject.getPredicates()) {

			@Override
			public Subject map(Predicate predicate) {

				Graph graph = predicate.getInnerGraph();
				if (graph == null) return null;

				return graph.getSubject(new XRI3Segment("$"));
			}
		});
	}

	private SyndFeed convertFeed(String xri, XdiContext context, Iterator<Subject> pdsSubjects) throws Exception {

		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType(this.format);

		feed.setTitle(xri);
		feed.setLink("http://xri2xrd.net/" + context.getCanonical());
		feed.setDescription("Feed for " + xri);

		String salmonEndpoint = this.salmonEndpoint + context.getCanonical();
		String selfEndpoint = this.selfEndpoint + context.getCanonical();

		List<org.jdom.Element> foreignElements = new ArrayList<org.jdom.Element> ();
		foreignElements.add(makeLinkElement("alternate", "http://xri2xrd.net/" + context.getCanonical() + "/", "text/html", null));
		foreignElements.add(makeLinkElement("hub", this.hub, null, "PubSubHubbub"));
		foreignElements.add(makeLinkElement("salmon-reply", salmonEndpoint, null, "Salmon Replies"));
		foreignElements.add(makeLinkElement("salmon-mention", salmonEndpoint, null, "Salmon Mentions"));
		foreignElements.add(makeLinkElement("self", selfEndpoint, this.contentType, null));
		feed.setForeignMarkup(foreignElements);

		List<SyndEntry> entries = new ArrayList<SyndEntry> ();

		while (pdsSubjects.hasNext()) {

			Subject subject = pdsSubjects.next();

			try {

				SyndEntry entry = FeedDictionary.fromSubject(subject);
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

	public String getFormat() {

		return this.format;
	}

	public void setFormat(String format) {

		this.format = format;

		if ("rss_2.0".equals(format)) this.contentType = "application/rdf+xml";
		if ("atom_1.0".equals(format)) this.contentType = "application/rdf+xml";
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
