package scratch.support.web.net;

import scratch.support.StringUtils;
import scratch.support.web.RequestParam;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;

public abstract class Connector {

	/**
	 * 请求连接
	 */
	private HttpURLConnection connection;

	private URL url;

	/**
	 * 请求头
	 */
	protected Map<String, String> headers;

	/**
	 * 请求参数
	 */
	protected RequestParam requestParam;

	/**
	 * 不能让外包直接实例化
	 */
	protected Connector(URL url) {
		headers = new HashMap<>();
		requestParam = new RequestParam();
		this.url = url;
	}

	/**
	 * 设置请求头 key/value 一对
	 * @param key
	 * @param value
	 * @return
	 */
	public Connector header(String key, String value) {
		headers.put(key, value);
		return this;
	}

	/**
	 * 设置请求头
	 * @param map
	 * @return
	 */
	public Connector header(Map<String, String> map) {
		headers.putAll(map);
		return this;
	}

	/**
	 * 设置request传输的内容（一般指json）
	 * @param content
	 * @return
	 */
	public Connector content(String content) {
		this.requestParam.content(content);
		return this;
	}

	/**
	 * 设置request传输的参数（一般指表单的数据格式）
	 * @param key
	 * @param value
	 * @return
	 */
	public Connector param(String key, String value) {
		this.requestParam.param(key, value);
		return this;
	}

	/**
	 * 设置request传输的参数（一般指表单的数据格式）
	 * @param map
	 * @return
	 */
	public Connector param(Map<String, String> map) {
		this.requestParam.param(map);
		return this;
	}


	public<R> R result(Function<String, R> process) {
		try {
			HttpURLConnection tempConnection = open(url);
			this.connection = doConnect(tempConnection);
			InputStream inputStream = doInputStream(this.connection);
			String html = StringUtils.toString(inputStream, "UTF-8");
			return process.apply(html);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.connection.disconnect();
		}
		return null;
	}

	public String html() {
		return result(html -> html);
	}

	private InputStream doInputStream(HttpURLConnection connection) throws IOException {
		if("gzip".equals(connection.getContentEncoding())) {
			return new GZIPInputStream(connection.getInputStream());
		}
		return connection.getInputStream();
	}

	protected abstract HttpURLConnection open(URL url) throws IOException;

	protected abstract HttpURLConnection doConnect(HttpURLConnection connection) throws Exception;


	//TODO 输出response
	/**
	 * 打印——发送的请求数据
	 * @return
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(connection.getURL() != null) {

			sb.append("method : ");
			sb.append(connection.getRequestMethod());
			sb.append("\n");


			sb.append("url : ");
			sb.append(connection.getURL().toString());
			sb.append("\n");

		}

		sb.append("Request\n");
		if(headers != null) {
			sb.append("header:");
			sb.append(headers);
			sb.append("\n");
		}

		if(requestParam != null) {
			sb.append("param:");
			sb.append(requestParam);
			sb.append("\n");
		}

		sb.append("Response\n");

		try {
			int code = connection.getResponseCode();
			sb.append("code:" + code + "\n");
		} catch (IOException e) {
		}

		sb.append("contentType:" + connection.getContentType());

		return sb.toString();
	}

	private String format(String str) {
		return String.format("%12s", str);
	}

}
