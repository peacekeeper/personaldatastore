package pds.core.xri;

import java.util.Properties;

import javax.servlet.FilterConfig;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.PdsConnection;
import pds.core.PdsConnectionException;
import pds.core.PdsConnectionFactory;
import pds.store.xri.Xri;

public class XriPdsConnectionFactory implements PdsConnectionFactory {

	private Properties properties;
	private pds.store.xri.XriStore xriStore;

	@Override
	public void init(FilterConfig filterConfig) throws PdsConnectionException {

	}

	public PdsConnection getPdsConnection(String identifier) throws PdsConnectionException {

		Xri xri = null;
		XRI3Segment inumber = null;
		String userIdentifier = null;
		User user = null;

		// find xri and user

		try {

			xri = this.xriStore.findXri(identifier);
			if (xri != null && xri.getCanonicalID() != null) inumber = new XRI3Segment(xri.getCanonicalID().getValue());
			if (xri != null) userIdentifier = xri.getUserIdentifier();
			if (userIdentifier != null) user = this.ibrokerStore.findUser(userIdentifier);
			if (inumber == null || user == null) return null;
		} catch (Exception ex) {

			throw new PdsConnectionException("Cannot find xri and/user user: " + ex.getMessage(), ex);
		}

		// done

		return new XriPdsConnection(this, xri, user);
	}

	Properties getProperties() {

		return this.properties;
	}

	void setProperties(Properties properties) {

		this.properties = properties;
	}

	pds.store.xri.XriStore getXriStore() {

		return this.xriStore;
	}

	void setXriStore(pds.store.xri.XriStore xriStore) {

		this.xriStore = xriStore;
	}
}
