package scratch.support.web;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scratch.support.StringUtils;

public class HttpConnection {
	
	private Map<String, String> params = new HashMap<String, String>();
	
	private Map<String, String> headers = new HashMap<String, String>();
	
	private static String USER_AGENT = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36";
	
	private String json = "";
	
	private String method = "get";
	
	public HttpConnection method(String method) {
		this.method = method;
		return this;
	}
	
	public HttpConnection param(String json) {
		this.json = json;
		return this;
	}
	
	public HttpConnection param(String key, String value) {
		params.put(key, value);
		return this;
	}
	
	public HttpConnection header(String key, String value) {
		headers.put(key, value);
		return this;
	}
	
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
			
			// set headers
			connection.setRequestProperty("User-agent", USER_AGENT);
			for(Entry<String, String> entry : headers.entrySet()) {
				connection.setRequestProperty(entry.getKey(), entry.getValue());	
			}
			
			if("post".equals(method)) {
				connection.setDoInput(true);
				connection.setUseCaches(false);
				connection.setRequestMethod("POST");
			}
			
			connection.connect();
			
			if("post".equals(method)) {
				String result = getRequestParam();
				OutputStream output = connection.getOutputStream();
				output.write(result.getBytes("UTF-8"));
				output.flush();	
			}
			
			html = StringUtils.toString(connection.getInputStream(), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return html;
	}
	
	private String getRequestParam() {
		String result = "";
		if(json.isEmpty()) {
			StringBuilder param = new StringBuilder("");
			for(Entry<String, String> entry : params.entrySet()) {
				param.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue()) + "&");
			}
			result = param.substring(0, param.length()-1).toString();	
		} else {
			result = json;
		}
		return result;
	}
	
}
