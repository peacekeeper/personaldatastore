package pds.web;


import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.webcontainer.WebContainerServlet;
import nextapp.echo.webcontainer.service.JavaScriptService;
import nextapp.echo.webcontainer.service.StaticTextService;

import org.openxri.resolve.Resolver;

import pds.web.configuration.Configuration;

public class PDSServlet extends WebContainerServlet {

	private static final long serialVersionUID = -7856586634363745175L;

	private transient Properties properties;
	private transient Resolver resolver;

	@Override
	public ApplicationInstance newApplicationInstance() {

		return new PDSApplication(this);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {

		super.init(config);

		this.addInitScript(JavaScriptService.forResource("CustomWaitIndicator", "pds/web/resource/js/CustomWaitIndicator.js"));
		this.addInitStyleSheet(StaticTextService.forResource("pds.web.css", "text/css", "pds/web/resource/style/pds.web.css"));

		this.initProperties();
		this.initResolver();
	}

	private void initProperties() {

		try {

			this.properties = new Properties();
			this.properties.load(Configuration.class.getResourceAsStream("application.properties"));
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize properties: " + ex.getMessage(), ex);
		}
	}

	private void initResolver() {

		try {

			this.resolver = new Resolver(null);
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize resolver: " + ex.getMessage(), ex);
		}
	}

	public Properties getProperties() {

		if (this.properties == null) this.initProperties();

		return this.properties;
	}

	public Resolver getResolver() {

		if (this.resolver == null) this.initResolver();

		return this.resolver;
	}
}
