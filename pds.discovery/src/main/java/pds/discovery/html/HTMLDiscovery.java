package pds.discovery.html;

import java.net.URI;
import java.util.List;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.tags.BodyTag;
import org.htmlparser.tags.HeadTag;
import org.htmlparser.visitors.NodeVisitor;
import org.opensaml.xml.util.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.discovery.util.DiscoveryUtil;

/**
 * This class can:
 * 
 * - Discover a set of <Link>s from an http(s):// URL.
 * - Select a <Link> with a given rel and type from a set of <Link>s.
 */
public class HTMLDiscovery {

	private static final Logger log = LoggerFactory.getLogger(HTMLDiscovery.class);

	private HTMLDiscovery() { }

	public static List<Tag> discoverLinks(URI uri) throws Exception {

		if (uri == null) throw new NullPointerException("No URI provided.");

		log.debug("Trying to discover <Link>s from " + uri);

		String content = DiscoveryUtil.getContents(uri);
		Parser htmlParser = Parser.createParser(content, null);

		LinkVisitor linkVisitor = new LinkVisitor();
		htmlParser.visitAllNodesWith(linkVisitor);

		// done

		log.debug(linkVisitor.getLinks().size() + " links");

		return linkVisitor.getLinks();
	}

	public static URI selectLinkHref(URI uri, List<Tag> links, String rel, String type) throws Exception {

		if (links == null || rel == null) throw new NullPointerException("No <Link>s or rel provided.");

		log.debug("Looking for <Link> with rel=" + rel + " and type=" + type);

		for (Tag tag : links) {

			String[] linkRels = tag.getAttribute("rel") != null ? tag.getAttribute("rel").split(" ") : new String[] { null };
			String[] linkTypes = tag.getAttribute("type") != null ? tag.getAttribute("type").split(" ") : new String[] { null };

			for (String linkRel : linkRels) {

				for (String linkType : linkTypes) {

					log.debug("Trying to match <Link> with rel=" + linkRel + " and type="  + linkType);

					if (rel.equals(linkRel) &&
							(type == null || type.equals(linkType))) {

						URI href = URI.create(tag.getAttribute("href"));
						if (! href.isAbsolute()) href = uri.resolve(href);

						log.debug("Match! URI: " + href.toString());

						return href;
					}
				}
			}
		}

		log.debug("No matching <Link>");

		return null;
	}

	private static class LinkVisitor extends NodeVisitor {

		private boolean inHead;
		private List<Tag> links;

		public LinkVisitor() {

			this.inHead = false;
			this.links = new LazyList<Tag>();
		}

		public void visitTag(Tag tag) {

			if (tag instanceof HeadTag) {

				this.inHead = true;
			} else if (tag instanceof BodyTag) {

				this.inHead = false;
			} else if (tag.getTagName().equalsIgnoreCase("link")) {

				if (this.inHead) this.links.add(tag);
			}
		}

		public void visitEndTag(Tag tag) {

			if (tag.getTagName().equalsIgnoreCase("head")) this.inHead = false;
		}

		public List<Tag> getLinks() {

			return this.links;
		}
	}
}
