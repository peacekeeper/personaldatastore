package pds.web.ui.app.feed;

import nextapp.echo.app.ResourceImageReference;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.ui.MainContentPane;
import pds.web.ui.app.PdsWebApp;
import pds.web.xdi.XdiContext;

public class FeedPdsWebApp implements PdsWebApp {

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
		feedWindowPane.setContextAndSubjectXri(context, subjectXri);

		mainContentPane.add(feedWindowPane);
	}
}
