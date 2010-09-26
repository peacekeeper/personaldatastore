package pds.web.ui.app.feed.components;


import java.util.Iterator;
import java.util.ResourceBundle;

import nextapp.echo.app.Column;
import nextapp.echo.app.Label;
import nextapp.echo.app.event.ActionEvent;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.addressing.Addressing;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.multivalue.MultiSubjects;
import org.eclipse.higgins.xdi4j.util.iterators.EmptyIterator;
import org.eclipse.higgins.xdi4j.util.iterators.MappingSubjectXrisIterator;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.ui.MessageDialog;
import pds.web.ui.app.feed.components.EntryPanel.EntryPanelDelegate;
import pds.xdi.XdiContext;
import pds.xdi.XdiException;
import pds.xdi.events.XdiGraphAddEvent;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import pds.xdi.events.XdiGraphModEvent;

public class EntriesColumn extends Column implements XdiGraphListener {

	private static final long serialVersionUID = -5106531864010407671L;

	protected ResourceBundle resourceBundle;

	private XdiContext context;
	private XRI3 address;

	private Label emptyLabel;

	/**
	 * Creates a new <code>DataPredicatesColumn</code>.
	 */
	public EntriesColumn() {
		super();

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

			// get list of entry XRIs

			Iterator<XRI3Segment> entryXris = this.getEntryXris();

			// add them

			this.removeAll();

			if (entryXris.hasNext()) {

				while (entryXris.hasNext()) {

					XRI3Segment entryXri = entryXris.next();

					this.addEntryPanel(entryXri);
				}
			} else {

				this.add(this.emptyLabel);
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	private void addEntryPanel(XRI3Segment entryXri) {

		EntryPanel entryPanel = new EntryPanel();
		entryPanel.setContextAndAddress(this.context, new XRI3(this.address + "//" + entryXri));
		entryPanel.setEntryPanelDelegate(new EntryPanelDelegate() {

			@Override
			public void onReplyActionPerformed(ActionEvent e) {

			}
		});

		this.add(entryPanel, 0);
	}

	public XRI3[] xdiGetAddresses() {

		return new XRI3[] {
				this.address
		};
	}

	public XRI3[] xdiAddAddresses() {

		return new XRI3[] {
				new XRI3("" + this.address + "/$$")
		};
	}

	public XRI3[] xdiModAddresses() {

		return new XRI3[0];
	}

	public XRI3[] xdiSetAddresses() {

		return new XRI3[0];
	}

	public XRI3[] xdiDelAddresses() {

		return new XRI3[] {
				new XRI3("" + this.address)
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

	public void setContextAndAddress(XdiContext context, XRI3 address) {

		// remove us as listener

		if (this.context != null) this.context.removeXdiGraphListener(this);

		// refresh

		this.context = context;
		this.address = address;

		this.refresh();

		// add us as listener

		this.context.addXdiGraphListener(this);
	}

	private Iterator<XRI3Segment> getEntryXris() throws XdiException {

		// $get

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, this.address);
		MessageResult messageResult = this.context.send(operation);

		Graph innerGraph = Addressing.findInnerGraph(messageResult.getGraph(), this.address);
		if (innerGraph == null) return new EmptyIterator<XRI3Segment> ();

		return new MappingSubjectXrisIterator(MultiSubjects.getMultiSubjects(innerGraph, new XRI3Segment("+entry")));
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		emptyLabel = new Label();
		emptyLabel.setStyleName("Default");
		emptyLabel.setText("There are no updates right now.");
		add(emptyLabel);
	}
}
