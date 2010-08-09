package pds.feed;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.addressing.Addressing;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.MessageEnvelope;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.messaging.client.http.XDIHttpClient;
import org.eclipse.higgins.xdi4j.types.Timestamps;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.discovery.Discovery;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;

public class FeedServlet extends HttpServlet {

	private static final long serialVersionUID = 9135016266076360503L;
	private static final String INITPARAMETER_KEY_FEEDTYPE = "feedtype";
	private static final String DEFAULT_FEEDTYPE = "atom_1.0";

	private static final Log log = LogFactory.getLog(FeedServlet.class.getName());

	private static final Discovery discovery;

	private String feedType;

	static {

		try {

			discovery = new Discovery();
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize discovery: " + ex.getMessage(), ex);
		}
	}

	private static String parseXri(HttpServletRequest request) throws Exception {

		String xri = request.getRequestURI();
		while (xri.length() > 0 && xri.contains("/")) xri = xri.substring(xri.indexOf("/") + 1);

		log.info("Got request for XRI " + xri);
		return xri;
	}

	private static Iterator<Subject> fetch(String inumber, String uri) throws Exception {

		MessageEnvelope messageEnvelope = MessageEnvelope.newInstance();
		Message message = messageEnvelope.newMessage(MessagingConstants.XRI_ANONYMOUS);
		Operation operation = message.createGetOperation();
		Graph operationGraph = operation.createOperationGraph(null);
		operationGraph.createStatement(new XRI3Segment(inumber), new XRI3Segment("+feed"));

		MessageResult messageResult = MessageResult.newInstance();

		new XDIHttpClient(uri).send(messageEnvelope, messageResult);

		Subject subject = messageResult.getGraph().getSubject(new XRI3Segment(inumber));
		if (subject == null) throw new RuntimeException("User " + inumber + " not found.");

		Predicate predicate = subject.getPredicate(new XRI3Segment("+feed"));
		if (predicate == null) throw new RuntimeException("Feed not found.");

		Graph innerGraph = predicate.getInnerGraph();
		if (innerGraph == null) throw new RuntimeException("Feed not found.");

		return innerGraph.getSubjects();
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {

		super.init(servletConfig);

		this.feedType = servletConfig.getInitParameter(INITPARAMETER_KEY_FEEDTYPE);
		if (this.feedType == null) this.feedType = DEFAULT_FEEDTYPE;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {

			SyndFeed feed = this.getFeed(request, response);

			Writer writer = response.getWriter();
			SyndFeedOutput output = new SyndFeedOutput();
			output.output(feed, writer);
			writer.close();
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	private SyndFeed getFeed(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String xri = parseXri(request);
		String inumber = discovery.resolveXriToInumber(xri);
		String uri = discovery.resolveInumberToUri(inumber);
		Iterator<Subject> subjects = fetch(inumber, uri);

		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType(this.feedType);

		feed.setTitle(xri);
		feed.setLink("http://xri.net/" + xri);
		feed.setDescription("Feed for " + xri);

		List<org.jdom.Element> foreignElements = new ArrayList<org.jdom.Element> ();
		foreignElements.add(makeLinkElement("hub", "http://pubsubhubbub.appspot.com/", null, null));
		foreignElements.add(makeLinkElement("alternate", "http://xri.net/" + inumber + "/", "text/html", null));
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
}
