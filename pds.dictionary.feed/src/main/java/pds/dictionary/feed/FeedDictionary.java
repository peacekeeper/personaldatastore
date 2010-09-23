package pds.dictionary.feed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Text.Type;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.addressing.Addressing;
import org.eclipse.higgins.xdi4j.multivalue.MultiSubjects;
import org.eclipse.higgins.xdi4j.types.Timestamps;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.jdom.Namespace;

import pds.dictionary.PdsDictionary;

import com.cliqset.abdera.ext.activity.ActivityEntry;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndPersonImpl;

/**
 * Dictionary methods for representing Atom/ActivityStreams feeds in XDI.
 */
public class FeedDictionary {

	public static final XRI3Segment XRI_FEED = new XRI3Segment("+ostatus+feed");
	public static final XRI3Segment XRI_TOPICS = new XRI3Segment("+ostatus+topics");
	public static final XRI3Segment XRI_ENTRIES = new XRI3Segment("+entries");
	public static final XRI3Segment XRI_ENTRY = new XRI3Segment("+entry");
	public static final XRI3Segment XRI_VERIFYTOKEN = new XRI3Segment("+push+verify.token");
	public static final XRI3Segment XRI_SUBSCRIBED = new XRI3Segment("+push+subscribed");
	public static final XRI3Segment XRI_MENTIONS = new XRI3Segment("+ostatus+mentions");

	public static final XRI3Segment XRI_ACTIVITY_ID = new XRI3Segment("$is$");
	public static final XRI3Segment XRI_ACTIVITY_VERB = new XRI3Segment("+activity+verb");
	public static final XRI3Segment XRI_TITLE = new XRI3Segment("+title");
	public static final XRI3Segment XRI_SUMMARY = new XRI3Segment("+summary");
	public static final XRI3Segment XRI_SUMMARY_TYPE = new XRI3Segment("+summary.type");
	public static final XRI3Segment XRI_CONTENT = new XRI3Segment("+content");
	public static final XRI3Segment XRI_CONTENT_MIME_TYPE = new XRI3Segment("+content+mime.type");
	public static final XRI3Segment XRI_PUBLISHED_DATE = new XRI3Segment("+published$d");
	public static final XRI3Segment XRI_UPDATED_DATE = new XRI3Segment("+updated$d");
	public static final XRI3Segment XRI_EDITED_DATE = new XRI3Segment("+edited$d");
	public static final XRI3Segment XRI_AUTHOR_NAME = new XRI3Segment("+author+name");
	public static final XRI3Segment XRI_AUTHOR_EMAIL = new XRI3Segment("+author+nemail");
	public static final XRI3Segment XRI_AUTHOR_URI = new XRI3Segment("+author+uri");
	public static final XRI3Segment XRI_ACTIVITY_OBJECT_TYPE = new XRI3Segment("+activity+object+type");
	public static final XRI3Segment XRI_ACTIVITY_ACTOR_GIVEN_NAME = new XRI3Segment("+activity+actor+given.name");
	public static final XRI3Segment XRI_ACTIVITY_ACTOR_FAMILY_NAME = new XRI3Segment("+activity+actor+family.name");
	public static final XRI3Segment XRI_ACTIVITY_ACTOR_PREFERRED_USERNAME = new XRI3Segment("+activity+actor+preferred.username");
	public static final XRI3Segment XRI_ACTIVITY_ACTOR_DISPLAY_NAME = new XRI3Segment("+activity+actor+display.name");

	private static final Namespace NAMESPACE_ATOM = Namespace.getNamespace("http://www.w3.org/2005/Atom");
	private static final Namespace NAMESPACE_XDI = Namespace.getNamespace("xdi", "http://xdi.oasis-open.org");
	private static final Namespace NAMESPACE_ACTIVITYSTREAMS = Namespace.getNamespace("activity", "http://activitystrea.ms/spec/1.0/");
	private static final Namespace NAMESPACE_POCO = Namespace.getNamespace("poco", "http://portablecontacts.net/spec/1.0");

	private static final Log log = LogFactory.getLog(FeedDictionary.class.getName());

	private FeedDictionary() { }

