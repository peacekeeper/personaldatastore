package pds.core.xri;

import ibrokerkit.epptools4java.EppTools;
import ibrokerkit.ibrokerstore.store.User;
import ibrokerkit.iname4java.store.Xri;
import ibrokerkit.iname4java.store.impl.grs.GrsXriStore;
import ibrokerkit.iname4java.store.impl.openxri.OpenxriXriStore;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import javax.servlet.FilterConfig;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.openxri.config.ServerConfig;
import org.openxri.factories.ServerConfigFactory;
import org.openxri.store.Store;

import pds.core.PdsConnection;
import pds.core.PdsConnectionException;
import pds.core.PdsConnectionFactory;

public class XriPdsConnectionFactory implements PdsConnectionFactory {

	private Properties properties;
	private ibrokerkit.iname4java.store.XriStore xriStore;
	private ibrokerkit.ibrokerstore.store.Store ibrokerStore;

	@Override
	public void init(FilterConfig filterConfig) throws PdsConnectionException {

		// load properties

		try {

			this.properties = new Properties();
			String propertiesFile = filterConfig.getServletContext().getRealPath("WEB-INF/application.properties");
			this.properties.load(new FileReader(propertiesFile));
		} catch (Exception ex) {

			throw new PdsConnectionException("Cannot read properties: " + ex.getMessage(), ex);
		}

		// load eppTools

		EppTools eppTools = null;

		try {

			Properties eppToolsProperties = new Properties();
			String eppToolsPropertiesFile = filterConfig.getServletContext().getRealPath("WEB-INF/epptools.properties");

			if (new File(eppToolsPropertiesFile).exists()) {

				eppToolsProperties.load(new FileReader(eppToolsPropertiesFile));

				eppTools = new EppTools(eppToolsProperties);
				eppTools.init();
			}
		} catch (Exception ex) {

			throw new PdsConnectionException("Cannot initialize eppTools: " + ex.getMessage(), ex);
		}

		// init OpenXRI ServletConfig and xriStore

		try {

			ServerConfig openxriServerConfig = ServerConfigFactory.initSingleton(filterConfig);
			if (eppTools != null)
				this.xriStore = new GrsXriStore(((Store) openxriServerConfig.getComponentRegistry().getComponent(Store.class)), eppTools);
			else
				this.xriStore = new OpenxriXriStore(((Store) openxriServerConfig.getComponentRegistry().getComponent(Store.class)));
		} catch (Exception ex) {

			throw new PdsConnectionException("Cannot initialize xriStore: " + ex.getMessage(), ex);
		}

		// load ibrokerStore

		try {

			Properties ibrokerStoreProperties = new Properties();
			String ibrokerStorePropertiesFile = filterConfig.getServletContext().getRealPath("WEB-INF/ibrokerstore.properties");
			ibrokerStoreProperties.load(new FileReader(ibrokerStorePropertiesFile));

			this.ibrokerStore = new ibrokerkit.ibrokerstore.store.impl.db.DatabaseStore(ibrokerStoreProperties);
			this.ibrokerStore.init();
		} catch (Exception ex) {

			throw new PdsConnectionException("Cannot initialize ibrokerStore: " + ex.getMessage(), ex);
		}
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

	ibrokerkit.iname4java.store.XriStore getXriStore() {

		return this.xriStore;
	}

	ibrokerkit.ibrokerstore.store.Store getIbrokerStore() {

		return this.ibrokerStore;
	}
}
