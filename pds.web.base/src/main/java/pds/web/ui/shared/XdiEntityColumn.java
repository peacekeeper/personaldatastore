package pds.web.ui.shared;

import java.util.ResourceBundle;

import nextapp.echo.app.Column;
import nextapp.echo.app.Component;
import pds.dictionary.PdsDictionary;
import pds.web.ui.MainWindow;
import pds.web.ui.MessageDialog;
import pds.xdi.XdiEndpoint;
import pds.xdi.events.XdiGraphAddEvent;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import pds.xdi.events.XdiGraphModEvent;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.ContextNode;
import xdi2.core.features.multiplicity.XdiAttribute;
import xdi2.core.features.multiplicity.XdiCollection;
import xdi2.core.features.multiplicity.XdiEntity;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;

public class XdiEntityColumn extends Column implements XdiGraphListener {

	private static final long serialVersionUID = -5106531864010407671L;

	protected ResourceBundle resourceBundle;

	private XdiEndpoint endpoint;
	private XdiEntity xdiEntity;
	private XRI3Segment xdiEntityXri;

	private boolean readOnly;

	/**
	 * Creates a new <code>DataPredicatesColumn</code>.
	 */
	public XdiEntityColumn() {
		super();

		this.readOnly = false;

		// Add design-time configured components.
		initComponents();
	}

	@Override
	public void init() {

		super.init();
	}

	@Override
	public void dispose() {

		super.dispose();

		// remove us as listener

		if (this.endpoint != null) this.endpoint.removeXdiGraphListener(this);
	}

	private void refresh() {

		try {

			// refresh data

			if (this.xdiEntity == null) this.xdiGet();

			// refresh UI

			XRI3Segment[] pdsDictionaryXris = PdsDictionary.DICTIONARY_PERSON;

			this.removeAll();

			for (XRI3Segment pdsDictionaryXri : pdsDictionaryXris) {

				ContextNode contextNode = this.xdiEntity.getContextNode().findContextNode(pdsDictionaryXri, true);

				if (XdiCollection.isValid(contextNode)) {

					this.addXdiCollectionPanel(XdiCollection.fromContextNode(contextNode));
				} else if (XdiAttribute.isValid(contextNode)) {

					this.addXdiAttributePanel(XdiAttribute.fromContextNode(contextNode), pdsDictionaryXri.toString());
				}
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	private void xdiGet() throws Xdi2ClientException {

		// $get

		Message message = this.endpoint.prepareMessage();
		message.createGetOperation(this.xdiEntityXri);

		MessageResult messageResult = this.endpoint.send(message);

		ContextNode contextNode = messageResult.getGraph().findContextNode(this.xdiEntityXri, false);
		if (contextNode == null) this.xdiEntity = null;

		this.xdiEntity = XdiEntity.fromContextNode(contextNode);
	}

	private void addXdiCollectionPanel(XdiCollection xdiCollection) {

		XdiCollectionPanel xdiCollectionPanel = new XdiCollectionPanel();
		xdiCollectionPanel.setEndpointAndXdiCollection(this.endpoint, xdiCollection, xdiCollection.getContextNode().getXri());
		xdiCollectionPanel.setReadOnly(this.readOnly);

		this.add(xdiCollectionPanel);
	}

	private void addXdiAttributePanel(XdiAttribute xdiAttribute, String label) {

		XdiAttributePanel xdiAttributePanel = new XdiAttributePanel();
		xdiAttributePanel.setEndpointAndXdiAttribute(this.endpoint, xdiAttribute, xdiAttribute.getContextNode().getXri(), label);
		xdiAttributePanel.setReadOnly(this.readOnly);

		this.add(xdiAttributePanel);
	}

	public XRI3Segment xdiMainAddress() {

		return this.xdiEntity.getContextNode().getXri();
	}

	public XRI3Segment[] xdiGetAddresses() {

		return new XRI3Segment[0];
	}

	public XRI3Segment[] xdiAddAddresses() {

		return new XRI3Segment[0];
	}

	public XRI3Segment[] xdiModAddresses() {

		return new XRI3Segment[0];
	}

	public XRI3Segment[] xdiDelAddresses() {

		return new XRI3Segment[] {
				this.xdiEntity.getContextNode().getXri()
		};
	}

	public void onXdiGraphEvent(XdiGraphEvent xdiGraphEvent) {

		try {

			if (xdiGraphEvent instanceof XdiGraphAddEvent) {

				this.refresh();
				return;
			}

			if (xdiGraphEvent instanceof XdiGraphModEvent) {

				this.refresh();
				return;
			}

			if (xdiGraphEvent instanceof XdiGraphDelEvent) {

				this.removeAll();
				return;
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	public void setEndpointAndXdiEntity(XdiEndpoint endpoint, XdiEntity xdiEntity, XRI3Segment xdiEntityXri) {

		// remove us as listener

		if (this.endpoint != null) this.endpoint.removeXdiGraphListener(this);

		// refresh

		this.endpoint = endpoint;
		this.xdiEntity = xdiEntity;
		this.xdiEntityXri = xdiEntityXri;

		this.refresh();

		// add us as listener

		this.endpoint.addXdiGraphListener(this);
	}

	public void setReadOnly(boolean readOnly) {

		this.readOnly = readOnly;

		for (Component component : MainWindow.findChildComponentsByClass(this, XdiCollectionPanel.class)) {

			((XdiCollectionPanel) component).setReadOnly(readOnly);
		}
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
	}
}