	/**
	 * Stores a feed entry as an XDI subject.
	 */
	public static void fromEntry(
			Subject entrySubject,
			IRI activityId, 
			IRI activityVerb, 
			String title, 
			String summary, 
			Type summaryType, 
			String content, 
			MimeType contentMimeType, 
			Date publishedDate, 
			Date updatedDate, 
			Date editedDate, 
			String authorName,
			String authorEmail,
			IRI authorUri,
			IRI activityObjectType,
			String activityActorGivenName,
			String activityActorFamilyName,
			String activityActorPreferredUsername,
			String activityActorDisplayName) {

		if (activityId != null) entrySubject.createStatement(XRI_ACTIVITY_ID, activityId.toString());
		if (activityVerb != null) entrySubject.createStatement(XRI_ACTIVITY_VERB, activityVerb.toString());
		if (title != null) entrySubject.createStatement(XRI_TITLE, title);
		if (summary != null) entrySubject.createStatement(XRI_SUMMARY, summary);
		if (summaryType != null) entrySubject.createStatement(XRI_SUMMARY_TYPE, summaryType.toString());
		if (content != null) entrySubject.createStatement(XRI_CONTENT, content);
		if (contentMimeType != null) entrySubject.createStatement(XRI_CONTENT_MIME_TYPE, contentMimeType.toString());
		if (publishedDate != null) entrySubject.createStatement(XRI_PUBLISHED_DATE, Timestamps.dateToXri(publishedDate));
		if (updatedDate != null) entrySubject.createStatement(XRI_UPDATED_DATE, Timestamps.dateToXri(updatedDate));
		if (editedDate != null) entrySubject.createStatement(XRI_EDITED_DATE, Timestamps.dateToXri(editedDate));
		if (authorName != null) entrySubject.createStatement(XRI_AUTHOR_NAME, authorName);
		if (authorEmail != null) entrySubject.createStatement(XRI_AUTHOR_EMAIL, authorEmail);
		if (authorUri != null) entrySubject.createStatement(XRI_AUTHOR_URI, authorUri.toString());
		if (activityObjectType != null) entrySubject.createStatement(XRI_ACTIVITY_OBJECT_TYPE, activityObjectType.toString());
		if (activityActorGivenName != null) entrySubject.createStatement(XRI_ACTIVITY_ACTOR_GIVEN_NAME, activityActorGivenName);
		if (activityActorFamilyName != null) entrySubject.createStatement(XRI_ACTIVITY_ACTOR_FAMILY_NAME, activityActorFamilyName);
		if (activityActorPreferredUsername != null) entrySubject.createStatement(XRI_ACTIVITY_ACTOR_PREFERRED_USERNAME, activityActorPreferredUsername);
		if (activityActorDisplayName != null) entrySubject.createStatement(XRI_ACTIVITY_ACTOR_DISPLAY_NAME, activityActorDisplayName);
	}

