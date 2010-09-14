package pds.endpoint.poco;


public class Poco {

	private String id;
	private String profileUrl;
	private String preferredUsername;
	private String displayName;
	private String nameFormatted;
	private String birthday;
	private String gender;
	private String email;
	private String url;

	public Poco(String id, String profileUrl, String preferredUsername, String displayName, String nameFormatted, String birthday, String gender, String email, String url) {

		this.id = id;
		this.profileUrl = profileUrl;
		this.preferredUsername = preferredUsername;
		this.displayName = displayName;
		this.nameFormatted = nameFormatted;
		this.birthday = birthday;
		this.gender = gender;
		this.email = email;
		this.url = url;
	}

	public String toJSON() {

		StringBuffer buffer = new StringBuffer();

		buffer.append("{\n");
		buffer.append("  \"entry\": {\n");

		if (this.profileUrl != null) buffer.append("    \"profileUrl\": \"" + this.profileUrl + "\",\n");

		if (this.preferredUsername != null) buffer.append("    \"preferredUsername\": \"" + this.preferredUsername + "\",\n");

		if (this.displayName != null) buffer.append("    \"displayName\": \"" + this.displayName + "\",\n");

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

		if (this.url != null) {

			buffer.append("    \"urls\": [ {\n");
			buffer.append("      \"value\": \"" + this.url + "\",\n");
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

		if (this.profileUrl != null) buffer.append("    <profileUrl>" + this.profileUrl + "</profileUrl>\n");

		if (this.preferredUsername != null) buffer.append("    <preferredUsername>" + this.preferredUsername + "</preferredUsername>\n");

		if (this.displayName != null) buffer.append("    <displayName>" + this.displayName + "</displayName>\n");

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

		if (this.email != null) {

			buffer.append("    <urls>\n");
			buffer.append("      <value>" + this.url + "</value>\n");
			buffer.append("      <primary>true</primary>\n");
			buffer.append("    </urls>\n");
		}

		if (this.id != null) buffer.append("    <id>" + this.id + "</id>\n");

		buffer.append("  </entry>\n");
		buffer.append("</response>\n");

		return buffer.toString();
	}
}
