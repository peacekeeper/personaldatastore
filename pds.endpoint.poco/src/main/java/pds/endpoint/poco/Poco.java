package pds.endpoint.poco;


public class Poco {

	public static final String FORMAT_JSON = "json";
	public static final String FORMAT_XML = "xml";

	private String id;
	private String profileurl;
	private String displayname;
	private String nameFormatted;
	private String birthday;
	private String gender;
	private String email;

	public Poco(String id, String profileurl, String displayname, String nameFormatted, String birthday, String gender, String email) {

		this.id = id;
		this.profileurl = profileurl;
		this.displayname = displayname;
		this.nameFormatted = nameFormatted;
		this.birthday = birthday;
		this.gender = gender;
		this.email = email;
	}

	public String toJSON() {

		StringBuffer buffer = new StringBuffer();

		buffer.append("{\n");
		buffer.append("  \"entry\": {\n");

		if (this.profileurl != null) buffer.append("    \"profileUrl\": \"" + this.profileurl + "\",\n");

		if (this.displayname != null) buffer.append("    \"displayName\": \"" + this.displayname + "\",\n");

		if (this.nameFormatted != null) {

			buffer.append("    \"name\": {\n");
			buffer.append("      \"formatted\": \"" + this.nameFormatted + "\"\n");
			buffer.append("    },\n");
		}

		if (this.birthday != null) buffer.append("    \"birthday\": \"" + this.birthday + "\",\n");

		if (this.gender != null) buffer.append("    \"gender\": \"" + this.gender + "\",\n");

		if (this.email != null) {

			buffer.append("    \"emails\": [ {\n");
			buffer.append("      \"value\": \"" + this.email + "\",\n");
			buffer.append("      \"primary\": \"true\"\n");
			buffer.append("    } ],\n");
		}

		if (this.id != null) buffer.append("    \"id\": \"" + this.id + "\"\n");

		buffer.append("  }\n");
		buffer.append("}\n");

		return buffer.toString();
	}

	public String toXML() {

		StringBuffer buffer = new StringBuffer();

		buffer.append("<response>\n");
		buffer.append("  <entry>\n");

		if (this.profileurl != null) buffer.append("    <profileUrl>" + this.profileurl + "</profileUrl>\n");

		if (this.displayname != null) buffer.append("    <displayName>" + this.displayname + "</displayName>\n");

		if (this.nameFormatted != null) {

			buffer.append("    <name>\n");
			buffer.append("      <formatted>" + this.nameFormatted + "</formatted>\n");
			buffer.append("    </name>\n");
		}

		if (this.birthday != null) buffer.append("    <birthday>" + this.birthday + "</birthday>\n");

		if (this.gender != null) buffer.append("    <gender>" + this.gender + "</gender>\n");

		if (this.email != null) {

			buffer.append("    <emails>\n");
			buffer.append("      <value>" + this.email + "</value>\n");
			buffer.append("      <primary>true</primary>\n");
			buffer.append("    </emails>\n");
		}

		if (this.id != null) buffer.append("    <id>" + this.id + "</id>\n");

		buffer.append("  </entry>\n");
		buffer.append("</response>\n");

		return buffer.toString();
	}

	public String toString(String format) {

		if (FORMAT_JSON.equals(format)) return this.toJSON();
		if (FORMAT_XML.equals(format)) return this.toXML();

		return this.toString();
	}
}
