package pds.p2p.node.webshell.webpages;


import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public abstract class BasePage extends WebPage {

	private static final long serialVersionUID = -3724565270246387450L;

	private Label titleLabel;

	public BasePage() {

		// create and add components

		this.titleLabel = new Label("titleLabel", this.getClass().getName());

		this.add(new BookmarkablePageLink<Page> ("homePageLink", Application.get().getHomePage()));
		this.add(new SidePanel("sidePanel"));
		this.add(new DanubeApiStatusPanel("danubeApiStatusPanel"));
		this.add(this.titleLabel);
		this.add(new FeedbackPanel("feedbackPanel"));
	}

	@Override
	public void renderHead(IHeaderResponse response) {

		super.renderHead(response);

		// add css

		response.renderCSSReference("style.css", "screen");

		// add javascript
		
		response.renderJavaScriptReference("lib/modernizr-2.5.3.min.js");
		response.renderJavaScriptReference("lib/jquery-1.7.1.min.js");
	}

	protected void setTitle(String title) {

		this.titleLabel.setDefaultModelObject(title);
	}
}
