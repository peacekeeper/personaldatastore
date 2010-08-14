package pds.core.xri.messagingtargets;


import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.addressing.Addressing;
import org.eclipse.higgins.xdi4j.constants.DictionaryConstants;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.messaging.server.impl.AbstractResourceHandler;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.openxri.GCSAuthority;
import org.openxri.XRI;

import pds.core.xri.XriPdsConnectionFactory;
import pds.core.xri.util.XriWizard;
import pds.store.user.StoreUtil;
import pds.store.xri.Xri;
import pds.store.xri.XriData;
import pds.store.xri.XriStoreException;

public class RootSubjectResourceHandler extends AbstractResourceHandler {

	private static final Log log = LogFactory.getLog(RootSubjectResourceHandler.class);

	private XriPdsConnectionFactory pdsConnectionFactory;

	public RootSubjectResourceHandler(Message message, Subject subject, XriPdsConnectionFactory pdsConnectionFactory) {

		super(message, subject);

		this.pdsConnectionFactory = pdsConnectionFactory;
	}

	@Override
	public boolean executeAdd(Operation operation, MessageResult messageResult, Object executionContext) throws MessagingException {

		pds.store.xri.XriStore xriStore = this.pdsConnectionFactory.getXriStore();
		pds.store.user.Store userStore = this.pdsConnectionFactory.getUserStore();

		// read information from the message

		String xriString = this.operationSubject.getSubjectXri().toString();
		String password = Addressing.findLiteralData(this.operationSubject, new XRI3("$password"));
		String email = Addressing.findLiteralData(this.operationSubject, new XRI3("+email"));

		// try to find parent xri

		GCSAuthority gcsAuthority = (GCSAuthority) new XRI(xriString).getAuthorityPath();
		GCSAuthority parentAuthority = (GCSAuthority) gcsAuthority.getParent();
		Xri parentXri = null;
		String localName = gcsAuthority.getSubSegmentAt(gcsAuthority.getNumSubSegments() - 1).toString();

		try {

			parentXri = xriStore.findXri(parentAuthority.toString());
			if (xriStore.existsXri(parentXri, localName)) throw new MessagingException("XRI exists already.");
		} catch (XriStoreException ex) {

			log.warn("Can not look up parent XRI: " + ex.getMessage(), ex);
			throw new MessagingException("Can not look up parent XRI: " + ex.getMessage(), ex);
		}

		// create user and xri

		Xri xri;
		String inumber;

		try {

			if (userStore != null) {

				userStore.createOrUpdateUser(xriString, StoreUtil.hashPass(password), null, xriString, email, Boolean.FALSE);
			}

			XriData xriData = xriStore.createXriDataFromSubject(this.operationSubject);

			xri = xriStore.registerXri(parentXri, localName, xriData, 0);
			inumber = xri.getCanonicalID().getValue();

			XriWizard.configure(this.pdsConnectionFactory, xri);
		} catch (Exception ex) {

			log.warn("Can not create XRI: " + ex.getMessage(), ex);
			throw new MessagingException("Can not create XRI: " + ex.getMessage(), ex);
		}

		// done


		messageResult.getGraph().createStatement(new XRI3Segment(xriString), new XRI3Segment("$$is"), new XRI3Segment(inumber));
		return true;
	}

	@Override
	public boolean executeGet(Operation operation, MessageResult messageResult, Object executionContext) throws MessagingException {

		pds.store.xri.XriStore xriStore = this.pdsConnectionFactory.getXriStore();

		// read information from the message

		String xriString = this.operationSubject.getSubjectXri().toString();

		// retrieve the xri

		Xri xri = null;

		try {

			xri = xriStore.findXri(xriString);
		} catch (Exception ex) {

			throw new MessagingException("Cannot find xri " + xriString + ": " + ex.getMessage(), ex);
		}

		// anyone can check if an XRI exists or not

		if (xri == null) return false;

		messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), DictionaryConstants.XRI_INHERITANCE, new XRI3Segment(this.operationSubject.getSubjectXri().getFirstSubSegment().getGCS().toString()));

		try {

			List<String> aliases = xri.getAliases();
			for (String alias : aliases) messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), DictionaryConstants.XRI_EQUIVALENCE, new XRI3Segment(alias));

			if (xri.getCanonicalID() != null) {

				messageResult.getGraph().createStatement(this.operationSubject.getSubjectXri(), new XRI3Segment("$$is"), new XRI3Segment(xri.getCanonicalID().getValue()));
			}
		} catch (XriStoreException ex) {

			throw new MessagingException("Cannot get XRI synonyms from store: " + ex.getMessage(), ex);
		}

		return true;
	}
}
