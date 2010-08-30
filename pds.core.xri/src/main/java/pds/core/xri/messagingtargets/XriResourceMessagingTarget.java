package pds.core.xri.messagingtargets;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.higgins.xdi4j.Literal;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.server.EndpointRegistry;
import org.eclipse.higgins.xdi4j.messaging.server.impl.ResourceHandler;
import org.eclipse.higgins.xdi4j.messaging.server.impl.ResourceMessagingTarget;
import org.eclipse.higgins.xdi4j.messaging.server.interceptor.impl.authn.PasswordAuthenticationMessageInterceptor;
import org.eclipse.higgins.xdi4j.messaging.server.interceptor.impl.authn.PasswordAuthenticationMessageInterceptor.PasswordValidator;
import org.eclipse.higgins.xdi4j.messaging.server.interceptor.impl.authz.ProtectedAddressInterceptor;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.xri.XriPdsInstance;
import pds.store.user.StoreUtil;
import pds.store.user.User;
import pds.store.xri.Xri;
import pds.store.xri.XriStore;

public class XriResourceMessagingTarget extends ResourceMessagingTarget {

	private static Log log = LogFactory.getLog(ResourceMessagingTarget.class.getName());

	private XriPdsInstance pdsInstance;

	public XriResourceMessagingTarget() {

		super(true);
	}

	@Override
	public void init(EndpointRegistry endpointRegistry) throws Exception {

		super.init(endpointRegistry);

		// add a PasswordAuthenticationMessageInterceptor

		PasswordAuthenticationMessageInterceptor passwordAuthenticationMessageInterceptor = new PasswordAuthenticationMessageInterceptor();
		passwordAuthenticationMessageInterceptor.setPasswordValidator(new PasswordValidator() {

			public boolean isValidPassword(XRI3Segment senderXri3, String password) throws MessagingException {

				XriStore xriStore = XriResourceMessagingTarget.this.pdsInstance.getPdsInstanceFactory().getXriStore();
				pds.store.user.Store userStore = XriResourceMessagingTarget.this.pdsInstance.getPdsInstanceFactory().getUserStore();

				Xri senderXri = null;
				String senderUserIdentifier = null;
				User senderUser = null;

				// find sender xri and user

				try {

					senderXri = xriStore.findXri(senderXri3.toString());

					if (senderXri != null) senderUserIdentifier = senderXri.getUserIdentifier();
					if (senderUserIdentifier != null) senderUser = userStore.findUser(senderUserIdentifier);
				} catch (Exception ex) {

					throw new MessagingException("Cannot look up xri: " + ex.getMessage(), ex);
				}

				if (senderXri == null || senderUser == null) {

					log.debug("Sender xri or user not found.");
					return false;
				}

				// check user and password

				User user = XriResourceMessagingTarget.this.pdsInstance.getUser();
				if (user == null) return false;

				boolean self = senderUser.equals(user);
				boolean valid = StoreUtil.checkPass(senderUser.getPass(), password);

				// done

				log.debug("self: " + self + ", valid: " + valid);
				return self && valid;
			}
		});
		this.getMessageInterceptors().add(passwordAuthenticationMessageInterceptor);

		// add a ProtectedAddressInterceptor

		List<XRI3> protectedGetAddresses = new ArrayList<XRI3> ();
		List<XRI3> protectedModAddresses = new ArrayList<XRI3> ();

		for (XRI3Segment alias : this.pdsInstance.getAliases()) {

			protectedGetAddresses.add(new XRI3(alias.toString() + "/$password"));
			protectedGetAddresses.add(new XRI3(alias.toString() + "/$key$private"));
			protectedModAddresses.add(new XRI3(alias.toString() + "/$password"));
		}

		ProtectedAddressInterceptor protectedAddressInterceptor = new ProtectedAddressInterceptor();
		protectedAddressInterceptor.setProtectedGetAddresses(protectedGetAddresses.toArray(new XRI3[protectedGetAddresses.size()]));
		protectedAddressInterceptor.setProtectedModAddresses(protectedGetAddresses.toArray(new XRI3[protectedModAddresses.size()]));
		this.getAddressInterceptors().add(protectedAddressInterceptor);
	}

	@Override
	public ResourceHandler getResource(Message message, Subject operationSubject) throws MessagingException {

		for (XRI3Segment alias : this.pdsInstance.getAliases()) {

			if (operationSubject.getSubjectXri().equals(alias)) {

				return new XriSubjectResourceHandler(message, operationSubject, this.pdsInstance);
			}
		}

		return null;
	}

	@Override
	public ResourceHandler getResource(Message message, Subject operationSubject, Predicate operationPredicate, Literal operationLiteral) throws MessagingException {

		for (XRI3Segment alias : this.pdsInstance.getAliases()) {

			if (operationSubject.getSubjectXri().equals(alias) &&
					operationPredicate.getPredicateXri().equals(new XRI3Segment("$password"))) {

				return new XriSubjectPredicateLiteralResourceHandler(message, operationSubject, operationPredicate, operationLiteral, this.pdsInstance);
			}
		}

		return null;
	}

	public XriPdsInstance getPdsInstance() {

		return this.pdsInstance;
	}

	public void setPdsInstance(XriPdsInstance pdsInstance) {

		this.pdsInstance = pdsInstance;
	}
}
