package pds.web.dictionary;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

public class PdsDictionary {

	private static final XRI3Segment[] DICTIONARY_PREDICATES = new XRI3Segment[] {
		
		new XRI3Segment("+name"),
		new XRI3Segment("+street"),
		new XRI3Segment("+postal.code"),
		new XRI3Segment("+city"),
		new XRI3Segment("+state.or.province"),
		new XRI3Segment("+country"),
		new XRI3Segment("+tel"),
		new XRI3Segment("+fax"),
		new XRI3Segment("+email"),
		new XRI3Segment("+gender"),
		new XRI3Segment("+date.of.birth"),
		new XRI3Segment("+language"),
		new XRI3Segment("+timezone")
	};
	
	public static XRI3Segment[] getDictionaryPredicates() {
		
		return DICTIONARY_PREDICATES;
	}
}
