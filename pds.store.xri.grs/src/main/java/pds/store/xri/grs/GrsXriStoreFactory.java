package pds.store.xri.grs;

import ibrokerkit.epptools4java.EppTools;

import java.util.Properties;

import javax.servlet.ServletContext;

import org.openxri.config.ServerConfig;
import org.openxri.factories.ServerConfigFactory;
import org.springframework.web.context.ServletContextAware;

import pds.store.xri.XriStoreFactory;

public class GrsXriStoreFactory implements XriStoreFactory, ServletContextAware {

	private Properties properties;
	private EppTools eppTools;
	private ServletContext servletContext;

	public GrsXriStoreFactory(Properties properties, EppTools eppTools) {
		
		this.properties = properties;
		this.eppTools = eppTools;
		this.servletContext = null;
	}
	
	public Properties getProperties() {

		return this.properties;
	}

	public void setProperties(Properties properties) {

		this.properties = properties;
	}

	public EppTools getEppTools() {

		return this.eppTools;
	}

	public void setEppTools(EppTools eppTools) {

		this.eppTools = eppTools;
	}

	public ServletContext getServletContext() {
		
		return this.servletContext;
	}
	
	public void setServletContext(ServletContext servletContext) {

		this.servletContext = servletContext;
	}

	public GrsXriStore createXriStore() throws Exception {

		ServerConfig serverConfig = ServerConfigFactory.init(this.servletContext, this.properties);

		return new GrsXriStore(serverConfig, this.eppTools);
	}
}
