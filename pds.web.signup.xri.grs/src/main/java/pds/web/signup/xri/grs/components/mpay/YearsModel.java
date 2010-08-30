package pds.web.signup.xri.grs.components.mpay;

import nextapp.echo.app.list.DefaultListModel;

class YearsModel extends DefaultListModel {

	private static final long serialVersionUID = -6194637175306608696L;

	YearsModel() {

		super(new String[] {
				"2010",
				"2011",
				"2012",
				"2013",
				"2014",
				"2015",
				"2016",
				"2017",
				"2018",
				"2019",
				"2020",
				"2021",
				"2022",
				"2023",
				"2024",
				"2025",
				"2026",
				"2027",
				"2028",
				"2029",
				"2030",
				"2031",
				"2032",
				"2033",
				"2034",
				"2035",
				"2036",
				"2037",
				"2038",
				"2039"
		});
	}
	
	static String yearToMpayexpiryyear(String year) {
		
		return year.substring(2);
	}
}