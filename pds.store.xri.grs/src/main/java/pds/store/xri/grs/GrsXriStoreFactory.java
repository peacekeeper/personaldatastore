package pds.store.xri.grs;

import ibrokerkit.epptools4java.EppTools;

import java.util.Properties;

import javax.servlet.ServletContext;

import org.openxri.config.ServerConfig;
import org.openxri.factories.ServerConfigFactory;
import org.springframework.web.context.ServletContextAware;

public class GrsXriStoreFactory implements ServletContextAware {

	private ServletContext servletContext;

	public void setServletContext(ServletContext servletContext) {

		this.servletContext = servletContext;
	}

	public GrsXriStore getXriStore(Properties properties, EppTools eppTools) throws Exception {

		ServerConfig serverConfig = ServerConfigFactory.getSingleton();
		if (serverConfig == null) serverConfig = ServerConfigFactory.initSingleton(this.servletContext, properties);

		return new GrsXriStore(serverConfig, eppTools);
	}
}
