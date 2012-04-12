package pds.p2p.node.webshell.webapplication;

import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.PageProvider;
import org.apache.wicket.request.handler.RenderPageRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.node.webshell.webpages.error.ExceptionPage;

public class WebShellRequestCycleListener extends AbstractRequestCycleListener implements IRequestCycleListener {

	private static Logger log = LoggerFactory.getLogger(WebShellRequestCycleListener.class.getName());

	@Override
	public IRequestHandler onException(RequestCycle requestCycle, Exception ex) {
		
		// let wicket handle its own stuff

		if (ex instanceof UnauthorizedInstantiationException || ex instanceof PageExpiredException) return null;

		// log and display the exception using our exception page

		log.error("Exception during request cycle: " + ex.getMessage(), ex);

		ExceptionPage page = new ExceptionPage(requestCycle, ex);
		
		// done
		
		return new RenderPageRequestHandler(new PageProvider(page));
	}
}
