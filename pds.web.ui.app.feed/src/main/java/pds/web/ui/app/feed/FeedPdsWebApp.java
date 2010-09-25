package pds.web.ui.app.feed;

import nextapp.echo.app.ResourceImageReference;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.ui.MainContentPane;
import pds.web.ui.app.PdsWebApp;
import pds.xdi.XdiContext;

public class FeedPdsWebApp implements PdsWebApp {

	private String hub;
	private String pubsubhubbubEndpoint;
	private String atomFeedEndpoint;
	private String leaseSeconds;

	@Override
	public String getName() {

		return "Network";
	}

	@Override
	public ResourceImageReference getResourceImageReference() {

		return new ResourceImageReference("/pds/web/ui/app/feed/app.png");
	}

	@Override
	public void onActionPerformed(MainContentPane mainContentPane, XdiContext context, XRI3Segment subjectXri) {

		FeedWindowPane feedWindowPane = new FeedWindowPane();
		feedWindowPane.setFeedPdsWebApp(this);
		feedWindowPane.setContextAndSubjectXri(context, subjectXri);

		mainContentPane.add(feedWindowPane);
	}

	public String getHub() {

		return this.hub;
	}

	public void setHub(String hub) {

		this.hub = hub;
	}

	public String getPubsubhubbubEndpoint() {

		return this.pubsubhubbubEndpoint;
	}

	public void setPubsubhubbubEndpoint(String pubsubhubbubEndpoint) {

		this.pubsubhubbubEndpoint = pubsubhubbubEndpoint;
		if (! this.pubsubhubbubEndpoint.endsWith("/")) this.pubsubhubbubEndpoint += "/";
	}

	public String getAtomFeedEndpoint() {

		return this.atomFeedEndpoint;
	}

	public void setAtomFeedEndpoint(String atomFeedEndpoint) {

		this.atomFeedEndpoint = atomFeedEndpoint;
		if (! this.atomFeedEndpoint.endsWith("/")) this.atomFeedEndpoint += "/";
	}

	public String getLeaseSeconds() {

		return this.leaseSeconds;
	}

	public void setLeaseSeconds(String pubsubhubbubLeaseSeconds) {

		this.leaseSeconds = pubsubhubbubLeaseSeconds;
	}
}
