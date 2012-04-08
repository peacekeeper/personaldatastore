package pds.p2p.node.webshell.webapplication;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.node.webshell.webpages.error.ExceptionPage;

public class WebShellRequestCycleListener extends AbstractRequestCycleListener implements IRequestCycleListener {

	private static Logger log = LoggerFactory.getLogger(WebShellRequestCycleListener.class.getName());

	@Override
	public IRequestHandler onException(RequestCycle requestCycle, Exception ex) {

		// let wicket handle its own stuff

		if (ex instanceof WicketRuntimeException) return null;

		// log and display the exception using our exception page

		log.error("Internal Error", ex);

		PageParameters parameters = new PageParameters();
		parameters.set("request", ex);
		parameters.set("ex", ex);

		ExceptionPage page = new ExceptionPage(requestCycle, ex);

		requestCycle.setResponsePage(page);
		
		// done
		
		return null;
	}
}
