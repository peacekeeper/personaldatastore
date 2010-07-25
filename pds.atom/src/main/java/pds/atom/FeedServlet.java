package pds.atom;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import org.openxri.XRI;
import org.openxri.resolve.Resolver;
import org.openxri.resolve.ResolverFlags;
import org.openxri.resolve.ResolverState;
import org.openxri.xml.Service;
import org.openxri.xml.XDIService;
import org.openxri.xml.XRD;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;

public class FeedServlet extends HttpServlet {

	private static final long serialVersionUID = 9135016266076360503L;

	private static final Log log = LogFactory.getLog(FeedServlet.class.getName());

	private static final String INAME = "=markus";

	private static final Resolver resolver;

	static {

		try {

			resolver = new Resolver(null);
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize resolver: " + ex.getMessage(), ex);
		}
	}

	private static String resolveInameToInumber(String iname) throws Exception {

		String inumber = null;

		ResolverFlags resolverFlags = new ResolverFlags();

		XRD xrd = resolver.resolveAuthToXRD(new XRI(iname), resolverFlags, new ResolverState());
		if (xrd.getCanonicalID() == null) return null;
		inumber = xrd.getCanonicalID().getValue();

		log.info("Resolved " + iname + " to " + inumber);
		return inumber;
	}

	private static String resolveInumberToUri(String inumber) throws Exception {

		String uri = null;

		ResolverFlags resolverFlags = new ResolverFlags();

		XRD xrd = resolver.resolveSEPToXRD(new XRI(inumber ), XDIService.SERVICE_TYPE, null, resolverFlags, new ResolverState());
		if (! xrd.getStatus().getCode().equals("100")) throw new RuntimeException("Resultion failed: " + xrd.getStatus().getCode());

		List<?> services = xrd.getSelectedServices().getList();

		for (Object service : services) {

			if (((Service) service).getNumURIs() > 0) uri = ((Service) service).getURIAt(0).getUriString();
		}
		if (uri != null && (! uri.endsWith("/"))) uri += "/";

		log.info("Resolved " + inumber + " to " + uri);
		return uri;
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

			this.doit(request, response);
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	private void doit(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String inumber = resolveInameToInumber(INAME);
		String uri = resolveInumberToUri(inumber);
		Iterator<Subject> subjects = fetch(inumber, uri);

		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("atom_1.0");

		feed.setTitle(INAME);
		feed.setLink("http://xri.net/" + INAME);
		feed.setDescription("Feed for " + INAME);

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
				entry.setLink("http://xri.net/" + INAME);
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

		Writer writer = response.getWriter();
		SyndFeedOutput output = new SyndFeedOutput();
		output.output(feed,writer);
		writer.close();
	}
}
