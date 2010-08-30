package pds.core.base.messagingtargets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.server.EndpointRegistry;
import org.eclipse.higgins.xdi4j.messaging.server.impl.ResourceHandler;
import org.eclipse.higgins.xdi4j.messaging.server.impl.ResourceMessagingTarget;
import org.eclipse.higgins.xdi4j.messaging.server.interceptor.impl.ReadOnlyAddressInterceptor;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.base.PdsInstance;

public class PdsResourceMessagingTarget extends ResourceMessagingTarget {

	private PdsInstance pdsInstance;

	public PdsResourceMessagingTarget() {

		super(true);
	}

	@Override
	public void init(EndpointRegistry endpointRegistry) throws Exception {

		super.init(endpointRegistry);

		// add a ReadOnlyAddressInterceptor

		List<XRI3> readOnlyAddresses = new ArrayList<XRI3> ();

		XRI3Segment canonical = this.pdsInstance.getCanonical();
		readOnlyAddresses.add(new XRI3(canonical + "/$is"));
		readOnlyAddresses.add(new XRI3(canonical + "/$$is"));
		readOnlyAddresses.add(new XRI3(canonical + "/$is$a"));

		ReadOnlyAddressInterceptor readOnlyAddressInterceptor = new ReadOnlyAddressInterceptor();
		readOnlyAddressInterceptor.setReadOnlyAddresses(readOnlyAddresses.toArray(new XRI3[readOnlyAddresses.size()]));
		this.getAddressInterceptors().add(readOnlyAddressInterceptor);
	}

	@Override
	public ResourceHandler getResource(Message message, Subject operationSubject) throws MessagingException {

		for (XRI3Segment alias : this.pdsInstance.getAliases()) {

			if (operationSubject.getSubjectXri().equals(alias)) {

				return new PdsSubjectResourceHandler(message, operationSubject, this.pdsInstance);
			}
		}

		return null;
	}

	public PdsInstance getPdsInstance() {

		return this.pdsInstance;
	}

	public void setPdsInstance(PdsInstance pdsInstance) {

		this.pdsInstance = pdsInstance;
	}
}
