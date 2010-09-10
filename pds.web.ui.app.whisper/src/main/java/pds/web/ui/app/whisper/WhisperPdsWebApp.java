package pds.web.ui.app.whisper;

import nextapp.echo.app.ResourceImageReference;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.ui.MainContentPane;
import pds.web.ui.MessageDialog;
import pds.web.ui.app.PdsWebApp;
import pds.xdi.XdiContext;

public class WhisperPdsWebApp implements PdsWebApp {

	@Override
	public String getName() {

		return "Whisper";
	}

	@Override
	public ResourceImageReference getResourceImageReference() {

		return new ResourceImageReference("/pds/web/ui/app/whisper/app.png");
	}

	@Override
	public void onActionPerformed(MainContentPane mainContentPane, XdiContext context, XRI3Segment subjectXri) {

		MessageDialog.notImplemented();
	}
}
