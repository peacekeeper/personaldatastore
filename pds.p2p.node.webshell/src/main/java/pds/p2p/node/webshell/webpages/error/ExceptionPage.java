package pds.p2p.node.webshell.webpages.error;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.cycle.RequestCycle;

import pds.p2p.node.webshell.webpages.BasePage;

public class ExceptionPage extends BasePage {

	private static final long serialVersionUID = -3288550596918043901L;
	private WebMarkupContainer exMessageContainer;
	private WebMarkupContainer exTraceContainer;
	private Label exClassLabel;
	private Label exTimeLabel;
	private Label exPathLabel;
	private Label exMessageLabel;
	private Label exTraceLabel;

	public ExceptionPage(RequestCycle requestCycle, Exception ex) {

		this.setTitle(this.getString("title"));

		// create components

		StringWriter writer = new StringWriter();
		ex.printStackTrace(new PrintWriter(writer));

		this.exClassLabel = new Label("exClass", ex.getClass().getSimpleName());
		this.exTimeLabel = new Label("exTime", new Date(requestCycle.getStartTime()).toString());
		this.exPathLabel = new Label("exPath", requestCycle.getRequest().getOriginalUrl().toString());
		this.exMessageContainer = new WebMarkupContainer("exMessageContainer");
		this.exMessageContainer.setVisible(ex.getMessage() != null && ex.getMessage().trim().length() > 0);
		this.exMessageLabel = new Label("exMessage", ex.getMessage());
		this.exTraceContainer = new WebMarkupContainer("exTraceContainer");
		this.exTraceContainer.setOutputMarkupId(true);
		this.exTraceLabel = new Label("exTrace", writer.toString());
		this.exTraceLabel.setVisible(false);

		// add components

		this.exMessageContainer.add(this.exMessageLabel);
		this.exTraceContainer.add(this.exTraceLabel);
		this.add(this.exClassLabel);
		this.add(this.exTimeLabel);
		this.add(this.exPathLabel);
		this.add(this.exMessageContainer);
		this.add(this.exTraceContainer);
		this.add(new AjaxFallbackLink<String> ("showTraceLink") {

			private static final long serialVersionUID = 596150019852780008L;

			@Override
			public void onClick(AjaxRequestTarget target) {

				ExceptionPage.this.exTraceLabel.setVisible(! ExceptionPage.this.exTraceLabel.isVisible());

				if (target != null) target.add(ExceptionPage.this.exTraceContainer);
			}
		});
	}

	@Override
	public boolean isErrorPage() {

		return(true);
	}
}
