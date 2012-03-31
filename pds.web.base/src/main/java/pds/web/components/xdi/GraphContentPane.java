package pds.web.components.xdi;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ResourceBundle;

import nextapp.echo.app.Button;
import nextapp.echo.app.Column;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Row;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import org.eclipse.higgins.XDI2.Graph;
import org.eclipse.higgins.XDI2.Literal;
import org.eclipse.higgins.XDI2.Statement;
import org.eclipse.higgins.XDI2.impl.memory.MemoryGraphFactory;
import org.eclipse.higgins.XDI2.io.XDIWriterRegistry;
import org.eclipse.higgins.XDI2.util.CopyUtil;
import org.eclipse.higgins.XDI2.util.CopyUtil.CopyStatementStrategy;
import org.eclipse.higgins.XDI2.xri3.impl.XRI3Segment;

import pds.web.PDSApplication;
import pds.web.components.HtmlLabel;
import pds.web.ui.MessageDialog;
import pds.web.util.HtmlUtil;

public class GraphContentPane extends ContentPane {

	private static final long serialVersionUID = 1L;

	protected ResourceBundle resourceBundle;

	private Graph graph;
	private String format;
	private String originalHtml;

	private HtmlLabel htmlLabel;

	/**
	 * Creates a new <code>XdiContentPane</code>.
	 */
	public GraphContentPane() {
		super();

		// Add design-time configured components.
		initComponents();

		this.format = "X3 Simple";
		this.originalHtml = this.htmlLabel.getHtml();
	}

	/**
	 * Returns the user's application instance, cast to its specific type.
	 *
	 * @return The user's application instance.
	 */
	protected PDSApplication getApplication() {
		return (PDSApplication) getApplicationInstance();
	}

	private void refresh() {

		StringWriter writer = new StringWriter();

		try {

			XDIWriterRegistry.forFormat(this.format).write(this.graph, writer, null);

			String html = this.originalHtml;
			html = html.replace("<!-- $$$ -->", HtmlUtil.htmlEncode(writer.getBuffer().toString(), true, false));
			this.htmlLabel.setHtml(html);
		} catch (IOException ex) {

			MessageDialog.problem("Sorry, a problem occurred while serializing the XDI graph:" + ex.getMessage(), ex);

			this.htmlLabel.setHtml("");
		}
	}

	public void setGraph(Graph graph) {

		this.graph = MemoryGraphFactory.getInstance().openGraph();
		CopyUtil.copyStatements(graph, this.graph, PASSWORDCENSORINGCOPYSTATEMENTSTRATEGY);

		this.refresh();
	}

	public Graph getGraph() {

		return this.graph;
	}

	private void onX3SimpleActionPerformed(ActionEvent e) {

		this.format = "X3 Simple";
		this.refresh();
	}

	private void onX3StandardActionPerformed(ActionEvent e) {

		this.format = "X3 Standard";
		this.refresh();
	}

	private void onX3JActionPerformed(ActionEvent e) {

		this.format = "X3J";
		this.refresh();
	}

	private void onXDIXMLActionPerformed(ActionEvent e) {

		this.format = "XDI/XML";
		this.refresh();
	}

	private static final XRI3Segment XRI_PASSWORD = new XRI3Segment("$password");

	private static CopyStatementStrategy PASSWORDCENSORINGCOPYSTATEMENTSTRATEGY = new CopyStatementStrategy() {

		@Override
		public boolean doCopy(Statement statement, Graph target) {

			return true;
		}

		@Override
		public String replaceLiteralData(Literal literal) {

			if (literal.getPredicate().getPredicateXri().equals(XRI_PASSWORD)) {

				return "********";
			} else {

				return literal.getData();
			}
		};
	};

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		this.setInsets(new Insets(new Extent(10, Extent.PX)));
		Column column1 = new Column();
		column1.setCellSpacing(new Extent(10, Extent.PX));
		add(column1);
		Row row1 = new Row();
		row1.setCellSpacing(new Extent(10, Extent.PX));
		column1.add(row1);
		Button button2 = new Button();
		button2.setStyleName("Default");
		button2.setText("X3 Simple");
		button2.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onX3SimpleActionPerformed(e);
			}
		});
		row1.add(button2);
		Button button1 = new Button();
		button1.setStyleName("Default");
		button1.setText("X3 Standard");
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onX3StandardActionPerformed(e);
			}
		});
		row1.add(button1);
		Button button3 = new Button();
		button3.setStyleName("Default");
		button3.setText("X3J");
		button3.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onX3JActionPerformed(e);
			}
		});
		row1.add(button3);
		Button button4 = new Button();
		button4.setStyleName("Default");
		button4.setText("XDI/XML");
		button4.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onXDIXMLActionPerformed(e);
			}
		});
		row1.add(button4);
		htmlLabel = new HtmlLabel();
		htmlLabel
		.setHtml("<div style=\"white-space:nowrap;font-family:monospace;\"><pre><!-- $$$ --></pre></div>");
		column1.add(htmlLabel);
	}
}
