package pds.web.ui.app.photos.components;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ResourceBundle;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.AwtImageReference;
import nextapp.echo.app.Button;
import nextapp.echo.app.Column;
import nextapp.echo.app.Extent;
import nextapp.echo.app.ImageReference;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.Panel;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.RowLayoutData;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.higgins.XDI2.addressing.Addressing;
import org.eclipse.higgins.XDI2.constants.MessagingConstants;
import org.eclipse.higgins.XDI2.messaging.MessageResult;
import org.eclipse.higgins.XDI2.messaging.Operation;
import org.eclipse.higgins.XDI2.xri3.impl.XRI3;
import org.eclipse.higgins.XDI2.xri3.impl.XRI3Segment;

import pds.web.components.xdi.XdiPanel;
import pds.web.ui.MessageDialog;
import pds.xdi.XdiContext;
import pds.xdi.XdiException;
import pds.xdi.XdiNotExistentException;
import pds.xdi.events.XdiGraphAddEvent;
import pds.xdi.events.XdiGraphDelEvent;
import pds.xdi.events.XdiGraphEvent;
import pds.xdi.events.XdiGraphListener;
import pds.xdi.events.XdiGraphModEvent;
import echopoint.ImageIcon;

public class PhotoPanel extends Panel implements XdiGraphListener {

	private static final long serialVersionUID = -5082464847478633075L;

	protected ResourceBundle resourceBundle;

	private XdiContext context;
	private XRI3Segment subjectXri;
	private XRI3Segment photoXri;
	private XRI3 address;
	private XRI3 binAddress;
	private XRI3 titleAddress;

	private XdiPanel xdiPanel;
	private Panel imageContainerPanel;
	private Button deleteButton;
	private Label titleLabel;

	/**
	 * Creates a new <code>AccountPersonaPanel</code>.
	 */
	public PhotoPanel() {
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

			byte[] bytes = this.getBytes();
			String title = this.getTitle();

			this.xdiPanel.setContextAndMainAddressAndGetAddresses(this.context, this.address, this.xdiGetAddresses());

			this.titleLabel.setText(title);

			Image image = Toolkit.getDefaultToolkit().createImage(bytes);
			ImageReference imageReference = new AwtImageReference(image);

			this.imageContainerPanel.removeAll();
			ImageIcon imageImageIcon = new ImageIcon();
			imageImageIcon.setIcon(imageReference);
			this.imageContainerPanel.add(imageImageIcon);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	public XRI3[] xdiGetAddresses() {

		return new XRI3[] {
				this.address
		};
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

				this.getParent().remove(this);
				return;
			}
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while retrieving your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	private void onDeleteActionPerformed(ActionEvent e) {

		try {

			// $del

			Operation operation = this.context.prepareOperation(MessagingConstants.XRI_DEL);
			operation.createOperationGraph(Addressing.convertAddressToGraph(this.address));

			this.context.send(operation);
		} catch (Exception ex) {

			MessageDialog.problem("Sorry, a problem occurred while storing your Personal Data: " + ex.getMessage(), ex);
			return;
		}
	}

	public void setContextAndSubjectXriAndPhotoXri(XdiContext context, XRI3Segment subjectXri, XRI3Segment photoXri) {

		// remove us as listener
		
		if (this.context != null) this.context.removeXdiGraphListener(this);

		// refresh
		
		this.context = context;
		this.subjectXri = subjectXri;
		this.photoXri = photoXri;
		this.address = new XRI3("" + this.subjectXri + "/+photos//" + this.photoXri);
		this.binAddress = new XRI3("" + this.subjectXri + "/+photos//" + this.photoXri + "/$a$bin");
		this.titleAddress = new XRI3("" + this.subjectXri + "/+photos//" + this.photoXri + "/$a$xsd$string");

		this.refresh();

		// add us as listener

		this.context.addXdiGraphListener(this);
	}

	private byte[] getBytes() throws XdiException {

		// $get

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, this.binAddress);
		MessageResult messageResult = this.context.send(operation);

		String data = Addressing.findLiteralData(messageResult.getGraph(), this.binAddress);
		if (data == null) throw new XdiNotExistentException();

		return Base64.decodeBase64(data.getBytes());
	}

	private String getTitle() throws XdiException {

		// $get

		Operation operation = this.context.prepareOperation(MessagingConstants.XRI_GET, this.titleAddress);
		MessageResult messageResult = this.context.send(operation);

		String data = Addressing.findLiteralData(messageResult.getGraph(), this.titleAddress);
		if (data == null) throw new XdiNotExistentException();

		return data;
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents() {
		Column column1 = new Column();
		column1.setCellSpacing(new Extent(10, Extent.PX));
		add(column1);
		Row row1 = new Row();
		row1.setCellSpacing(new Extent(10, Extent.PX));
		column1.add(row1);
		xdiPanel = new XdiPanel();
		row1.add(xdiPanel);
		imageContainerPanel = new Panel();
		imageContainerPanel.setHeight(new Extent(80, Extent.PX));
		imageContainerPanel.setWidth(new Extent(200, Extent.PX));
		imageContainerPanel.setInsets(new Insets(new Extent(10, Extent.PX)));
		row1.add(imageContainerPanel);
		ImageIcon imageIcon1 = new ImageIcon();
		imageContainerPanel.add(imageIcon1);
		Column column2 = new Column();
		row1.add(column2);
		titleLabel = new Label();
		titleLabel.setStyleName("Default");
		titleLabel.setText("...");
		column2.add(titleLabel);
		deleteButton = new Button();
		deleteButton.setStyleName("Plain");
		ResourceImageReference imageReference1 = new ResourceImageReference(
				"/pds/web/resource/image/op-cancel.png");
		deleteButton.setIcon(imageReference1);
		RowLayoutData deleteButtonLayoutData = new RowLayoutData();
		deleteButtonLayoutData.setAlignment(new Alignment(Alignment.DEFAULT,
				Alignment.CENTER));
		deleteButton.setLayoutData(deleteButtonLayoutData);
		deleteButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onDeleteActionPerformed(e);
			}
		});
		column2.add(deleteButton);
	}
}
