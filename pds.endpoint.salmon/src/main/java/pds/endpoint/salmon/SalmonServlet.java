package pds.endpoint.salmon;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.openxri.resolve.Resolver;
import org.springframework.web.HttpRequestHandler;

import pds.dictionary.feed.FeedDictionary;
import pds.discovery.xrd.LRDDOpenXRDKeyFinder;
import pds.xdi.Xdi;
import pds.xdi.XdiContext;
import pds.xdi.XdiException;

import com.cliqset.salmon.MagicEnvelope;
import com.cliqset.salmon.MagicSigUtil;
import com.cliqset.salmon.Salmon;
import com.cliqset.salmon.dataparser.AbderaDataParser;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

public class SalmonServlet implements HttpRequestHandler {	 

	private static final long serialVersionUID = -1912598515775509417L;

//	private static final XRI3Segment XRI_FEED = new XRI3Segment("+ostatus+feed");
	private static final XRI3Segment XRI_MENTIONS = new XRI3Segment("+ostatus+mentions");
	//	private static final XRI3Segment XRI_TOPICS = new XRI3Segment("+ostatus+topics");
	//	private static final XRI3Segment XRI_ENTRIES = new XRI3Segment("+entries");
	private static final XRI3Segment XRI_ENTRY = new XRI3Segment("+entry");

	private static final Log log = LogFactory.getLog(SalmonServlet.class.getName());

	private static final Xdi xdi;
	private static final Salmon salmon;

	static {

		try {

			xdi = new Xdi(new Resolver(null));
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize XDI: " + ex.getMessage(), ex);
		}

		try {

			salmon = new Salmon().withKeyFinder(new LRDDOpenXRDKeyFinder()).withDataParser(new AbderaDataParser());
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize Salmon: " + ex.getMessage(), ex);
		}
	}

	public void init() throws Exception {

	}

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		log.trace(request.getMethod() + ": " + request.getRequestURI() + ", Content-Type: " + request.getContentType() + ", Content-Length: " + request.getContentLength());

		try {

			if ("POST".equals(request.getMethod())) this.doPost(request, response);
			else response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// receive a magic signature envelope

		if (! request.getContentType().contains("application/magic-envelope+xml")) {

			response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
			return;
		}

		// verify and decode the salmon

		byte[] post = new byte[request.getContentLength()];
		new DataInputStream(request.getInputStream()).readFully(post);
		log.debug("POST data: " + new String(post));
		
		byte[] data;

		try {

			MagicEnvelope magicEnvelope = MagicEnvelope.fromInputStream(new ByteArrayInputStream(post));
			//data = MagicSigUtil.decode(magicEnvelope.getData().getValue());
			data = salmon.verify(magicEnvelope);
		} catch (Exception ex) {

			log.warn("Cannot verify Salmon: " + ex.getMessage(), ex);
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		// create the new SyndEntry object

		InputStream stream = new ByteArrayInputStream(data);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
		String buffer, line;
		buffer = "<feed xml:lang=\"en-US\" xmlns=\"http://www.w3.org/2005/Atom\" xmlns:thr=\"http://purl.org/syndication/thread/1.0\" xmlns:georss=\"http://www.georss.org/georss\" xmlns:activity=\"http://activitystrea.ms/spec/1.0/\" xmlns:media=\"http://purl.org/syndication/atommedia\" xmlns:poco=\"http://portablecontacts.net/spec/1.0\" xmlns:ostatus=\"http://ostatus.org/schema/1.0\" xmlns:statusnet=\"http://status.net/schema/api/1/\">";
		while ((line = reader.readLine()) != null) buffer += line.replaceAll("<\\?.*\\?>", "");
		buffer += "</feed>";
		log.debug("Assembled Salmon feed: " + buffer.toString());

		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new StringReader(buffer));

		List<SyndEntry> entries = (List<SyndEntry>) feed.getEntries();
		if (entries.size() < 1) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No feed entries.");
			return;
		}

		log.debug("Found " + entries.size() + " feed entries in Salmon.");
		
		// find the XDI data

		String xri = this.parseXri(request);
		XdiContext context = this.getContext(xri);
		/*		Subject pdsSubject = context == null ? null : this.fetch(context, hubtopic);

		if (pdsSubject == null) {

			response.sendError(HttpServletResponse.SC_NOT_FOUND, xri + " not found.");
			return;
		}*/

		// add mentions

		// TODO: some feed entries may be replies to existing items!!!

		addMentions(context, feed);

		// done

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@SuppressWarnings("unchecked")
	private static void addMentions(XdiContext context, SyndFeed feed) throws Exception {

		log.debug("Adding mentions");

		List<SyndEntry> syndEntries = (List<SyndEntry>) feed.getEntries();

		// $add

		Operation operation = context.prepareOperation(MessagingConstants.XRI_ADD);
		Graph operationGraph = operation.createOperationGraph(null);
		Graph mentionsGraph = operationGraph.createStatement(context.getCanonical(), XRI_MENTIONS, (Graph) null).getInnerGraph();

		int i = 1;

		for (SyndEntry syndEntry : syndEntries) {

			Subject subject = mentionsGraph.createSubject(new XRI3Segment(XRI_ENTRY + "$($" + i + ")"));
			FeedDictionary.fromEntry(subject, syndEntry);

			i++;
		}

		context.send(operation);
	}

	/*	@SuppressWarnings("unchecked")
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
	}*/

	private String parseXri(HttpServletRequest request) throws Exception {

		String xri = request.getRequestURI();
		while (xri.length() > 0 && xri.contains("/")) xri = xri.substring(xri.indexOf("/") + 1);

		log.debug("Got request for XRI " + xri);
		return xri;
	}

	private XdiContext getContext(String xri) throws XdiException {

		return xdi.resolveContextByIname(xri, null);
	}

	/*	private Subject fetch(XdiContext context, String hubtopic) throws Exception {

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
	}*/

	/*	private Subject fetch(XdiContext context, String hubtopic) throws Exception {

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
	}*/
}	