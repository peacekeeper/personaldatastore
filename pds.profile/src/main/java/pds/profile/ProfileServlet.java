package pds.profile;

import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxri.resolve.Resolver;

public class ProfileServlet extends HttpServlet {

	private static final long serialVersionUID = 9135016266076360503L;

	private static final Log log = LogFactory.getLog(ProfileServlet.class.getName());

	private static final Resolver resolver;

	static {

		try {

			resolver = new Resolver(null);
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize resolver: " + ex.getMessage(), ex);
		}
	}
/*
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
		String inumber = resolveXriToInumber(xri);
		String uri = resolveInumberToUri(inumber);
		Iterator<Subject> subjects = fetch(inumber, uri);

		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("atom_1.0");

		feed.setTitle(xri);
		feed.setLink("http://xri.net/" + xri);
		feed.setDescription("Feed for " + xri);

		List<org.jdom.Element> foreignElements = new ArrayList<org.jdom.Element> ();
		org.jdom.Element element;
		element = new org.jdom.Element("link");
		element.setAttribute("href", "http://pubsubhubbub.appspot.com/");
		element.setAttribute("rel", "hub");
		foreignElements.add(element);
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
	}*/
}
