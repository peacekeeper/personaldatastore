package pds.web.signup.xri.grs.components.mpay;

import nextapp.echo.app.list.DefaultListModel;

import com.fullxri.mpay4java.MpayTools;

class BrandsModel extends DefaultListModel {

	private static final long serialVersionUID = 3467358701159910792L;

	BrandsModel() {

		super(new String[] {
				MpayTools.BRAND_AMEX,
				MpayTools.BRAND_DINERS,
				MpayTools.BRAND_JCB,
				MpayTools.BRAND_MASTERCARD,
				MpayTools.BRAND_VISA
		});
	}
}