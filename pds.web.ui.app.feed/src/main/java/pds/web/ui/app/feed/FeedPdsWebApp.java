package pds.web.ui.app.feed;

import nextapp.echo.app.ResourceImageReference;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.ui.MainContentPane;
import pds.web.ui.app.PdsWebApp;
import pds.xdi.XdiContext;

public class FeedPdsWebApp implements PdsWebApp {

	private String hub;
	private String pubsubhubbubCallback;

	@Override
	public String getName() {

		return "Feed";
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

	public String getPubsubhubbubCallback() {

		return this.pubsubhubbubCallback;
	}

	public void setPubsubhubbubCallback(String pubsubhubbubCallback) {

		this.pubsubhubbubCallback = pubsubhubbubCallback;
	}
}
