package pds.web.ui.app.feed.components;


import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import nextapp.echo.app.Column;
import nextapp.echo.app.event.ActionEvent;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.constants.MessagingConstants;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.util.iterators.IteratorListMaker;
import org.eclipse.higgins.xdi4j.util.iterators.MappingIterator;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.web.ui.MessageDialog;
import pds.web.ui.app.feed.components.TopicPanel.TopicPanelDelegate;
import pds.xdi.XdiContext;
import pds.xdi.XdiException;
import pds.xdi.events.XdiGraphAddEvent;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import pds.xdi.events.XdiGraphModEvent;

public class TopicsColumn extends Column implements XdiGraphListener {

	private static final long serialVersionUID = -5106531864010407671L;

	private static final XRI3Segment XRI_TOPICS = new XRI3Segment("+ostatus+topics");

	protected ResourceBundle resourceBundle;

	private XdiContext context;
	private XRI3Segment subjectXri;
	private XRI3 address;

	/**
	 * Creates a new <code>DataPredicatesColumn</code>.
	 */
	public TopicsColumn() {
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

			// get list of topic XRIs

			List<XRI3Segment> topicXris;

			topicXris = this.getTopicXris();

			// add them

			this.removeAll();
			for (XRI3Segment topicXri : topicXris) {

				this.addTopicPanel(topicXri);
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	private void addTopicPanel(XRI3Segment topicXri) {

		TopicPanel topicPanel = new TopicPanel();
		topicPanel.setContextAndSubjectXriAndTopicXri(this.context, this.subjectXri, topicXri);
		topicPanel.setTopicPanelDelegate(new TopicPanelDelegate() {

			@Override
			public void onResubscribeActionPerformed(ActionEvent e) {

			}

			@Override
			public void onUnsubscribeActionPerformed(ActionEvent e) {

			}
		});

		this.add(topicPanel);
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

	public void setContextAndSubjectXri(XdiContext context, XRI3Segment subjectXri) {

		// remove us as listener

		if (this.context != null) this.context.removeXdiGraphListener(this);

		// refresh

		this.context = context;
		this.subjectXri = subjectXri;
		this.address = new XRI3("" + this.subjectXri + "/" + XRI_TOPICS);

		this.refresh();

		// add us as listener

		this.context.addXdiGraphListener(this);
	}

	private List<XRI3Segment> getTopicXris() throws XdiException {

		// $get

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET);
		Graph operationGraph = operation.createOperationGraph(null);
		operationGraph.createStatement(this.subjectXri, XRI_TOPICS);

		MessageResult messageResult = this.context.send(operation);

		Subject subject = messageResult.getGraph().getSubject(this.subjectXri);
		if (subject == null) return new ArrayList<XRI3Segment> ();

		Predicate predicate = subject.getPredicate(XRI_TOPICS);
		if (predicate == null) return new ArrayList<XRI3Segment> ();

		Graph innerGraph = predicate.getInnerGraph();
		if (innerGraph == null) return new ArrayList<XRI3Segment> ();

		return new IteratorListMaker<XRI3Segment> (new MappingIterator<Subject, XRI3Segment> (innerGraph.getSubjects()) {

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
