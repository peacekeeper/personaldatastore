package pds.web.ui.app.photos;

import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.WindowPane;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.ui.app.PdsWebApp;
import pds.web.xdi.XdiContext;

public class PhotosPdsWebApp implements PdsWebApp {

	@Override
	public String getName() {

		return "Photos";
	}

	@Override
	public ResourceImageReference getResourceImageReference() {

		return new ResourceImageReference("/pds/web/ui/app/photos/app.png");
	}

	@Override
	public WindowPane newWindowPane(XdiContext context, XRI3Segment subjectXri) {

		PhotosWindowPane photosWindowPane = new PhotosWindowPane();
		photosWindowPane.setContextAndSubjectXri(context, subjectXri);

		return photosWindowPane;
	}
}
