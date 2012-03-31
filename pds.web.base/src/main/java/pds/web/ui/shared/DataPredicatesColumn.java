package pds.web.ui.shared;

import java.util.ResourceBundle;

import nextapp.echo.app.Column;
import nextapp.echo.app.Component;

import org.eclipse.higgins.XDI2.xri3.impl.XRI3;
import org.eclipse.higgins.XDI2.xri3.impl.XRI3Segment;

import pds.dictionary.PdsDictionary;
import pds.web.ui.MainWindow;
import pds.web.ui.MessageDialog;
import pds.xdi.XdiContext;
import pds.xdi.events.XdiGraphAddEvent;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import pds.xdi.events.XdiGraphModEvent;

public class DataPredicatesColumn extends Column implements XdiGraphListener {

	private static final long serialVersionUID = -5106531864010407671L;

	protected ResourceBundle resourceBundle;

	private XdiContext context;
	private XRI3Segment subjectXri;
	private XRI3 address;

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
		
		if (this.context != null) this.context.removeXdiGraphListener(this);
	}

	private void refresh() {

		try {

			// get list of data predicate XRIs

			XRI3Segment[] pdsDictionaryPredicates = PdsDictionary.DICTIONARY_PREDICATES;

			// add them

			this.removeAll();
			for (XRI3Segment dataPredicateXri : pdsDictionaryPredicates) {

				this.addDataPredicatePanel(dataPredicateXri);
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	private void addDataPredicatePanel(XRI3Segment dataPredicateXri) {

		DataPredicatePanel dataPredicatePanel = new DataPredicatePanel();
		dataPredicatePanel.setContextAndSubjectXriAndPredicateXri(this.context, this.subjectXri, dataPredicateXri);
		dataPredicatePanel.setReadOnly(this.readOnly);

		this.add(dataPredicatePanel);
	}

	public XRI3[] xdiGetAddresses() {

		return new XRI3[0];
	}

	public XRI3[] xdiAddAddresses() {

		return new XRI3[0];
	}

	public XRI3[] xdiModAddresses() {

		return new XRI3[0];
	}

	public XRI3[] xdiSetAddresses() {

		return new XRI3[0];
	}

	public XRI3[] xdiDelAddresses() {

		return new XRI3[] {
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

	public void setContextAndSubjectXri(XdiContext context, XRI3Segment subjectXri) {

		// remove us as listener
		
		if (this.context != null) this.context.removeXdiGraphListener(this);

		// refresh
		
		this.context = context;
		this.subjectXri = subjectXri;
		this.address = new XRI3("" + this.subjectXri);

		this.refresh();

		// add us as listener

		this.context.addXdiGraphListener(this);
	}

	public void setReadOnly(boolean readOnly) {

		this.readOnly = readOnly;

		for (Component component : MainWindow.findChildComponentsByClass(this, DataPredicatePanel.class)) {

			((DataPredicatePanel) component).setReadOnly(readOnly);
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
