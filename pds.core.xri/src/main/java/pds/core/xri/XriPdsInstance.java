package pds.core.xri;

import java.util.List;

import org.openxri.xml.CanonicalID;

import pds.core.base.PdsInstance;
import pds.core.base.impl.AbstractPdsInstance;
import pds.core.xri.messagingtargets.XriResourceMessagingTarget;
import pds.store.user.User;
import pds.store.xri.Xri;
import pds.store.xri.XriStoreException;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.target.impl.AbstractMessagingTarget;

public class XriPdsInstance extends AbstractPdsInstance implements PdsInstance {

	private XriPdsInstanceFactory pdsInstanceFactory;
	private Xri xri;
	private User user;
	private String[] endpoints;

	XriPdsInstance(String target, XriPdsInstanceFactory pdsInstanceFactory, Xri xri, User user, String[] endpoints) {

		super(target);

		this.pdsInstanceFactory = pdsInstanceFactory;
		this.xri = xri;
		this.user = user;
		this.endpoints = endpoints;
	}

	public XriPdsInstanceFactory getPdsInstanceFactory() {

		return this.pdsInstanceFactory;
	}

	@Override
	public XRI3Segment getCanonical() {

		CanonicalID canonicalID;

		try {

			canonicalID = this.xri.getCanonicalID();
		} catch (XriStoreException ex) {

			throw new RuntimeException(ex);
		}

		if (canonicalID == null) return null;

		String canonicalIDValue = canonicalID.getValue();
		if (canonicalIDValue == null) return null;

		return new XRI3Segment(canonicalIDValue);
	}

	public XRI3Segment[] getAliases() {

		List<String> aliases = this.xri.getAliases();
		XRI3Segment canonical = this.getCanonical();

		int size = aliases.size();
		if (canonical != null) size++;

		XRI3Segment[] xriAliases = new XRI3Segment[size];

		for (int i=0; i<aliases.size(); i++) xriAliases[i] = new XRI3Segment(aliases.get(i));
		if (canonical != null) xriAliases[size-1] = canonical;

		return xriAliases;
	}

	public String[] getEndpoints() {

		String[] endpoints = new String[this.endpoints.length];

		for (int i=0; i<endpoints.length; i++) {

			endpoints[i] = this.endpoints[i];
			if (! endpoints[i].endsWith("/")) endpoints[i] += "/";
			endpoints[i] += this.getCanonical().toString() + "/";
		}

		return endpoints;
	}

	public AbstractMessagingTarget[] getMessagingTargets() {

		XriResourceMessagingTarget pdsInstanceResourceMessagingTarget = new XriResourceMessagingTarget();
		pdsInstanceResourceMessagingTarget.setPdsInstance(this);

		return new AbstractMessagingTarget[] { pdsInstanceResourceMessagingTarget };
	}

	public Xri getXri() {

		return this.xri;
	}

	public User getUser() {

		return this.user;
	}
}
