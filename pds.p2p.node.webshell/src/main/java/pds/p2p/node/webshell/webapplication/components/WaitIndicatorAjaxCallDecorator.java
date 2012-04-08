package pds.p2p.node.webshell.webapplication.components;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.IAjaxCallDecorator;

public class WaitIndicatorAjaxCallDecorator implements IAjaxCallDecorator {

	private static final long serialVersionUID = 1052715487825535602L;

	public CharSequence decorateScript(Component component, CharSequence script) {

		return "Spinner.show('" + component.getMarkupId() + "');" + script;
	}

	public CharSequence decorateOnSuccessScript(Component component, CharSequence script) {

		return "Spinner.hide('" + component.getMarkupId() + "');" + script;
	}

	public CharSequence decorateOnFailureScript(Component component, CharSequence script) {

		return "Spinner.hide('" + component.getMarkupId() + "');" + script;
	}
}
