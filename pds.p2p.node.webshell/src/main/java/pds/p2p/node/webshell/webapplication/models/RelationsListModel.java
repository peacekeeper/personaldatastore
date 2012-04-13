package pds.p2p.node.webshell.webapplication.models;

import java.net.URLDecoder;
import java.util.ArrayList;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pds.p2p.api.node.client.DanubeApiClient;
import pds.p2p.node.webshell.objects.Relation;

public class RelationsListModel extends AbstractReadOnlyModel<ArrayList<Relation>> {

	private static final long serialVersionUID = 6088509994392035182L;

	private static Logger log = LoggerFactory.getLogger(RelationsListModel.class.getName());

	private ArrayList<Relation> relations;

	@Override
	public void detach() {

		this.relations = null;
	}

	public ArrayList<Relation> getObject() {

		if (this.relations != null) return this.relations;

		log.debug("Getting relations.");

		// get XDI data

		String[] rawrelations;

		try {

			String inumber = DanubeApiClient.orionObject.inumber();
			rawrelations = DanubeApiClient.polarisObject.getRelations(inumber + "/+friend/($)", null);
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		// prepare relations

		this.relations = new ArrayList<Relation> ();

		for (String rawrelation : rawrelations) {

			try {

				String iname = rawrelation;
				String inumber = DanubeApiClient.orionObject.resolve(iname);
				String nodeId = inumber == null ? null : DanubeApiClient.siriusObject.getLiteral(inumber + "$nodeid");
				String xdiUri = inumber == null ? null : DanubeApiClient.siriusObject.getLiteral(inumber + "$xdiuri");

				Relation relation = new Relation();
				relation.setIname(rawrelation);
				relation.setInumber(inumber == null ? null : inumber);
				relation.setNodeId(nodeId == null ? null : URLDecoder.decode(nodeId, "UTF-8"));
				relation.setXdiUri(xdiUri == null ? null : URLDecoder.decode(xdiUri, "UTF-8"));

				this.relations.add(relation);
			} catch (Exception ex) {

				throw new RuntimeException(ex.getMessage(), ex);
			}
		}

		// done

		log.debug("Loaded " + this.relations.size() + " relations.");

		return this.relations;
	}
}
