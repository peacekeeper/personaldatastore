package pds.dictionary.feed;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.addressing.Addressing;
import org.eclipse.higgins.xdi4j.types.Timestamps;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.jdom.Namespace;

import pds.xdi.XdiContext;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

/**
 * Dictionary methods for representing Atom/ActivityStreams feeds in XDI.
 */
public class FeedDictionary {

	private static final String DEFAULT_ACTIVITYVERB = "http://activitystrea.ms/schema/1.0/post";
	private static final String DEFAULT_ACTIVTYOBJECTTYPE = "http://activitystrea.ms/schema/1.0/note";

	private static final Namespace NAMESPACE_XDI = Namespace.getNamespace("xdi", "http://xdi.oasis-open.org");
	private static final Namespace NAMESPACE_ATOM = Namespace.getNamespace("http://www.w3.org/2005/Atom");
	private static final Namespace NAMESPACE_ACTIVITYSTREAMS = Namespace.getNamespace("activity", "http://activitystrea.ms/spec/1.0/");

	private static final Log log = LogFactory.getLog(FeedDictionary.class.getName());

	private FeedDictionary() { }

	/**
	 * Retrieves a feed from a list of XDI subjects plus some extra information.
	 */
	public static SyndFeed toFeed(String xri, XdiContext context, Iterator<Subject> pdsSubjects, String format, String contentType, String hub, String selfEndpoint, String salmonEndpoint) {

		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType(format);

		feed.setTitle(xri);
		feed.setLink("http://xri2xrd.net/" + context.getCanonical());
		feed.setDescription("Feed for " + xri);

		List<org.jdom.Element> foreignElements = new ArrayList<org.jdom.Element> ();
		foreignElements.add(makeLinkElement("alternate", "http://xri2xrd.net/" + context.getCanonical() + "/", "text/html", null));
		foreignElements.add(makeLinkElement("hub", hub, null, "PubSubHubbub"));
		foreignElements.add(makeLinkElement("salmon-reply", salmonEndpoint, null, "Salmon Replies"));
		foreignElements.add(makeLinkElement("salmon-mention", salmonEndpoint, null, "Salmon Mentions"));
		foreignElements.add(makeLinkElement("self", selfEndpoint, contentType, null));
		feed.setForeignMarkup(foreignElements);

		List<SyndEntry> entries = new ArrayList<SyndEntry> ();

		while (pdsSubjects.hasNext()) {

			Subject subject = pdsSubjects.next();

			try {

				SyndEntry entry = toEntry(subject, contentType, selfEndpoint);
				entries.add(entry);

				log.debug("Added entry for " + subject.getSubjectXri());
			} catch (Exception ex) {

				log.warn("Skipping entry " + subject.getSubjectXri() + ": " + ex.getMessage(), ex);
			}
		}

		feed.setEntries(entries);

		return feed;
	}

	/**
	 * Stores a feed entry as an XDI subject.
	 */
	public static void fromEntry(Subject subject, String title, String description, Date publishedDate) {

		subject.createStatement(new XRI3Segment("$d"), Timestamps.dateToXri(publishedDate));
		subject.createStatement(new XRI3Segment("+title"), title);
		subject.createStatement(new XRI3Segment("+description"), title);
	}

	/**
	 * Stores a feed entry as an XDI subject.
	 */
	public static void fromEntry(Subject subject, SyndEntry syndEntry) {

		fromEntry(
				subject,
				syndEntry.getTitle(),
				syndEntry.getDescription().getValue(),
				syndEntry.getPublishedDate());
	}

	/**
	 * Retrieves a feed entry from an XDI subject.
	 */
	public static SyndEntry toEntry(Subject subject, String contentType, String selfEndpoint) throws Exception {

		Date publishedDate = getEntryPublishedDate(subject);
		String title = getEntryTitle(subject);
		String description = getEntryDescription(subject);
		String activityVerb = getEntryActivityVerb(subject);
		String activityObjectType = getEntryActivityObjectType(subject);

		SyndContent syndDescription = new SyndContentImpl();
		syndDescription.setType("text/plain");
		syndDescription.setValue(description);

		SyndEntry syndEntry = new SyndEntryImpl();
		syndEntry.setPublishedDate(publishedDate);
		syndEntry.setTitle(title);
		syndEntry.setDescription(syndDescription);
		syndEntry.setContents(Collections.singletonList(syndDescription));
		syndEntry.setAuthor(subject.getContainingGraph().getPredicate().getSubject().toString());

		List<org.jdom.Element> foreignMarkup = new ArrayList<org.jdom.Element> ();
		foreignMarkup.add(makeIdElement(subject));
		foreignMarkup.add(makeSourceElement(subject, contentType, selfEndpoint));
		foreignMarkup.add(makeXdiElement(subject, "X3 Standard", null));
		foreignMarkup.add(makeActivityVerb(activityVerb));
		foreignMarkup.add(makeActivityObjectType(activityObjectType));
		syndEntry.setForeignMarkup(foreignMarkup);

		return syndEntry;
	}

	/*
	 * Helper methods for reading data from XDI.
	 */

	public static String getEntryTitle(Subject subject) {

		return Addressing.findLiteralData(subject, new XRI3("+title"));
	}

	public static String getEntryDescription(Subject subject) {

		return Addressing.findLiteralData(subject, new XRI3("+description"));
	}

	public static Date getEntryPublishedDate(Subject subject) throws ParseException {

		return Timestamps.xriToDate(Addressing.findReferenceXri(subject, new XRI3("$d")));
	}

	public static String getEntryActivityVerb(Subject subject) {

		String activityVerb = Addressing.findLiteralData(subject, new XRI3("+activity+verb"));

		return activityVerb != null ? activityVerb : DEFAULT_ACTIVITYVERB;
	}

	public static String getEntryActivityObjectType(Subject subject) {

		String activityVerb = Addressing.findLiteralData(subject, new XRI3("+activity+object.type"));

		return activityVerb != null ? activityVerb : DEFAULT_ACTIVTYOBJECTTYPE;
	}

	/*
	 * Helper methods for constructing XML elements.
	 */

	private static org.jdom.Element makeIdElement(Subject subject) {

		org.jdom.Element element;
		element = new org.jdom.Element("id", NAMESPACE_ATOM);
		element.setText(Addressing.getAddress(subject, false).toString());

		return element;
	}

	private static org.jdom.Element makeSourceElement(Subject subject, String contentType, String selfEndpoint) {

		org.jdom.Element element;
		element = new org.jdom.Element("source", NAMESPACE_ATOM);
		org.jdom.Element innerElement1;
		innerElement1 = new org.jdom.Element("id", NAMESPACE_ATOM);
		innerElement1.setText(selfEndpoint);
		element.addContent(innerElement1);
		org.jdom.Element innerElement2;
		innerElement2 = new org.jdom.Element("title", NAMESPACE_ATOM);
		innerElement2.setText(subject.getContainingGraph().getPredicate().getSubject().getSubjectXri().toString());
		element.addContent(innerElement2);
		element.addContent(makeLinkElement("alternate", "http://xri2xrd.net/" + subject.getSubjectXri(), "text/html", null));
		element.addContent(makeLinkElement("self", selfEndpoint, contentType, null));

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

	private static org.jdom.Element makeLinkElement(String rel, String href, String type, String title) {

		org.jdom.Element element;
		element = new org.jdom.Element("link");
		if (rel != null) element.setAttribute("rel", rel);
		if (href != null) element.setAttribute("href", href);
		if (type != null) element.setAttribute("type", type);
		if (title != null) element.setAttribute("title", title);

		return element;
	}
}
