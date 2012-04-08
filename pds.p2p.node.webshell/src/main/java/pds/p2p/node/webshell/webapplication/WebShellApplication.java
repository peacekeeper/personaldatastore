package pds.p2p.node.webshell.webapplication;

import java.util.Properties;

import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.time.Duration;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.webpages.error.AccessDeniedPage;
import pds.p2p.node.webshell.webpages.error.InternalErrorPage;
import pds.p2p.node.webshell.webpages.error.PageExpired;
import pds.p2p.node.webshell.webpages.index.Index;
import pds.p2p.node.webshell.webpages.information.About;
import pds.p2p.node.webshell.webpages.information.PrivacyPolicy;
import pds.p2p.node.webshell.webpages.information.TermsAndConditions;
import pds.p2p.node.webshell.webpages.terminals.ShellTerminal;
import pds.p2p.node.webshell.webpages.vrm.CreatePrfp;

public class WebShellApplication extends WebApplication {

	protected Properties properties;
	protected Properties velocityProperties;

	@Override
	public void init() {

		// set up Danube API

		try {

			DanubeApiClient.init();
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}

		// set up page mounting

		this.mountPage("/PrivacyPolicy", PrivacyPolicy.class);
		this.mountPage("/TermsOfUse", TermsAndConditions.class);
		this.mountPackage("/information", About.class);
		this.mountPackage("/terminals", ShellTerminal.class);
		this.mountPackage("/vrm", CreatePrfp.class);

		// set up various wicket parameters

		this.getRequestCycleListeners().add(new WebShellRequestCycleListener());
		this.getApplicationSettings().setClassResolver(new WebShellClassResolver());
		this.getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
		this.getApplicationSettings().setInternalErrorPage(InternalErrorPage.class);
		this.getApplicationSettings().setPageExpiredErrorPage(PageExpired.class);
		this.getMarkupSettings().setDefaultMarkupEncoding("UTF-8");

		switch (this.getConfigurationType()) {

		case DEVELOPMENT:
			this.getMarkupSettings().setStripWicketTags(false);
			this.getMarkupSettings().setStripComments(false);
			this.getResourceSettings().setResourcePollFrequency(Duration.ONE_SECOND);
			this.getDebugSettings().setComponentUseCheck(true);
			this.getDebugSettings().setAjaxDebugModeEnabled(true);
			break;

		case DEPLOYMENT:
			this.getMarkupSettings().setStripWicketTags(true);
			this.getMarkupSettings().setStripComments(true);
			this.getResourceSettings().setResourcePollFrequency(null);
			this.getDebugSettings().setComponentUseCheck(false);
			this.getDebugSettings().setAjaxDebugModeEnabled(false);
			break;

		}
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();

		DanubeApiClient.shutdown();
	}

	@Override
	public Class<? extends Page> getHomePage() {

		return Index.class;
	}

	@Override
	public RuntimeConfigurationType getConfigurationType() {

		return RuntimeConfigurationType.DEPLOYMENT;
	}

	@Override
	public Session newSession(Request request, Response response) {

		return(new WebShellSession(request));
	}

	public Properties getProperties() {
		return (this.properties);
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public Properties getVelocityProperties() {
		return (this.velocityProperties);
	}

	public void setVelocityProperties(Properties velocityProperties) {
		this.velocityProperties = velocityProperties;
	}
}
