package pds.web.ui.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import nextapp.echo.app.Column;
import nextapp.echo.app.Component;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.dictionary.Dictionary;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.PDSApplication;
import pds.web.ui.MainWindow;
import pds.web.ui.MessageDialog;
import pds.web.xdi.XdiException;
import pds.web.xdi.events.XdiGraphAddEvent;
import pds.web.xdi.events.XdiGraphDelEvent;
import pds.web.xdi.events.XdiGraphEvent;
import pds.web.xdi.events.XdiGraphListener;
import pds.web.xdi.events.XdiGraphModEvent;
import pds.web.xdi.objects.XdiContext;

public class DataPredicatesColumn extends Column implements XdiGraphListener {

	private static final long serialVersionUID = -5106531864010407671L;

	private static final XRI3Segment[] DATAPREDICATEXRIS = new XRI3Segment[] {
		new XRI3Segment("+name"),
		new XRI3Segment("+gender"),
		new XRI3Segment("+date.of.birth"),
		new XRI3Segment("+email"),
		new XRI3Segment("+address"),
		new XRI3Segment("+street"),
		new XRI3Segment("+zip"),
		new XRI3Segment("+city"),
		new XRI3Segment("+country"),
		new XRI3Segment("+tel")
	};

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

	private void refresh() {

		try {

			// get list of data predicate XRIs

			List<XRI3Segment> dataPredicateXris;

			dataPredicateXris = this.getExistingDataPredicateXris(DATAPREDICATEXRIS);

			// some not initialized yet?

			List<XRI3Segment> missingDataPredicateXris = new ArrayList<XRI3Segment> ();

			for (XRI3Segment dataPredicateXri : DATAPREDICATEXRIS) {

				if (! dataPredicateXris.contains(dataPredicateXri)) missingDataPredicateXris.add(dataPredicateXri);
			}

			if (! missingDataPredicateXris.isEmpty()) {

				this.addDataPredicateXris(missingDataPredicateXris.toArray(new XRI3Segment[missingDataPredicateXris.size()]));
			}

			// add them

			this.removeAll();
			for (XRI3Segment dataPredicateXri : DATAPREDICATEXRIS) {

				this.addDataPredicatePanel(dataPredicateXri);
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your personal data: " + ex.getMessage(), ex);
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

			MessageDialog.problem("Sorry, a problem occurred while retrieving your personal data: " + ex.getMessage(), ex);
			return;
		}
	}

	public void setContextAndSubjectXri(XdiContext context, XRI3Segment subjectXri) {

		this.context = context;
		this.subjectXri = subjectXri;
		this.address = new XRI3("" + this.subjectXri);

		this.refresh();

		// add us as listener

		PDSApplication.getApp().getOpenContext().addXdiGraphListener(this);
	}

	public void setReadOnly(boolean readOnly) {

		this.readOnly = readOnly;

		for (Component component : MainWindow.findChildComponentsByClass(this, DataPredicatePanel.class)) {

			((DataPredicatePanel) component).setReadOnly(readOnly);
		}
	}

	private List<XRI3Segment> getExistingDataPredicateXris(XRI3Segment[] dataPredicateXris) throws XdiException {

		// $get

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET);
		Graph operationGraph = operation.createOperationGraph(null);

		for (XRI3Segment predicateXri : dataPredicateXris) {

			operationGraph.createStatement(this.subjectXri, Dictionary.makeExtensionPredicate(predicateXri));
		}

		MessageResult messageResult = this.context.send(operation);

		Subject subject = messageResult.getGraph().getSubject(this.subjectXri);
		if (subject == null) return new ArrayList<XRI3Segment> ();

		List<XRI3Segment> existingDataPredicateXris = new ArrayList<XRI3Segment> ();

		for (XRI3Segment predicateXri : dataPredicateXris) {

			Predicate predicate = subject.getPredicate(Dictionary.makeExtensionPredicate(predicateXri));
			if (predicate == null) continue;

			existingDataPredicateXris.add(predicateXri);
		}
		
		return existingDataPredicateXris;
	}

	private void addDataPredicateXris(XRI3Segment[] dataPredicateXris) throws XdiException {

		// $add

		Message message = this.context.prepareMessage();
		Operation operation = message.createAddOperation();
		Graph operationGraph = operation.createOperationGraph(null);

		for (XRI3Segment dataPredicateXri : dataPredicateXris) {

			operationGraph.createStatement(this.subjectXri, Dictionary.makeExtensionPredicate(dataPredicateXri));
			operationGraph.createStatement(this.subjectXri, Dictionary.makeRestrictionPredicate(dataPredicateXri));
			operationGraph.createStatement(this.subjectXri, Dictionary.makeCanonicalPredicate(dataPredicateXri));
		}

		this.context.send(message);
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
	}
}
