package pds.web.signup.xri.grs.models;

import java.text.DecimalFormat;

import nextapp.echo.app.list.DefaultListModel;

public class CurrenciesModel extends DefaultListModel {

	private static final long serialVersionUID = 1697235881917011247L;

	public static final String DEFAULT_CURRENCY = "USD";

	private static final String[] CURRENCIES = {

		"USD",
		"EUR"
	};

	private static final double[] EXCHANGE = {

		1,		// USD
		0.73	// EUR
	};

	public CurrenciesModel() {
		
		super(CURRENCIES);
	}

	public static double priceForUsdprice(String currency, double usdprice) {

		if ("USD".equals(currency)) return(usdprice * EXCHANGE[0]);
		if ("EUR".equals(currency)) return(usdprice * EXCHANGE[1]);

		return(-1);
	}

	public static String formatPrice(double price) {

		DecimalFormat format = new DecimalFormat("0.00");
		return(format.format(price));
	}
}
