package pds.web.signup.xri.grs.models;

import nextapp.echo.app.list.DefaultListModel;

public class YearsModel extends DefaultListModel {

	private static final long serialVersionUID = 1697235881917011247L;

	public static final Integer DEFAULT_YEARS = Integer.valueOf(1);

	private static final Integer[] YEARS = {

		Integer.valueOf(1),
		Integer.valueOf(2),
		Integer.valueOf(3),
		Integer.valueOf(4),
		Integer.valueOf(5),
		Integer.valueOf(6),
		Integer.valueOf(7),
		Integer.valueOf(8),
		Integer.valueOf(9)
	};

	public YearsModel() {

		super(YEARS);
	}
}
