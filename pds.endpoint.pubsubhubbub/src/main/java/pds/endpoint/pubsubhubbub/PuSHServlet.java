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
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.openxri.resolve.Resolver;
import org.springframework.web.HttpRequestHandler;

import pds.xdi.Xdi;
import pds.xdi.XdiContext;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class PuSHServlet implements HttpRequestHandler {	 

	private static final long serialVersionUID = -1912598515775509417L;

	private static final XRI3Segment XRI_VERIFYTOKEN = new XRI3Segment("+verify.token");
	private static final XRI3Segment XRI_SUBSCRIBED = new XRI3Segment("+subscribed");

	private static final Log log = LogFactory.getLog(PuSHServlet.class.getName());

	private static final Xdi xdi;

	private String endpoint;

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

		// find the context

		String xri = this.parseXri(request);
		XdiContext context = this.getContext(xri);
		Subject subject = this.fetch(context, hubtopic);

		// check if the hub.verifytoken is correct for the hub.topic

		boolean channelVerifyTokenCorrect = this.isChannelVerifyTokenCorrect(subject, hubverifytoken);
		log.debug("channelVerifyTokenCorrect(" + hubtopic + "," + hubverifytoken + "): " + Boolean.toString(channelVerifyTokenCorrect));

		if (! channelVerifyTokenCorrect) {

			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// subscribe / unsubscribe

		if (hubmode.equals("subscribe")){

			this.subscribeChannel(context, subject);
		} else if (hubmode.equals("unsubscribe")){

			this.unsubscribeChannel(context, subject);
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

		// check if the channel exist. If so, add feeds to the channel

		updateChannel(hubtopic, feed);

		// done

		response.setStatus(HttpServletResponse.SC_OK);
	}

	private boolean isChannelVerifyTokenCorrect(Subject subject, String hubverifytoken) {

		Predicate predicate = subject.getPredicate(XRI_VERIFYTOKEN);
		if (predicate == null) return false;

		Literal literal = predicate.getLiteral();
		if (literal == null) return false;

		return hubverifytoken.equals(literal.getData());
	}

	private void subscribeChannel(XdiContext context, Subject subject) throws Exception {

		Operation operation = context.prepareOperation(MessagingConstants.XRI_SET);
		Graph operationGraph = operation.createOperationGraph(null);
		Graph channelsGraph = operationGraph.createStatement(context.getCanonical(), new XRI3Segment("+channels"), (Graph) null).getInnerGraph();
		channelsGraph.createStatement(subject.getSubjectXri(), XRI_SUBSCRIBED, "true");

		context.send(operation);
	}

	private void unsubscribeChannel(XdiContext context, Subject subject) throws Exception {

		Operation operation = context.prepareOperation(MessagingConstants.XRI_SET);
		Graph operationGraph = operation.createOperationGraph(null);
		Graph channelsGraph = operationGraph.createStatement(context.getCanonical(), new XRI3Segment("+channels"), (Graph) null).getInnerGraph();
		channelsGraph.createStatement(subject.getSubjectXri(), XRI_SUBSCRIBED, "false");

		context.send(operation);
	}

	private void updateChannel(String hubtopic, SyndFeed feed){

		//checks if the channel is already created

		//NOTE! This code should be changed for your case//
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

	private Subject fetch(XdiContext context, String hubtopic) throws Exception {

		Operation operation = context.prepareOperation(MessagingConstants.XRI_GET);
		Graph operationGraph = operation.createOperationGraph(null);
		Graph channelsGraph = operationGraph.createStatement(context.getCanonical(), new XRI3Segment("+channels"), (Graph) null).getInnerGraph();
		channelsGraph.createSubject(new XRI3Segment("$(" + hubtopic + ")"));

		MessageResult messageResult = context.send(operation);

		Subject subject = messageResult.getGraph().getSubject(context.getCanonical());
		if (subject == null) throw new RuntimeException("User " + context.getCanonical() + " not found.");

		Predicate predicate = subject.getPredicate(new XRI3Segment("+channels"));
		if (predicate == null) throw new RuntimeException("Channels not found.");

		Graph innerGraph = predicate.getInnerGraph();
		if (innerGraph == null) throw new RuntimeException("Channels not found.");

		Subject innerSubject = innerGraph.getSubject(new XRI3Segment("$(" + hubtopic + ")"));

		return innerSubject;
	}

	public String getEndpoint() {

		return this.endpoint;
	}

	public void setEndpoint(String endpoint) {

		this.endpoint = endpoint;
	}
}	