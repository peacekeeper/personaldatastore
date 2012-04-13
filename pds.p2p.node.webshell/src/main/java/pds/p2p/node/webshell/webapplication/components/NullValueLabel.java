package pds.p2p.node.webshell.webapplication.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

public class NullValueLabel extends Label {

	private static final long serialVersionUID = 7104728302728008862L;

	private String nullValue;

	public NullValueLabel(String id, IModel<?> model, String nullValue) {

		super(id, model);

		this.nullValue = nullValue;
	}

	public NullValueLabel(String id, String label, String nullValue) {

		super(id, label);

		this.nullValue = nullValue;
	}

	public NullValueLabel(String id, String nullValue) {

		super(id);

		this.nullValue = nullValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag)
	{
		String defaultModelObjectAsString = getDefaultModelObjectAsString();

		if (defaultModelObjectAsString != null && (! defaultModelObjectAsString.isEmpty())) {

			replaceComponentTagBody(markupStream, openTag, defaultModelObjectAsString);
		} else {

			replaceComponentTagBody(markupStream, openTag, this.nullValue);
		}
	}
}
