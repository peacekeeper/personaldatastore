package pds.store.xri.util;

import java.util.List;

import org.openxri.xml.SEPType;
import org.openxri.xml.Service;

public class ServiceUtil {

	public static final String[] SERVICE_TYPES = {
		"http://openid.net/signon/1.0",
		"http://specs.openid.net/auth/2.0/signon",
		"xri://+i-service*(+contact)*($v*1.0)",
		"xri://+i-service*(+forwarding)*($v*1.0)",
		"xri://+i-service*(+locator)*($v*1.0)",
		"xri://$res*auth*($v*2.0)",
		"xri://+i-service*(+account)*($v*1.0)",
		"xri://$xdi!($v!1)",
		"xri://$certificate*($x.509)",
		"http://microformats.org/profile/hcard",
		"http://portablecontacts.net/spec/1.0#me",
		"http://schemas.google.com/g/2010#updates-from",
		"salmon",
		"http://salmon-protocol.org/ns/salmon-replies",
		"http://salmon-protocol.org/ns/salmon-mention",
		"describedby",
		"magic-public-key"
	};

	@SuppressWarnings("unchecked")
	public static boolean isStandard(Service service) {

		List<SEPType> serviceTypes = (List<SEPType>) service.getTypes();

		for (int i=0; i<serviceTypes.size(); i++) {

			SEPType serviceType = serviceTypes.get(i);

			for (int ii=0; ii<SERVICE_TYPES.length; ii++)
				if (SERVICE_TYPES[ii].equals(serviceType.getValue())) return(true);
		}

		return(false);
	}
}
