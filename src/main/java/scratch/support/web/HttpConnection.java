package scratch.support.web;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scratch.support.StringUtils;

public class HttpConnection {
	
	private Map<String, String> params = new HashMap<String, String>();
	private Map<String, String> headers = new HashMap<String, String>();
	private String param = "";
	
	private String method = "get";
	
	public String connect(String url) {

		String html = "";
		
		try {
			html = connect(new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return html;
	}
	
	public HttpConnection method(String method) {
		this.method = method;
		return this;
	}
	
	public HttpConnection param(String param) {
		this.param = param;
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
	
	
	public String connect(URL url) {
		String html = "";
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0");
			
			if("post".equals(method)) {
				connection.setDoInput(true);
				connection.setUseCaches(false);
				connection.setRequestMethod("POST");
			}
			
			for(Entry<String, String> entry : headers.entrySet()) {
				connection.setRequestProperty(entry.getKey(), entry.getValue());	
			}
			
			connection.connect();
			
			if("post".equals(method)) {
				String result = "";
				if(param.isEmpty()) {
					StringBuilder param = new StringBuilder("");
					for(Entry<String, String> entry : params.entrySet()) {
						param.append(entry.getKey() + "=" + entry.getValue() + "&");
					}
					result = param.substring(0, param.length()-1).toString();	
				} else {
					result = param;
				}
				
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
	
}