	/**
	 * Stores a feed entry as an XDI subject.
	 */
	public static void fromEntry(Subject entrySubject, ActivityEntry activityEntry) {

		IRI activityId = activityEntry.getIdElement() != null ? activityEntry.getId() : null;
		IRI activityVerb = activityEntry.getVerbElement() != null ? activityEntry.getVerb() : null;
		String title = activityEntry.getTitleElement() != null ? activityEntry.getTitle() : null;
		String summary = activityEntry.getSummaryElement() != null ? activityEntry.getSummary() : null;
		Type summaryType = activityEntry.getSummaryElement() != null ? activityEntry.getSummaryType() : null;
		String content = activityEntry.getContentElement() != null ? activityEntry.getContent() : null;
		MimeType contentMimeType = activityEntry.getContentElement() != null ? activityEntry.getContentMimeType() : null;
		Date publishedDate = activityEntry.getPublishedElement() != null ? activityEntry.getPublished() : null;
		Date updatedDate = activityEntry.getUpdatedElement() != null ? activityEntry.getUpdated() : null;
		Date editedDate = activityEntry.getEditedElement() != null ? activityEntry.getEdited() : null;

		String authorName = null;
		String authorEmail = null;
		IRI authorUri = null;
		if (activityEntry.getAuthors() != null && activityEntry.getAuthors().size() > 0) {

			authorName = activityEntry.getAuthors().get(0).getName();
			authorEmail = activityEntry.getAuthors().get(0).getEmail();
			authorUri = activityEntry.getAuthors().get(0).getUri();
		}

		IRI activityObjectType = null;
		if (activityEntry.getObjects() != null && activityEntry.getObjects().size() > 0) {

			activityObjectType = activityEntry.getObjects().get(0).getObjectType();
			if (title == null) title = activityEntry.getObjects().get(0).getTitle();
			if (summary == null) summary = activityEntry.getObjects().get(0).getSummary();
			if (summaryType == null) summaryType = activityEntry.getObjects().get(0).getSummaryType();
			if (content == null) content = activityEntry.getObjects().get(0).getContent();
			if (contentMimeType == null) contentMimeType = activityEntry.getObjects().get(0).getContentMimeType();
		}

		String activityActorGivenName = null;
		String activityActorFamilyName = null;
		String activityActorPreferredUsername = null;
		String activityActorDisplayName = null;
		if (activityEntry.getActor() != null) {

			Element activityActorElement = activityEntry.getActor();
			Element pocoNameElement = activityActorElement.getFirstChild(new QName("http://portablecontacts.net/spec/1.0", "name"));

			if (pocoNameElement != null) {

				Element pocoGivenNameElement = pocoNameElement.getFirstChild(new QName("http://portablecontacts.net/spec/1.0", "givenName"));
				if (pocoGivenNameElement != null) activityActorGivenName = pocoGivenNameElement.getText();

				Element pocoFamilyNameElement = pocoNameElement.getFirstChild(new QName("http://portablecontacts.net/spec/1.0", "familyName"));
				if (pocoFamilyNameElement != null) activityActorFamilyName = pocoFamilyNameElement.getText();

				Element pocoPreferredUsernameElement = pocoNameElement.getFirstChild(new QName("http://portablecontacts.net/spec/1.0", "preferredUsername"));
				if (pocoPreferredUsernameElement != null) activityActorPreferredUsername = pocoPreferredUsernameElement.getText();

				Element pocoDisplayNameElement = pocoNameElement.getFirstChild(new QName("http://portablecontacts.net/spec/1.0", "displayName"));
				if (pocoDisplayNameElement != null) activityActorDisplayName = pocoDisplayNameElement.getText();
			}
		}

		fromEntry(
				entrySubject,
				activityId, 
				activityVerb, 
				title, 
				summary,
				summaryType,
				content, 
				contentMimeType, 
				publishedDate, 
				updatedDate, 
				editedDate, 
				authorName,
				authorEmail,
				authorUri,
				activityObjectType,
				activityActorGivenName,
				activityActorFamilyName,
				activityActorPreferredUsername,
				activityActorDisplayName);
	}

