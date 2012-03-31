package pds.p2p.api.vega.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class XriUtil {

	private static final String PROXY = "http://xri.net/";

	private XriUtil() { }

	public static String discoverConnectUri(String xri) throws IOException {

		URL url = new URL(
				PROXY +
				xri +
				"?_xrd_r=text/uri-list;sep=true;nodefault_t=1" + 
		"&_xrd_t=xri://@vega*connect");

		HttpURLConnection http = (HttpURLConnection) url.openConnection();
		http.setRequestMethod("GET");
		if (http.getResponseCode() != 200) return(null);
		InputStream stream = http.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		String line;
		line = reader.readLine();

		if (line == null) return(null);
		if (! line.startsWith("vega://")) return(null);

		return(line);
	}
}
