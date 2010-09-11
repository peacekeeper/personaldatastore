package pds.dictionary.feed;

import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.addressing.Addressing;
import org.eclipse.higgins.xdi4j.types.Timestamps;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;

/**
 * Dictionary methods for representing Atom/ActivityStreams feeds in XDI.
 */
public class FeedDictionary {

	private FeedDictionary() { }

	public static void toSubject(Subject subject, String title, String description, Date publishedDate) {

		subject.createStatement(new XRI3Segment("$d"), Timestamps.dateToXri(publishedDate));
		subject.createStatement(new XRI3Segment("+title"), title);
		subject.createStatement(new XRI3Segment("+description"), title);
	}

	public static void toSubject(Subject subject, SyndEntry syndEntry) {

		toSubject(
				subject,
				syndEntry.getTitle(),
				syndEntry.getDescription().getValue(),
				syndEntry.getPublishedDate());
	}

	public static SyndEntry fromSubject(Subject subject) throws Exception {

		Date publishedDate = getEntryPublishedDate(subject);
		String title = getEntryTitle(subject);
		String description = getEntryDescription(subject);

		SyndContent syndDescription = new SyndContentImpl();
		syndDescription.setType("text/plain");
		syndDescription.setValue(description);

		SyndEntry syndEntry = new SyndEntryImpl();
		syndEntry.setPublishedDate(publishedDate);
		syndEntry.setTitle(title);
		syndEntry.setForeignMarkup(makeXdiElement(subject, "X3 Standard", null));
		syndEntry.setDescription(syndDescription);

		return syndEntry;
	}

	public static String getEntryTitle(Subject subject) {

		return Addressing.findLiteralData(subject, new XRI3("+title"));
	}

	public static String getEntryDescription(Subject subject) {

		return Addressing.findLiteralData(subject, new XRI3("+description"));
	}

	public static Date getEntryPublishedDate(Subject subject) throws ParseException {

		return Timestamps.xriToDate(Addressing.findReferenceXri(subject, new XRI3("$d")));
	}

	private static org.jdom.Element makeXdiElement(Subject subject, String format, Properties properties) {

		org.jdom.Element element;
		element = new org.jdom.Element("xdi");
		if (format != null) element.setAttribute("format", format);
		element.setText(subject.toString(format, properties));

		return element;
	}
}
