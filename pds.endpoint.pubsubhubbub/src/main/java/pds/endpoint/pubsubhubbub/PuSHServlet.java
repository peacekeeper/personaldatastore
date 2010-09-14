package pds.endpoint.pubsubhubbub;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.Literal;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.openxri.resolve.Resolver;
import org.springframework.web.HttpRequestHandler;

import pds.dictionary.feed.FeedDictionary;
import pds.xdi.Xdi;
import pds.xdi.XdiContext;
import pds.xdi.XdiException;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class PuSHServlet implements HttpRequestHandler {	 

	private static final long serialVersionUID = -1912598515775509417L;

	private static final XRI3Segment XRI_TOPICS = new XRI3Segment("+ostatus+topics");
	private static final XRI3Segment XRI_ENTRIES = new XRI3Segment("+entries");
	private static final XRI3Segment XRI_ENTRY = new XRI3Segment("+entry");
	private static final XRI3Segment XRI_VERIFYTOKEN = new XRI3Segment("+push+verify.token");
	private static final XRI3Segment XRI_SUBSCRIBED = new XRI3Segment("+push+subscribed");

	private static final Log log = LogFactory.getLog(PuSHServlet.class.getName());

	private static final Xdi xdi;

	static {

		try {

			xdi = new Xdi(new Resolver(null));
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize XDI: " + ex.getMessage(), ex);
		}
	}

	public void init() throws Exception {

	}

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		log.trace(request.getMethod() + ": " + request.getRequestURI());

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
		XdiContext context = this.getContext(xri);
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

	@SuppressWarnings("unchecked")
	private void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// receive an atom or an RSS feed

		if ((! request.getContentType().contains("application/atom+xml")) && 
				(! request.getContentType().contains("application/rss+xml"))) {

			response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
			return;
		}

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
		XdiContext context = this.getContext(xri);
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

		Predicate predicate = pdsSubject.getPredicate(XRI_VERIFYTOKEN);
		if (predicate == null) return false;

		Literal literal = predicate.getLiteral();
		if (literal == null) return false;

		return hubverifytoken.equals(literal.getData());
	}

	private static void subscribeTopic(XdiContext context, Subject pdsSubject) throws Exception {

		log.debug("Subscribing to topic " + pdsSubject.getSubjectXri());

		// $set and $del

		Message message = context.prepareMessage();
		Operation operation = message.createOperation(MessagingConstants.XRI_SET);
		Graph operationGraph = operation.createOperationGraph(null);
		Graph topicsGraph = operationGraph.createStatement(context.getCanonical(), XRI_TOPICS, (Graph) null).getInnerGraph();
		topicsGraph.createStatement(pdsSubject.getSubjectXri(), XRI_SUBSCRIBED, "true");
		Operation operation2 = message.createOperation(MessagingConstants.XRI_DEL);
		Graph operationGraph2 = operation2.createOperationGraph(null);
		Graph topicsGraph2 = operationGraph2.createStatement(context.getCanonical(), XRI_TOPICS, (Graph) null).getInnerGraph();
		topicsGraph2.createStatement(pdsSubject.getSubjectXri(), XRI_VERIFYTOKEN);

		context.send(message);
	}

	private static void unsubscribeTopic(XdiContext context, Subject pdsSubject) throws Exception {

		log.debug("Unsubscribing from topic " + pdsSubject.getSubjectXri());

		// $set and $del
		
		Message message = context.prepareMessage();
		Operation operation = message.createOperation(MessagingConstants.XRI_SET);
		Graph operationGraph = operation.createOperationGraph(null);
		Graph topicsGraph = operationGraph.createStatement(context.getCanonical(), XRI_TOPICS, (Graph) null).getInnerGraph();
		topicsGraph.createStatement(pdsSubject.getSubjectXri(), XRI_SUBSCRIBED, "false");
		Operation operation2 = message.createOperation(MessagingConstants.XRI_DEL);
		Graph operationGraph2 = operation2.createOperationGraph(null);
		Graph topicsGraph2 = operationGraph2.createStatement(context.getCanonical(), XRI_TOPICS, (Graph) null).getInnerGraph();
		topicsGraph2.createStatement(pdsSubject.getSubjectXri(), XRI_VERIFYTOKEN);

		context.send(message);
	}

	@SuppressWarnings("unchecked")
	private static void addEntries(XdiContext context, Subject pdsSubject, SyndFeed feed) throws Exception {

		log.debug("Adding entries to topic " + pdsSubject.getSubjectXri());

		List<SyndEntry> syndEntries = (List<SyndEntry>) feed.getEntries();

		// $add

		Operation operation = context.prepareOperation(MessagingConstants.XRI_ADD);
		Graph operationGraph = operation.createOperationGraph(null);
		Graph topicsGraph = operationGraph.createStatement(context.getCanonical(), XRI_TOPICS, (Graph) null).getInnerGraph();
		Graph entriesGraph = topicsGraph.createStatement(pdsSubject.getSubjectXri(), XRI_ENTRIES, (Graph) null).getInnerGraph();

		for (SyndEntry syndEntry : syndEntries) {

			Subject subject = entriesGraph.createSubject(new XRI3Segment(XRI_ENTRY + "$($)"));
			FeedDictionary.fromEntry(subject, syndEntry);
		}

		context.send(operation);
	}

	private String parseXri(HttpServletRequest request) throws Exception {

		String xri = request.getRequestURI();
		while (xri.length() > 0 && xri.contains("/")) xri = xri.substring(xri.indexOf("/") + 1);

		log.debug("Got request for XRI " + xri);
		return xri;
	}

	private XdiContext getContext(String xri) throws XdiException {

		return xdi.resolveContextByIname(xri, null);
	}

	private Subject fetch(XdiContext context, String hubtopic) throws Exception {

		Operation operation = context.prepareOperation(MessagingConstants.XRI_GET);
		Graph operationGraph = operation.createOperationGraph(null);
		Graph topicsGraph = operationGraph.createStatement(context.getCanonical(), XRI_TOPICS, (Graph) null).getInnerGraph();
		topicsGraph.createSubject(new XRI3Segment("$(" + hubtopic + ")"));

		MessageResult messageResult = context.send(operation);

		Subject subject = messageResult.getGraph().getSubject(context.getCanonical());
		if (subject == null) return null;

		Predicate predicate = subject.getPredicate(XRI_TOPICS);
		if (predicate == null) return null;

		Graph innerGraph = predicate.getInnerGraph();
		if (innerGraph == null) return null;

		Subject innerSubject = innerGraph.getSubject(new XRI3Segment("$(" + hubtopic + ")"));
		if (innerSubject == null) return null;

		return innerSubject;
	}
}	