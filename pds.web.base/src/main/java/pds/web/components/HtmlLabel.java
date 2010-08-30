package pds.web.components;

import nextapp.echo.app.Component;

public class HtmlLabel extends Component {

	private static final long serialVersionUID = 2512604148813751228L;

	public static final String PROPERTY_HTML = "html";

	public HtmlLabel() {
		this("");
	}

	public HtmlLabel(String html) {
		setHtml(html);
	}

	public String getHtml() {
		return (String) get(PROPERTY_HTML);
	}

	public void setHtml(String newValue) {
		set(PROPERTY_HTML, newValue);
	}
}
