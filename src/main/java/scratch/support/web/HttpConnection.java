package scratch.support.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;

import org.springframework.web.util.UriComponentsBuilder;
import scratch.support.StringUtils;

public class HttpConnection {

	private Map<String, String> headers = new HashMap<String, String>();

	private RequestParam requestParam = new RequestParam();

	private static Properties properties = new Properties();

	private static String PROPERTIES_NAME = "httpconnetion.properties";

	private static String USER_AGENT = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36";

	private static String METHOD_GET = "GET";

	private static String METHOD_POST = "POST";

	private String method = METHOD_GET;

	private HttpURLConnection connection;

	static {
		initProperties();
	}

	private static void initProperties() {
		InputStream in = ClassLoader.getSystemClassLoader()
				.getResourceAsStream(PROPERTIES_NAME);
		if (in == null) {
			System.out.println("cant find " + PROPERTIES_NAME);
			return;
		}
		try {
			properties.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public HttpConnection method(String method) {
		this.method = method;
		return this;
	}

	public HttpConnection content(String content) {
		this.requestParam.content(content);
		return this;
	}

	public HttpConnection param(String key, String value) {
		this.requestParam.param(key, value);
		return this;
	}

	public HttpConnection param(Map<String, String> map) {
		this.requestParam.param(map);
		return this;
	}

	public HttpConnection header(String key, String value) {
		headers.put(key, value);
		return this;
	}

	public String connect(String pathVariableUrl, Object... pathVariable) {
		String url = UriComponentsBuilder.fromUriString(pathVariableUrl)
				.buildAndExpand(pathVariable)
				.encode()
				.toUriString();
		return connect(url);
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
		try {
			if (METHOD_GET.equalsIgnoreCase(method)) {
				this.connection = get(url);
			}
			if (METHOD_POST.equalsIgnoreCase(method)) {
				this.connection = post(url);
			}
			html = StringUtils.toString(this.connection.getInputStream(), "UTF-8");
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		return html;
	}

	private void preConnect(HttpURLConnection httpURLConnection) {
		httpURLConnection.setDoOutput(true);
		httpURLConnection.setInstanceFollowRedirects(false);
		setRequestHeader(httpURLConnection);
	}

	private HttpURLConnection post(URL url) throws IOException {
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

		preConnect(httpURLConnection);

		httpURLConnection.setDoInput(true);
		httpURLConnection.setUseCaches(false);
		httpURLConnection.setRequestMethod(METHOD_POST);
		httpURLConnection.connect();

		// post request param
		String result = requestParam.toString();
		OutputStream output = httpURLConnection.getOutputStream();
		output.write(result.getBytes("UTF-8"));
		output.flush();
		output.close();
		return httpURLConnection;
	}

	private HttpURLConnection get(URL url) throws IOException {
		if (requestParam.toString() != null && !requestParam.toString().isEmpty()) {
			url = new URL(url.toString() + "?" + requestParam.toString());
		}
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

		preConnect(httpURLConnection);
		httpURLConnection.setRequestMethod(METHOD_GET);

		httpURLConnection.connect();
		return httpURLConnection;
	}

	public Map<String, String> getCookie() {
		Map<String, String> cookies = new HashMap<String, String>();
		if (connection == null) {
			return cookies;
		}
		Map<String, List<String>> headers = connection.getHeaderFields();
		List<String> lists = headers.get("Set-Cookie");
		if (lists != null && lists.size() > 0) {
			for (int i = 0; i < lists.size(); i++) {
				String cookie = lists.get(i);
				// 找到第一个分号
				int endIndex = cookie.indexOf(";");
				if (endIndex == -1) {
					endIndex = cookie.length();
				}
				int midIndex = cookie.indexOf("=");
				if (midIndex == -1) {
					continue;
				}
				String key = cookie.substring(0, midIndex);
				String value = cookie.substring(midIndex + 1, endIndex);
				cookies.put(key, value);
			}
		}
		return cookies;
	}

	private void setRequestHeader(HttpURLConnection connection) {
		connection.setRequestProperty("User-agent", USER_AGENT);
		// Cookie默认采取配置文件，如果header配置了Cookie，则覆盖默认值
		connection.setRequestProperty("Cookie", getDefaultCookie(connection.getURL().getHost()));
		for (Entry<String, String> entry : headers.entrySet()) {
			connection.setRequestProperty(entry.getKey(), entry.getValue());
		}
	}

	private String getDefaultCookie(String hostName) {
		return properties.getProperty(hostName);
	}

}
