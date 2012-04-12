package pds.p2p.node.webshell.webpages;

import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.DanubeApiShellServlet;

public class DanubeApiStatusPanel extends Panel {

	private static final long serialVersionUID = 8002365033991382512L;

	private static Logger log = LoggerFactory.getLogger(DanubeApiShellServlet.class);

	private Label nodeIdLabel;
	private Label inameLabel;
	private Label inumberLabel;

	public DanubeApiStatusPanel(String id) {

		super(id);

		this.nodeIdLabel = new Label("nodeIdLabel", new NodeIdModel());
		this.inameLabel = new Label("inameLabel", new InameModel());
		this.inumberLabel = new Label("inumberLabel", new InumberModel());

		// create and add components

		this.add(this.nodeIdLabel);
		this.add(this.inameLabel);
		this.add(this.inumberLabel);

		this.setOutputMarkupId(true);
		this.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(10)) {

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

	private static final class NodeIdModel extends AbstractReadOnlyModel<String> {

		private static final long serialVersionUID = -1004143566184261706L;

		@Override
		public String getObject() {
			try {

				return DanubeApiClient.vegaObject.nodeId();
			} catch (Exception ex) {

				log.warn(ex.getMessage(), ex);
				return "(none)";
			}
		}
	}

	private static final class InameModel extends AbstractReadOnlyModel<String> {

		private static final long serialVersionUID = 6399606689894790735L;

		@Override
		public String getObject() {

			try {

				return DanubeApiClient.orionObject.iname();
			} catch (Exception ex) {

				log.warn(ex.getMessage(), ex);
				return "(none)";
			}
		}
	}

	private static final class InumberModel extends AbstractReadOnlyModel<String> {

		private static final long serialVersionUID = 602197041909086712L;

		@Override
		public String getObject() {

			try {

				return DanubeApiClient.orionObject.inumber();
			} catch (Exception ex) {

				log.warn(ex.getMessage(), ex);
				return "(none)";
			}
		}
	}
}
