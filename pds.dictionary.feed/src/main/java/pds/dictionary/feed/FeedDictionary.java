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
import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.addressing.Addressing;
import org.eclipse.higgins.xdi4j.types.Timestamps;
import org.eclipse.higgins.xdi4j.util.iterators.SelectingIterator;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.jdom.Namespace;

import pds.dictionary.PdsDictionary;
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
	private static final String DEFAULT_ACTIVITYOBJECTTYPE = "http://activitystrea.ms/schema/1.0/note";

	private static final Namespace NAMESPACE_ATOM = Namespace.getNamespace("http://www.w3.org/2005/Atom");
	private static final Namespace NAMESPACE_XDI = Namespace.getNamespace("xdi", "http://xdi.oasis-open.org");
	private static final Namespace NAMESPACE_ACTIVITYSTREAMS = Namespace.getNamespace("activity", "http://activitystrea.ms/spec/1.0/");
	private static final Namespace NAMESPACE_POCO = Namespace.getNamespace("poco", "http://portablecontacts.net/spec/1.0");

	private static final Log log = LogFactory.getLog(FeedDictionary.class.getName());

	private FeedDictionary() { }

	/**
	 * Retrieves a feed from a list of XDI subjects plus some extra information.
	 */
	public static SyndFeed toFeed(String xri, XdiContext context, Subject pdsSubject, String format, String contentType, String hub, String selfEndpoint, String salmonEndpoint) {

		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType(format);

		feed.setTitle(xri);
		feed.setLink("http://xri2xrd.net/" + context.getCanonical());
		feed.setDescription("Feed for " + xri);

		List<org.jdom.Element> foreignElements = new ArrayList<org.jdom.Element> ();
		foreignElements.add(makeActivitySubject(pdsSubject));
		foreignElements.add(makeLink("alternate", "http://xri2xrd.net/" + context.getCanonical() + "/", "text/html", null));
		foreignElements.add(makeLink("hub", hub, null, "PubSubHubbub"));
		foreignElements.add(makeLink("salmon-reply", salmonEndpoint, null, "Salmon Replies"));
		foreignElements.add(makeLink("salmon-mention", salmonEndpoint, null, "Salmon Mentions"));
		foreignElements.add(makeLink("self", selfEndpoint, contentType, null));
		feed.setForeignMarkup(foreignElements);

		List<SyndEntry> entries = new ArrayList<SyndEntry> ();

		Predicate predicate = pdsSubject.getPredicate(new XRI3Segment("+ostatus+feed"));
		if (predicate == null) return null;

		Graph innerGraph = predicate.getInnerGraph();
		if (innerGraph == null) return null;

		Iterator<Subject> entrySubjects = new SelectingIterator<Subject> (innerGraph.getSubjects()) {

			@Override
			public boolean select(Subject entrySuubject) {

				return entrySuubject.getSubjectXri().toString().startsWith("+entry$");
			}
		};

		while (entrySubjects.hasNext()) {

			Subject entrySubject = entrySubjects.next();

			try {

				SyndEntry entry = toEntry(pdsSubject, entrySubject, contentType, selfEndpoint);
				entries.add(entry);

				log.debug("Added entry for " + entrySubject.getSubjectXri());
			} catch (Exception ex) {

				log.warn("Skipping entry " + entrySubject.getSubjectXri() + ": " + ex.getMessage(), ex);
			}
		}

		feed.setEntries(entries);

		return feed;
	}

	/**
	 * Stores a feed entry as an XDI subject.
	 */
	public static void fromEntry(Subject entrySubject, String title, String description, Date publishedDate, String activityVerb, String activityObjectType) {

		if (activityVerb == null) activityVerb = DEFAULT_ACTIVITYVERB;
		if (activityObjectType == null) activityObjectType = DEFAULT_ACTIVITYOBJECTTYPE;
		
		if (publishedDate != null) entrySubject.createStatement(new XRI3Segment("$d"), Timestamps.dateToXri(publishedDate));
		if (title != null) entrySubject.createStatement(new XRI3Segment("+title"), title);
		if (description != null) entrySubject.createStatement(new XRI3Segment("+description"), description);
		if (activityVerb != null) entrySubject.createStatement(new XRI3Segment("+activity+verb"), activityVerb);
		if (activityObjectType != null) entrySubject.createStatement(new XRI3Segment("+activity+object.type"), activityObjectType);
	}

	/**
	 * Stores a feed entry as an XDI subject.
	 */
	@SuppressWarnings("unchecked")
	public static void fromEntry(Subject entrySubject, SyndEntry syndEntry) {

		String title = syndEntry.getTitle();
		String description = syndEntry.getDescription() == null ? null : syndEntry.getDescription().getValue();
		Date publishedDate = syndEntry.getPublishedDate();

		String activityVerb = null;
		String activityObjectType = null;
		List<org.jdom.Element> foreignMarkup = (List<org.jdom.Element>) syndEntry.getForeignMarkup();
		for (org.jdom.Element foreignElement : foreignMarkup) {
			
			if (foreignElement.getNamespace().equals(NAMESPACE_ACTIVITYSTREAMS) && foreignElement.getName().equals("verb")) activityVerb = foreignElement.getText();
			if (foreignElement.getNamespace().equals(NAMESPACE_ACTIVITYSTREAMS) && foreignElement.getName().equals("object-type")) activityObjectType = foreignElement.getText();
		}

		fromEntry(entrySubject, title, description, publishedDate, activityVerb, activityObjectType);
	}

	/**
	 * Retrieves a feed entry from an XDI subject.
	 */
	public static SyndEntry toEntry(Subject pdsSubject, Subject entrySubject, String contentType, String selfEndpoint) throws Exception {

		Date publishedDate = getEntryPublishedDate(entrySubject);
		String title = getEntryTitle(entrySubject);
		String description = getEntryDescription(entrySubject);
		String activityVerb = getEntryActivityVerb(entrySubject);
		String activityObjectType = getEntryActivityObjectType(entrySubject);

		SyndContent syndDescription = new SyndContentImpl();
		syndDescription.setType("text/plain");
		syndDescription.setValue(description);

		SyndEntry syndEntry = new SyndEntryImpl();
		syndEntry.setPublishedDate(publishedDate);
		syndEntry.setTitle(title);
		syndEntry.setDescription(syndDescription);
		syndEntry.setContents(Collections.singletonList(syndDescription));
		syndEntry.setAuthor(entrySubject.getContainingGraph().getPredicate().getSubject().toString());

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
	 * Helper methods for reading data from XDI.
	 */

	public static String getEntryTitle(Subject entrySubject) {

		return Addressing.findLiteralData(entrySubject, new XRI3("+title"));
	}

	public static String getEntryDescription(Subject entrySubject) {

		return Addressing.findLiteralData(entrySubject, new XRI3("+description"));
	}

	public static Date getEntryPublishedDate(Subject entrySubject) throws ParseException {

		return Timestamps.xriToDate(Addressing.findReferenceXri(entrySubject, new XRI3("$d")));
	}

	public static String getEntryActivityVerb(Subject entrySubject) {

		String activityVerb = Addressing.findLiteralData(entrySubject, new XRI3("+activity+verb"));

		return activityVerb != null ? activityVerb : DEFAULT_ACTIVITYVERB;
	}

	public static String getEntryActivityObjectType(Subject entrySubject) {

		String activityVerb = Addressing.findLiteralData(entrySubject, new XRI3("+activity+object.type"));

		return activityVerb != null ? activityVerb : DEFAULT_ACTIVITYOBJECTTYPE;
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

		String preferredUsername = Addressing.findLiteralData(pdsSubject, new XRI3("$" + PdsDictionary.XRI_NAME.toString()));
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