	/**
	 * Retrieves a feed from a list of XDI subjects plus some extra information.
	 */
	public static SyndFeed toFeed(String xri, Subject pdsSubject, String format, String contentType, String hub, String selfEndpoint, String salmonEndpoint) {

		// add feed data

		String authorName = Addressing.findLiteralData(pdsSubject, new XRI3("$" + PdsDictionary.XRI_NAME));
		if (authorName == null) authorName = xri;

		String authorEmail = Addressing.findLiteralData(pdsSubject, new XRI3("$" + PdsDictionary.XRI_EMAIL));

		SyndPersonImpl syndPerson = new SyndPersonImpl();
		if (authorName != null) syndPerson.setName(authorName);
		if (authorEmail != null) syndPerson.setEmail(authorEmail);
		syndPerson.setUri("http://xri2xrd.net/" + pdsSubject.getSubjectXri());

		SyndFeed syndFeed = new SyndFeedImpl();
		syndFeed.setTitle(xri);
		syndFeed.setFeedType(format);
		syndFeed.setLink("http://xri2xrd.net/" + pdsSubject.getSubjectXri());
		syndFeed.setDescription("Feed for " + xri);
		if (authorName != null) syndFeed.setAuthors(Collections.singletonList(syndPerson));

		List<org.jdom.Element> foreignElements = new ArrayList<org.jdom.Element> ();
		foreignElements.add(makeId(pdsSubject));
		foreignElements.add(makeActivitySubject(pdsSubject));
		foreignElements.add(makeLink("hub", hub, null, "PubSubHubbub"));
		foreignElements.add(makeLink("salmon", salmonEndpoint, null, "Salmon"));
		foreignElements.add(makeLink("salmon-reply", salmonEndpoint, null, "Salmon Replies"));
		foreignElements.add(makeLink("salmon-mention", salmonEndpoint, null, "Salmon Mention"));
		foreignElements.add(makeLink("http://salmon-protocol.org/ns/salmon-replies", salmonEndpoint, null, "Salmon Replies"));
		foreignElements.add(makeLink("http://salmon-protocol.org/ns/salmon-mention", salmonEndpoint, null, "Salmon Mention"));

		foreignElements.add(makeLink("self", selfEndpoint, contentType, null));
		syndFeed.setForeignMarkup(foreignElements);

		// add entries

		List<SyndEntry> entries = new ArrayList<SyndEntry> ();

		Predicate predicate = pdsSubject.getPredicate(XRI_FEED);
		if (predicate == null) return syndFeed;

		Graph innerGraph = predicate.getInnerGraph();
		if (innerGraph == null) return syndFeed;

		Iterator<Subject> entrySubjects = MultiSubjects.getMultiSubjects(innerGraph, XRI_ENTRY);

		while (entrySubjects.hasNext()) {

			Subject entrySubject = entrySubjects.next();

			try {

				SyndEntry entry = toEntry(xri, pdsSubject, entrySubject, contentType, selfEndpoint);
				entries.add(0, entry);

				log.debug("Added entry for " + entrySubject.getSubjectXri());
			} catch (Exception ex) {

				log.warn("Skipping entry " + entrySubject.getSubjectXri() + ": " + ex.getMessage(), ex);
			}
		}

		syndFeed.setEntries(entries);

		return syndFeed;
	}

	/**
	 * Retrieves a feed entry from an XDI subject.
	 */
	public static SyndEntry toEntry(String xri, Subject pdsSubject, Subject entrySubject, String contentType, String selfEndpoint) throws Exception {

		String authorName = Addressing.findLiteralData(pdsSubject, new XRI3("$" + PdsDictionary.XRI_NAME));
		if (authorName == null) authorName = xri;

		String authorEmail = Addressing.findLiteralData(pdsSubject, new XRI3("$" + PdsDictionary.XRI_EMAIL));

		XRI3Segment publishedDate = Addressing.findReferenceXri(entrySubject, new XRI3("" + XRI_PUBLISHED_DATE));
		String title = Addressing.findLiteralData(entrySubject, new XRI3("" + XRI_TITLE));
		String content = Addressing.findLiteralData(entrySubject, new XRI3("" + XRI_CONTENT));
		String contentMimeType = Addressing.findLiteralData(entrySubject, new XRI3("" + XRI_CONTENT_MIME_TYPE));
		String activityVerb = Addressing.findLiteralData(entrySubject, new XRI3("" + XRI_ACTIVITY_VERB));
		String activityObjectType = Addressing.findLiteralData(entrySubject, new XRI3("" + XRI_ACTIVITY_OBJECT_TYPE));

		SyndPersonImpl syndPerson = new SyndPersonImpl();
		if (authorName != null) syndPerson.setName(authorName);
		if (authorEmail != null) syndPerson.setEmail(authorEmail);
		syndPerson.setUri("http://xri2xrd.net/" + pdsSubject.getSubjectXri());

		SyndContentImpl syndDescription = new SyndContentImpl();
		if (content != null) syndDescription.setValue(content);
		if (contentMimeType != null) syndDescription.setType(contentMimeType);

		SyndEntry syndEntry = new SyndEntryImpl();
		if (content != null) syndEntry.setDescription(syndDescription);
		if (authorName != null) syndEntry.setAuthors(Collections.singletonList(syndPerson));
		if (publishedDate != null) syndEntry.setPublishedDate(Timestamps.xriToDate(publishedDate));
		if (title != null) syndEntry.setTitle(title);
		if (content != null) syndEntry.setContents(Collections.singletonList(syndDescription));

		List<org.jdom.Element> foreignMarkup = new ArrayList<org.jdom.Element> ();
		foreignMarkup.add(makeId(entrySubject));
		foreignMarkup.add(makeSourceElement(pdsSubject, contentType, selfEndpoint));
		foreignMarkup.add(makeXdiElement(entrySubject, "X3 Standard", null));
		foreignMarkup.add(makeActivityVerb(activityVerb));
		foreignMarkup.add(makeActivityObjectType(activityObjectType));
		syndEntry.setForeignMarkup(foreignMarkup);

		return syndEntry;
	}

