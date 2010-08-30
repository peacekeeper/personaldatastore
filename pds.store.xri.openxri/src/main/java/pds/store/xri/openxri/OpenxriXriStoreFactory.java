package pds.store.xri.openxri;

import java.util.Properties;

import javax.servlet.ServletContext;

import org.openxri.config.ServerConfig;
import org.openxri.factories.ServerConfigFactory;
import org.springframework.web.context.ServletContextAware;

import pds.store.xri.XriStoreFactory;

public class OpenxriXriStoreFactory implements XriStoreFactory, ServletContextAware {

	private Properties properties;
	private ServletContext servletContext;

	public OpenxriXriStoreFactory(Properties properties) {
		
		this.properties = properties;
		this.servletContext = null;
	}
	
	public Properties getProperties() {

		return this.properties;
	}

	public void setProperties(Properties properties) {

		this.properties = properties;
	}

	public ServletContext getServletContext() {
		
		return this.servletContext;
	}
	
	public void setServletContext(ServletContext servletContext) {

		this.servletContext = servletContext;
	}

	public OpenxriXriStore createXriStore() throws Exception {

		ServerConfig serverConfig = ServerConfigFactory.init(this.servletContext, this.properties);

		return new OpenxriXriStore(serverConfig);
	}
}
