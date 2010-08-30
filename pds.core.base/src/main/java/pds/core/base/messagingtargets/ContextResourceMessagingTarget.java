package pds.core.base.messagingtargets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.server.EndpointRegistry;
import org.eclipse.higgins.xdi4j.messaging.server.impl.ResourceHandler;
import org.eclipse.higgins.xdi4j.messaging.server.impl.ResourceMessagingTarget;
import org.eclipse.higgins.xdi4j.messaging.server.interceptor.impl.ReadOnlyAddressInterceptor;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;

import pds.core.base.PdsInstance;

public class ContextResourceMessagingTarget extends ResourceMessagingTarget {

	private PdsInstance pdsInstance;

	public ContextResourceMessagingTarget() {

		super(true);
	}

	@Override
	public void init(EndpointRegistry endpointRegistry) throws Exception {

		super.init(endpointRegistry);

		// add a ReadOnlyAddressInterceptor

		List<XRI3> readOnlyAddresses = new ArrayList<XRI3> ();

		readOnlyAddresses.add(new XRI3("$/$is"));
		readOnlyAddresses.add(new XRI3("$/$$is"));
		readOnlyAddresses.add(new XRI3("$/$is$a"));
		readOnlyAddresses.add(new XRI3("$/$is($xdi$v$1)"));

		ReadOnlyAddressInterceptor readOnlyAddressInterceptor = new ReadOnlyAddressInterceptor();
		readOnlyAddressInterceptor.setReadOnlyAddresses(readOnlyAddresses.toArray(new XRI3[readOnlyAddresses.size()]));
		this.getAddressInterceptors().add(readOnlyAddressInterceptor);
	}

	@Override
	public ResourceHandler getResource(Message message, Graph operationGraph) throws MessagingException {

		return new ContextGraphResourceHandler(message, operationGraph, this.pdsInstance);
	}

	public PdsInstance getPdsInstance() {

		return this.pdsInstance;
	}

	public void setPdsInstance(PdsInstance pdsInstance) {

		this.pdsInstance = pdsInstance;
	}
}