	/*
	 * Helper methods for constructing XML elements.
	 */

	private static org.jdom.Element makeId(Subject subject) {

		org.jdom.Element element;
		element = new org.jdom.Element("id", NAMESPACE_ATOM);
		element.setText("http://xri2xrd.net/" + Addressing.getAddress(subject, false).toString());

		return element;
	}

	private static org.jdom.Element makeActivitySubject(Subject pdsSubject) {

		String preferredUsername = pdsSubject.getSubjectXri().toString();

		for (XRI3Segment referenceXri : Addressing.findReferenceXris(pdsSubject, new XRI3("$is"))) {

			if (referenceXri.toString().startsWith("=!") || referenceXri.toString().startsWith("@!")) continue;
			preferredUsername = referenceXri.toString();
			break;
		}

		String displayName = Addressing.findLiteralData(pdsSubject, new XRI3("$" + PdsDictionary.XRI_NAME.toString()));

		org.jdom.Element element;
		element = new org.jdom.Element("subject", NAMESPACE_ACTIVITYSTREAMS);
		if (preferredUsername != null) element.addContent(makePoco("preferredUsername", preferredUsername));
		if (displayName != null) element.addContent(makePoco("displayName", displayName));
		element.addContent(makeLink("alternate", "http://xri2xrd.net/" + pdsSubject.getSubjectXri(), "text/html", null));
		element.addContent(makeId(pdsSubject));
		element.addContent(makeActivityObjectType("http://activitystrea.ms/schema/1.0/person"));

		return element;

	}

	private static org.jdom.Element makeSourceElement(Subject pdsSubject, String contentType, String selfEndpoint) {

		org.jdom.Element element;
		element = new org.jdom.Element("source", NAMESPACE_ATOM);
		element.addContent(makeId(pdsSubject));
		element.addContent(makeLink("alternate", "http://xri2xrd.net/" + pdsSubject.getSubjectXri(), "text/html", null));
		element.addContent(makeLink("self", selfEndpoint, contentType, null));

		return element;
	}

	private static org.jdom.Element makeXdiElement(Subject subject, String format, Properties properties) {

		org.jdom.Element element;
		element = new org.jdom.Element("xdi", NAMESPACE_XDI);
		if (format != null) element.setAttribute("format", format);
		element.setText(subject.toString(format, properties));

		return element;
	}

	private static org.jdom.Element makeActivityVerb(String activityVerb) {

		org.jdom.Element element;
		element = new org.jdom.Element("verb", NAMESPACE_ACTIVITYSTREAMS);
		element.setText(activityVerb);

		return element;
	}

	private static org.jdom.Element makeActivityObjectType(String activityObjectType) {

		org.jdom.Element element;
		element = new org.jdom.Element("object-type", NAMESPACE_ACTIVITYSTREAMS);
		element.setText(activityObjectType);

		return element;
	}

	private static org.jdom.Element makeLink(String rel, String href, String type, String title) {

		org.jdom.Element element;
		element = new org.jdom.Element("link", NAMESPACE_ATOM);
		if (rel != null) element.setAttribute("rel", rel);
		if (href != null) element.setAttribute("href", href);
		if (type != null) element.setAttribute("type", type);
		if (title != null) element.setAttribute("title", title);

		return element;
	}

	private static org.jdom.Element makePoco(String tagName, String value) {

		org.jdom.Element element;
		element = new org.jdom.Element(tagName, NAMESPACE_POCO);
		element.setText(value);

		return element;
	}
}
