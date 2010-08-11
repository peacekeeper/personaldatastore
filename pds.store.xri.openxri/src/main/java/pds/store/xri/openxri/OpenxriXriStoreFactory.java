package pds.store.xri.openxri;

import java.util.Properties;

import javax.servlet.ServletContext;

import org.openxri.config.ServerConfig;
import org.openxri.factories.ServerConfigFactory;
import org.springframework.web.context.ServletContextAware;

public class OpenxriXriStoreFactory implements ServletContextAware {

	private ServletContext servletContext;

	public void setServletContext(ServletContext servletContext) {

		this.servletContext = servletContext;
	}

	public OpenxriXriStore getXriStore(Properties properties) throws Exception {

		ServerConfig serverConfig = ServerConfigFactory.getSingleton();
		if (serverConfig == null) serverConfig = ServerConfigFactory.initSingleton(this.servletContext, properties);

		return new OpenxriXriStore(serverConfig);
	}
}
