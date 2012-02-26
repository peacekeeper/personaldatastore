package pds.core.xri.messagingtargets;


import java.util.List;

import javax.xml.ws.soap.Addressing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxri.GCSAuthority;
import org.openxri.XRI;

import pds.core.xri.XriPdsInstanceFactory;
import pds.core.xri.util.XriWizard;
import pds.store.user.StoreUtil;
import pds.store.xri.Xri;
import pds.store.xri.XriData;
import pds.store.xri.XriStoreException;
import xdi2.core.ContextNode;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.xri3.impl.XRI3;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.impl.AbstractResourceHandler;

public class RootSubjectResourceHandler extends AbstractResourceHandler {

	private static final Log log = LogFactory.getLog(RootSubjectResourceHandler.class);

	private XriPdsInstanceFactory pdsInstanceFactory;

	public RootSubjectResourceHandler(Operation operation, ContextNode contextNode, XriPdsInstanceFactory pdsInstanceFactory) {

		super(operation, contextNode);

		this.pdsInstanceFactory = pdsInstanceFactory;
	}

	@Override
	public boolean executeAdd(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		pds.store.xri.XriStore xriStore = this.pdsInstanceFactory.getXriStore();
		pds.store.user.Store userStore = this.pdsInstanceFactory.getUserStore();

		// read information from the message

		String xriString = this.operationContextNode.getSubjectXri().toString();
		String password = Addressing.findLiteralData(this.operationContextNode, new XRI3("$password"));
		String email = Addressing.findLiteralData(this.operationContextNode, new XRI3("+email"));

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

			XriData xriData = xriStore.createXriDataFromSubject(this.operationContextNode);

			xri = xriStore.registerXri(parentXri, localName, xriData, 0);
			inumber = xri.getCanonicalID().getValue();

			XriWizard.configure(this.pdsInstanceFactory, xri);
		} catch (Exception ex) {

			log.warn("Can not create XRI: " + ex.getMessage(), ex);
			throw new MessagingException("Can not create XRI: " + ex.getMessage(), ex);
		}

		// done


		messageResult.getGraph().createStatement(new XRI3Segment(xriString), new XRI3Segment("$$is"), new XRI3Segment(inumber));
		return true;
	}

	@Override
	public boolean executeGet(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		pds.store.xri.XriStore xriStore = this.pdsInstanceFactory.getXriStore();

		// read information from the message

		String xriString = this.operationContextNode.getSubjectXri().toString();

		// retrieve the xri

		Xri xri = null;

		try {

			xri = xriStore.findXri(xriString);
		} catch (Exception ex) {

			throw new MessagingException("Cannot find xri " + xriString + ": " + ex.getMessage(), ex);
		}

		if (xri == null) return false;

		// canonical, type and aliases

		try {

			if (xri.getCanonicalID() != null) {

				messageResult.getGraph().createStatement(this.operationContextNode.getSubjectXri(), new XRI3Segment("$$is"), new XRI3Segment(xri.getCanonicalID().getValue()));
			}

			messageResult.getGraph().createStatement(this.operationContextNode.getSubjectXri(), DictionaryConstants.XRI_IS_A, new XRI3Segment(this.operationContextNode.getSubjectXri().getFirstSubSegment().getGCS().toString()));

			List<String> aliases = xri.getAliases();
			for (String alias : aliases) {

				messageResult.getGraph().createStatement(this.operationContextNode.getSubjectXri(), DictionaryConstants.XRI_IS, new XRI3Segment(alias));
			}
		} catch (XriStoreException ex) {

			throw new MessagingException("Cannot get XRI synonyms from store: " + ex.getMessage(), ex);
		}

		return true;
	}
}
