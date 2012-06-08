package pds.endpoint.pubsubhubbub;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.higgins.XDI2.Graph;
import org.eclipse.higgins.XDI2.Literal;
import org.eclipse.higgins.XDI2.Predicate;
import org.eclipse.higgins.XDI2.Subject;
import org.eclipse.higgins.XDI2.constants.MessagingConstants;
import org.eclipse.higgins.XDI2.messaging.Message;
import org.eclipse.higgins.XDI2.messaging.MessageResult;
import org.eclipse.higgins.XDI2.messaging.Operation;
import org.eclipse.higgins.XDI2.xri3.impl.XRI3Segment;
import org.openxri.resolve.Resolver;
import org.springframework.web.HttpRequestHandler;

import pds.dictionary.feed.FeedDictionary;
import pds.xdi.XdiClient;
import pds.xdi.XdiEndpoint;
import pds.xdi.XdiException;

import com.cliqset.abdera.ext.activity.ActivityEntry;
import com.cliqset.abdera.ext.activity.ActivityExtensionFactory;
import com.cliqset.abdera.ext.serviceprovider.ServiceProviderExtensionFactory;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class PuSHServlet implements HttpRequestHandler {	 

	private static final long serialVersionUID = -1912598515775509417L;

	private static final Logger log = LoggerFactory.getLogger(PuSHServlet.class.getName());

	private static final XdiClient xdi;
	private static final Abdera abdera;

	static {

		try {

			xdi = new XdiClient(new Resolver(null));
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize XDI: " + ex.getMessage(), ex);
		}

		abdera = new Abdera();
		abdera.getFactory().registerExtension(new ActivityExtensionFactory());
		abdera.getFactory().registerExtension(new ServiceProviderExtensionFactory());
	}

	public void init() throws Exception {

	}

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		log.trace(request.getMethod() + ": " + request.getRequestURI() + ", Content-Type: " + request.getContentType() + ", Content-Length: " + request.getContentLength());

		try {

			if ("GET".equals(request.getMethod())) this.doGet(request, response);
			else if ("POST".equals(request.getMethod())) this.doPost(request, response);
			else response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	private void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String hubmode = request.getParameter("hub.mode");
		String hubtopic = request.getParameter("hub.topic");
		String hubchallenge = request.getParameter("hub.challenge");
		String hubverifytoken = request.getParameter("hub.verify_token");

		// check parameters

		if ((! "subscribe".equals(hubmode)) && (! "unsubscribe".equals(hubmode))) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid hub.mode parameter: " + hubmode);
			return;
		}

		if (hubtopic == null || hubtopic.length() < 1) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing hub.topic parameter.");
			return;
		}

		if (hubchallenge == null || hubchallenge.length() < 1) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing hub.challenge parameter.");
			return;
		}

		// find the XDI data

		String xri = this.parseXri(request);
		XdiEndpoint context = this.getContext(xri);
		Subject pdsSubject = context == null ? null : this.fetch(context, hubtopic);

		if (pdsSubject == null) {

			response.sendError(HttpServletResponse.SC_NOT_FOUND, xri + " not found.");
			return;
		}

		// check if the hub.verifytoken is correct for the hub.topic

		boolean topicVerifyTokenCorrect = this.isTopicVerifyTokenCorrect(pdsSubject, hubverifytoken);
		log.debug("topicVerifyTokenCorrect(" + hubtopic + "," + hubverifytoken + "): " + Boolean.toString(topicVerifyTokenCorrect));

		if (! topicVerifyTokenCorrect) {

			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		// subscribe / unsubscribe

		if (hubmode.equals("subscribe")){

			subscribeTopic(context, pdsSubject);
		} else if (hubmode.equals("unsubscribe")){

			unsubscribeTopic(context, pdsSubject);
		}

		// done

		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().print(hubchallenge);
		response.getWriter().flush();
		response.getWriter().close();
	}

	private void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// receive an atom or an RSS feed

		if (request.getContentType().contains("application/atom+xml")) this.doPostAtom(request, response);
		else if (request.getContentType().contains("application/rss+xml")) this.doPostRss(request, response);
		else response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Need application/atom+xml or application/rss+xml");
	}

	private void doPostAtom(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// create the Feed object

		Reader reader = request.getReader();
		Parser parser = abdera.getParser();
		Document<Element> document = parser.parse(reader);
		Feed feed = (Feed) document.getRoot();

		String hubtopic = null;
		if (hubtopic == null && feed.getSelfLink() != null) hubtopic = feed.getSelfLink().getHref().toString();
		if (hubtopic == null && feed.getId() != null) hubtopic = feed.getId().toString();

		// find the XDI data

		String xri = this.parseXri(request);
		XdiEndpoint context = this.getContext(xri);
		Subject pdsSubject = context == null ? null : this.fetch(context, hubtopic);

		if (pdsSubject == null) {

			response.sendError(HttpServletResponse.SC_NOT_FOUND, xri + " not found.");
			return;
		}

		// add entries to the topic

		addEntries(context, pdsSubject, feed);

		// done

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@SuppressWarnings("unchecked")
	private void doPostRss(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// create the new SyndFeed object

		InputStream stream = request.getInputStream();
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(stream));
		List<SyndLinkImpl> linkList = (List<SyndLinkImpl>) feed.getLinks();

		String hubtopic = null;

		for (SyndLinkImpl link : linkList) {

			if (link.getRel().equals("self")) hubtopic = link.getHref().toString();
		}

		if (hubtopic == null) hubtopic = feed.getUri();

		// find the XDI data

		String xri = this.parseXri(request);
		XdiEndpoint context = this.getContext(xri);
		Subject pdsSubject = context == null ? null : this.fetch(context, hubtopic);

		if (pdsSubject == null) {

			response.sendError(HttpServletResponse.SC_NOT_FOUND, xri + " not found.");
			return;
		}

		// add entries to the topic

		addEntries(context, pdsSubject, feed);

		// done

		response.setStatus(HttpServletResponse.SC_OK);
	}

	private boolean isTopicVerifyTokenCorrect(Subject pdsSubject, String hubverifytoken) {

		Predicate predicate = pdsSubject.getPredicate(FeedDictionary.XRI_VERIFYTOKEN);
		if (predicate == null) return false;

		Literal literal = predicate.getLiteral();
		if (literal == null) return false;

		return hubverifytoken.equals(literal.getData());
	}

	private static void subscribeTopic(XdiEndpoint context, Subject pdsSubject) throws Exception {

		log.debug("Subscribing to topic " + pdsSubject.getSubjectXri());

		// $set and $del

		Message message = context.prepareMessage();
		Operation operation = message.createOperation(MessagingConstants.XRI_SET);
		Graph operationGraph = operation.createOperationGraph(null);
		Graph topicsGraph = operationGraph.createStatement(context.getCanonical(), FeedDictionary.XRI_TOPICS, (Graph) null).getInnerGraph();
		topicsGraph.createStatement(pdsSubject.getSubjectXri(), FeedDictionary.XRI_SUBSCRIBED, "true");
		Operation operation2 = message.createOperation(MessagingConstants.XRI_DEL);
		Graph operationGraph2 = operation2.createOperationGraph(null);
		Graph topicsGraph2 = operationGraph2.createStatement(context.getCanonical(), FeedDictionary.XRI_TOPICS, (Graph) null).getInnerGraph();
		topicsGraph2.createStatement(pdsSubject.getSubjectXri(), FeedDictionary.XRI_VERIFYTOKEN);

		context.send(message);
	}

	private static void unsubscribeTopic(XdiEndpoint context, Subject pdsSubject) throws Exception {

		log.debug("Unsubscribing from topic " + pdsSubject.getSubjectXri());

		// $set and $del

		Message message = context.prepareMessage();
		Operation operation = message.createOperation(MessagingConstants.XRI_SET);
		Graph operationGraph = operation.createOperationGraph(null);
		Graph topicsGraph = operationGraph.createStatement(context.getCanonical(), FeedDictionary.XRI_TOPICS, (Graph) null).getInnerGraph();
		topicsGraph.createStatement(pdsSubject.getSubjectXri(), FeedDictionary.XRI_SUBSCRIBED, "false");
		Operation operation2 = message.createOperation(MessagingConstants.XRI_DEL);
		Graph operationGraph2 = operation2.createOperationGraph(null);
		Graph topicsGraph2 = operationGraph2.createStatement(context.getCanonical(), FeedDictionary.XRI_TOPICS, (Graph) null).getInnerGraph();
		topicsGraph2.createStatement(pdsSubject.getSubjectXri(), FeedDictionary.XRI_VERIFYTOKEN);

		context.send(message);
	}

	private static void addEntries(XdiEndpoint context, Subject pdsSubject, Feed feed) throws Exception {

		log.debug("Adding entries to topic " + pdsSubject.getSubjectXri());

		List<Entry> entries = feed.getEntries();

		// $add

		Operation operation = context.prepareOperation(MessagingConstants.XRI_ADD);
		Graph operationGraph = operation.createOperationGraph(null);
		Graph topicsGraph = operationGraph.createStatement(context.getCanonical(), FeedDictionary.XRI_TOPICS, (Graph) null).getInnerGraph();
		Graph entriesGraph = topicsGraph.createStatement(pdsSubject.getSubjectXri(), FeedDictionary.XRI_ENTRIES, (Graph) null).getInnerGraph();

		int i = 1;

		for (Entry entry : entries) {

			Subject subject = entriesGraph.createSubject(new XRI3Segment(FeedDictionary.XRI_ENTRY + "$($" + i + ")"));
			FeedDictionary.fromEntry(subject, new ActivityEntry(entry));

			i++;
		}

		context.send(operation);
	}

	private static void addEntries(XdiEndpoint context, Subject pdsSubject, SyndFeed syndFeed) throws Exception {

		throw new RuntimeException("Sorry, RSS is not currently supported.");
	}

	private String parseXri(HttpServletRequest request) throws Exception {

		String xri = request.getRequestURI();
		while (xri.length() > 0 && xri.contains("/")) xri = xri.substring(xri.indexOf("/") + 1);

		log.debug("Got request for XRI " + xri);
		return xri;
	}

	private XdiEndpoint getContext(String xri) throws XdiException {

		return xdi.resolveContextByIname(xri, null);
	}

	private Subject fetch(XdiEndpoint context, String hubtopic) throws Exception {

		Operation operation = context.prepareOperation(MessagingConstants.XRI_GET);
		Graph operationGraph = operation.createOperationGraph(null);
		Graph topicsGraph = operationGraph.createStatement(context.getCanonical(), FeedDictionary.XRI_TOPICS, (Graph) null).getInnerGraph();
		topicsGraph.createSubject(new XRI3Segment("$(" + hubtopic + ")"));

		MessageResult messageResult = context.send(operation);

		Subject subject = messageResult.getGraph().getSubject(context.getCanonical());
		if (subject == null) return null;

		Predicate predicate = subject.getPredicate(FeedDictionary.XRI_TOPICS);
		if (predicate == null) return null;

		Graph innerGraph = predicate.getInnerGraph();
		if (innerGraph == null) return null;

		Subject innerSubject = innerGraph.getSubject(new XRI3Segment("$(" + hubtopic + ")"));
		if (innerSubject == null) return null;

		return innerSubject;
	}
}	