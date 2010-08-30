package pds.web.signup.xri.openxri.models;

import java.util.ArrayList;
import java.util.List;

import nextapp.echo.app.list.AbstractListModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pds.store.xri.Xri;
import pds.store.xri.XriStore;

public class RootNamespacesListModel extends AbstractListModel {

	private static final long serialVersionUID = 4426338918654302704L;

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

		try {

			this.list = new ArrayList<String> ();

			for (Xri xri : this.xriStore.listRootXris()) {

				if (xri.getUserIdentifier() != null) continue;
				this.list.add(xri.getFullName());
			}
		} catch (Exception ex) {

			log.error("Cannot list root XRIs: " + ex.getMessage(), ex);
		}

		log.debug("Loaded " + this.list.size() + " root XRIs.");
	}
}
