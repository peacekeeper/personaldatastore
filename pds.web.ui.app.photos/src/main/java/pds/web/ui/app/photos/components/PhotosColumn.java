package pds.web.ui.app.photos.components;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.security.auth.Subject;
import javax.sql.rowset.Predicate;

import nextapp.echo.app.Column;
import pds.web.ui.MessageDialog;
import pds.xdi.XdiEndpoint;
import pds.xdi.XdiException;
import pds.xdi.events.XdiGraphAddEvent;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import pds.xdi.events.XdiGraphModEvent;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.constants.XDIMessagingConstants;

public class PhotosColumn extends Column implements XdiGraphListener {

	private static final long serialVersionUID = -5106531864010407671L;

	protected ResourceBundle resourceBundle;

	private XdiEndpoint endpoint;
	private XRI3Segment contextNodeXri;
	private XRI3Segment address;

	/**
	 * Creates a new <code>DataPredicatesColumn</code>.
	 */
	public PhotosColumn() {
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

		if (this.endpoint != null) this.endpoint.removeXdiGraphListener(this);
	}

	private void refresh() {

		try {

			// get list of photo XRIs

			List<XRI3Segment> photoXris;

			photoXris = this.getPhotoXris();

			// add them

			this.removeAll();
			for (XRI3Segment photoXri : photoXris) {

				this.addPhotoPanel(photoXri);
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	private void addPhotoPanel(XRI3Segment photoXri) {

		PhotoPanel photoPanel = new PhotoPanel();
		photoPanel.setContextAndSubjectXriAndPhotoXri(this.endpoint, this.contextNodeXri, photoXri);

		this.add(photoPanel);
	}

	public XRI3Segment[] xdiGetAddresses() {

		return new XRI3Segment[] {
				this.address
		};
	}

	public XRI3Segment[] xdiAddAddresses() {

		return new XRI3Segment[] {
				new XRI3Segment("" + this.address + "/$$")
		};
	}

	public XRI3Segment[] xdiModAddresses() {

		return new XRI3Segment[0];
	}

	public XRI3Segment[] xdiSetAddresses() {

		return new XRI3Segment[0];
	}

	public XRI3Segment[] xdiDelAddresses() {

		return new XRI3Segment[] {
				new XRI3Segment("" + this.address)
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
		this.address = new XRI3Segment("" + this.contextNodeXri + "/+photos");

		this.refresh();

		// add us as listener

		this.endpoint.addXdiGraphListener(this);
	}

	private List<XRI3Segment> getPhotoXris() throws XdiException {

		// $get

		XRI3Segment targetXri = new XRI3Segment("" + this.contextNodeXri + "+photos");

		Message message = this.endpoint.prepareOperation(XDIMessagingConstants.XRI_S_GET, targetXri);

		MessageResult messageResult = this.endpoint.send(message);

		ContextNode contextNode = messageResult.getGraph().findContextNode(targetXri, false);
		if (contextNode == null) return new ArrayList<XRI3Segment> ();

		return new IteratorListMaker<XRI3Segment> (new MappingIterator<ContextNode, XRI3Segment> (innerGraph.getSubjects()) {

			@Override
			public XRI3Segment map(Subject item) {

				return item.getSubjectXri();
			}
		}).list();
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
	}
}
