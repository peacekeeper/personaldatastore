package pds.p2p.node.webshell.webpages;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.time.Duration;

import pds.p2p.node.webshell.webapplication.components.NullValueLabel;
import pds.p2p.node.webshell.webapplication.models.InameModel;
import pds.p2p.node.webshell.webapplication.models.InumberModel;
import pds.p2p.node.webshell.webapplication.models.NeighborsModel;
import pds.p2p.node.webshell.webapplication.models.NodeIdModel;
import pds.p2p.node.webshell.webpages.terminals.ShellTerminal;
import pds.p2p.node.webshell.webpages.terminals.XDITerminal;

public class DanubeApiStatusPanel extends Panel {

	private static final long serialVersionUID = 8002365033991382512L;

	private Label nodeIdLabel;
	private Label neighborsLabel;
	private Label inameLabel;
	private Label inumberLabel;

	public DanubeApiStatusPanel(String id) {

		super(id);

		this.nodeIdLabel = new NullValueLabel("nodeIdLabel", new NodeIdModel(), "(not connected)");
		this.neighborsLabel = new NullValueLabel("neighborsLabel", new NeighborsModel(), "(not connected)");
		this.inameLabel = new NullValueLabel("inameLabel", new InameModel(), "(not logged in)");
		this.inumberLabel = new NullValueLabel("inumberLabel", new InumberModel(), "(not logged in)");

		// create and add components

		this.add(this.nodeIdLabel);
		this.add(this.neighborsLabel);
		this.add(this.inameLabel);
		this.add(this.inumberLabel);
		this.add(new BookmarkablePageLink<Page> ("ShellTerminalLink", ShellTerminal.class));
		this.add(new BookmarkablePageLink<Page> ("XDITerminalLink", XDITerminal.class));

		this.setOutputMarkupId(true);
		this.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)) {

			private static final long serialVersionUID = 1427947950380198205L;

/*			@Override
			protected IAjaxCallDecorator getAjaxCallDecorator() {

				return new WaitIndicatorAjaxCallDecorator();
			}*/
		});
	}

	@Override
	public void renderHead(IHeaderResponse response) {

		super.renderHead(response);

		// add css

//		response.renderCSSReference(new CssResourceReference(WaitIndicatorAjaxCallDecorator.class, "WaitIndicatorAjaxCallDecorator.css"));

		// add javascript
		
//		response.renderJavaScriptReference(new JavaScriptResourceReference(WaitIndicatorAjaxCallDecorator.class, "WaitIndicatorAjaxCallDecorator.js"));
	}
}
