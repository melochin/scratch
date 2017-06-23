package scratch.support.web;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import scratch.support.StringUtils;

public class HttpConnection {
	
	public String connect(String url) {

		String html = "";
		
		try {
			html = connect(new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return html;
	}
	
	public String connect(URL url) {
		String html = "";
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0");
			connection.connect();
			html = StringUtils.toString(connection.getInputStream(), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return html;
	}
	
}
