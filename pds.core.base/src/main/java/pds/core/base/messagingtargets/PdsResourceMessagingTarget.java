package pds.core.base.messagingtargets;

import java.util.ArrayList;
import java.util.List;

import pds.core.base.PdsInstance;
import xdi2.core.ContextNode;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.xri3.impl.XRI3;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Operation;
import xdi2.messaging.target.impl.ResourceHandler;
import xdi2.messaging.target.impl.ResourceMessagingTarget;

public class PdsResourceMessagingTarget extends ResourceMessagingTarget {

	private PdsInstance pdsInstance;

	public PdsResourceMessagingTarget() {

		super();
	}

	@Override
	public void init() throws Exception {

		super.init();

		// add a ReadOnlyAddressInterceptor

		List<XRI3> readOnlyAddresses = new ArrayList<XRI3> ();

		XRI3Segment canonical = this.pdsInstance.getCanonical();
		readOnlyAddresses.add(new XRI3(canonical + "/$is"));
		readOnlyAddresses.add(new XRI3(canonical + "/$$is"));
		readOnlyAddresses.add(new XRI3(canonical + "/$is$a"));

/*	TODO	ReadOnlyAddressInterceptor readOnlyAddressInterceptor = new ReadOnlyAddressInterceptor();
		readOnlyAddressInterceptor.setReadOnlyAddresses(readOnlyAddresses.toArray(new XRI3[readOnlyAddresses.size()]));
		this.getAddressInterceptors().add(readOnlyAddressInterceptor);*/
	}

	@Override
	public ResourceHandler getResourceHandler(Operation operation, ContextNode operationContextNode) throws Xdi2MessagingException {

		for (XRI3Segment alias : this.pdsInstance.getAliases()) {

			if (operationContextNode.getXri().equals(alias)) {

				return new PdsSubjectResourceHandler(operation, operationContextNode, this.pdsInstance);
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
