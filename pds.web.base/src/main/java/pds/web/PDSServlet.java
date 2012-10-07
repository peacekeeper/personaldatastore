package pds.web;


import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.webcontainer.WebContainerServlet;
import nextapp.echo.webcontainer.service.JavaScriptService;
import nextapp.echo.webcontainer.service.StaticTextService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.web.ui.app.PdsWebApp;
import pds.web.ui.endpoint.SignInMethod;
import xdi2.resolution.XDIResolver;

public class PDSServlet extends WebContainerServlet {

	private static final long serialVersionUID = -7856586634363745175L;

	private static final Logger log = LoggerFactory.getLogger(PDSServlet.class);

	private List<PdsWebApp> pdsWebApps;
	private List<SignInMethod> signInMethods;

	private transient XDIResolver resolver;

	@Override
	public ApplicationInstance newApplicationInstance() {

		return new PDSApplication(this);
	}

	public void init(ServletConfig servletConfig) throws ServletException {

		super.init(servletConfig);

		log.info("Initializing...");

		this.addInitScript(JavaScriptService.forResource("CustomWaitIndicator", "pds/web/resource/js/CustomWaitIndicator.js"));
		this.addInitStyleSheet(StaticTextService.forResource("pds.web.css", "text/css", "pds/web/resource/style/pds.web.css"));

		this.initResolver();

		log.info("Initializing complete.");
	}

	private void initResolver() {

		try {

			this.resolver = new XDIResolver();
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize resolver: " + ex.getMessage(), ex);
		}
	}

	public XDIResolver getResolver() {

		if (this.resolver == null) this.initResolver();

		return this.resolver;
	}

	public List<PdsWebApp> getPdsWebApps() {

		return this.pdsWebApps;
	}

	public void setPdsWebApps(List<PdsWebApp> pdsWebApps) {

		this.pdsWebApps = pdsWebApps;
	}

	public List<SignInMethod> getSignInMethods() {

		return this.signInMethods;
	}

	public void setSignInMethods(List<SignInMethod> signInMethods) {

		this.signInMethods = signInMethods;
	}
}
