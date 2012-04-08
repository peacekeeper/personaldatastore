package pds.p2p.node.webshell.webapplication.behaviors;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;

public class DefaultFocusBehavior extends Behavior {

	private static final long serialVersionUID = -7405784136737136057L;

	@Override
	public void bind(Component component) {

		component.setOutputMarkupId(true);
	}

	@Override
	public void renderHead(Component component, IHeaderResponse response) {

		super.renderHead(component, response);

		String javaScript = "document.getElementById('" + component.getMarkupId() + "').focus();";
		response.renderOnDomReadyJavaScript(javaScript);
	}
}
