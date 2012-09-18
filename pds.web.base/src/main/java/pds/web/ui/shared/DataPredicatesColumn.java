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
import xdi2.core.xri3.impl.XRI3Segment;

public class DataPredicatesColumn extends Column implements XdiGraphListener {

	private static final long serialVersionUID = -5106531864010407671L;

	protected ResourceBundle resourceBundle;

	private XdiEndpoint endpoint;
	private XRI3Segment contextNodeXri;
	private XRI3Segment address;

	private boolean readOnly;

	/**
	 * Creates a new <code>DataPredicatesColumn</code>.
	 */
	public DataPredicatesColumn() {
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

			// get list of data predicate XRIs

			XRI3Segment[] pdsDictionaryPredicates = PdsDictionary.DICTIONARY_PREDICATES;

			// add them

			this.removeAll();
			for (XRI3Segment dataPredicateXri : pdsDictionaryPredicates) {

				this.addDataAttributePanel(dataPredicateXri);
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	private void addDataAttributePanel(XRI3Segment dataAttributeXri) {

		DataAttributePanel dataAttributePanel = new DataAttributePanel();
		dataAttributePanel.setEndpointAndContextNodeXriAndAttributeXri(this.endpoint, this.contextNodeXri, dataAttributeXri);
		dataAttributePanel.setReadOnly(this.readOnly);

		this.add(dataAttributePanel);
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
				this.address
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

	public void setEndpointAndContextNodeXri(XdiEndpoint endpoint, XRI3Segment contextNodeXri) {

		// remove us as listener
		
		if (this.endpoint != null) this.endpoint.removeXdiGraphListener(this);

		// refresh
		
		this.endpoint = endpoint;
		this.contextNodeXri = contextNodeXri;
		this.address = new XRI3Segment("" + this.contextNodeXri);

		this.refresh();

		// add us as listener

		this.endpoint.addXdiGraphListener(this);
	}

	public void setReadOnly(boolean readOnly) {

		this.readOnly = readOnly;

		for (Component component : MainWindow.findChildComponentsByClass(this, DataAttributePanel.class)) {

			((DataAttributePanel) component).setReadOnly(readOnly);
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
