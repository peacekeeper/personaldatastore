package pds.core.base.messagingtargets;


import java.util.ArrayList;
import java.util.List;

import pds.core.base.PdsInstance;
import pds.core.base.messagingtargets.handlers.PdsContextNodeHandler;
import pds.core.base.messagingtargets.handlers.PdsRootContextNodeHandler;
import xdi2.core.ContextNode;
import xdi2.core.xri3.impl.XRI3;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.AbstractMessagingTarget;

public class PdsMessagingTarget extends AbstractMessagingTarget {

	private PdsInstance pdsInstance;

	public PdsMessagingTarget() {

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
	public ContextNodeHandler getContextNodeHandler(ContextNode operationContextNode) throws Xdi2MessagingException {

		if (operationContextNode.isRootContextNode()) {

			return new PdsRootContextNodeHandler(this.pdsInstance);
		}

		for (XRI3Segment alias : this.pdsInstance.getAliases()) {

			if (operationContextNode.getXri().equals(alias)) {

				return new PdsContextNodeHandler(this.pdsInstance);
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
