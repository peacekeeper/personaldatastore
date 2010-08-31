package pds.web.signup.xri.openxri.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nextapp.echo.app.list.AbstractListModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pds.store.xri.Xri;
import pds.store.xri.XriStore;
import pds.store.xri.XriStoreException;

public class RootNamespacesListModel extends AbstractListModel {

	private static final long serialVersionUID = 4426338918654302704L;

	private static final String ATTRIBUTE_KEY_INVISIBLE = "invisible";

	private static final Log log = LogFactory.getLog(RootNamespacesListModel.class);

	private XriStore xriStore;
	private transient List<String> list;

	public RootNamespacesListModel(XriStore xriStore) {

		this.xriStore = xriStore;
	}

	@Override
	public Object get(int i) {

		if (this.list == null) this.load();

		return this.list.get(i);
	}

	@Override
	public int size() {

		if (this.list == null) this.load();

		return this.list.size();
	}

	private void load() {

		List<Xri> xris;
		this.list = new ArrayList<String> ();

		// list root xris

		log.debug("Listing root xris.");

		try {

			xris = this.xriStore.listRootXris();
		} catch (XriStoreException ex) {

			log.error("Cannot list root xris: " + ex.getMessage(), ex);
			throw new RuntimeException(ex);
		}

		// remove xris with an invisible attribute

		try {

			for (Iterator<Xri> i = xris.iterator(); i.hasNext(); ) {

				Xri xri = i.next();
				if (! xri.hasXriAttribute(ATTRIBUTE_KEY_INVISIBLE)) {

					this.list.add(xri.getFullName());
				}
			}
		} catch (XriStoreException ex) {

			log.error("Cannot read xri attribute: " + ex.getMessage(), ex);
			throw new RuntimeException(ex);
		}

		// done

		log.debug("Loaded " + this.list.size() + " root XRIs.");
	}
}
