package scratch.support.web.net;

import scratch.support.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class Flow {

	private BlockingQueue<Connector> blockingQueue;

	private static String PROPERTIES_NAME = "flow.properties";

	private static Properties properties = new Properties();

	private static String USER_AGENT = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) " +
			"AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36";

	static {
		initProperties();
	}

	/**
	 * 初始化配置信息
	 * 读取文件名为{PROPERTIES_NAME}的配置信息
	 */
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

	public Flow() {
		blockingQueue = new LinkedBlockingQueue<>();
	}

	public Connector post(String pathVariableUrl, Object... pathVariable) throws MalformedURLException {
		String url = StringUtils.buildEncodeUrl(pathVariableUrl, pathVariable);
		return post(url);
	}

	public Connector post(String url) throws MalformedURLException {
		return post(new URL(url));
	}

	public Connector post(URL url) {
		Connector post = new Post(url);
		post.header(getDefaultHeader(url));
		blockingQueue.add(post);
		return post;
	}

	public Connector get(String pathVariableUrl, Object... pathVariable) throws MalformedURLException {
		String url = StringUtils.buildEncodeUrl(pathVariableUrl, pathVariable);
		return get(url);
	}

	public Connector get(String url) throws MalformedURLException {
		return get(new URL(url));
	}

	public Connector get(URL url) {
		Connector get = new Get(url);
		get.header(getDefaultHeader(url));
		blockingQueue.add(get);
		return get;
	}

	private Map<String, String> getDefaultHeader(URL url) {
		Map<String, String> map = new HashMap<>();
		map.put("User-agent", USER_AGENT);

		String cookie = properties.getProperty(url.getHost());
		if(cookie != null) {
			map.put("Cookie", cookie);
		}
		return map;
	}

	@Override
	public String toString() {

		return blockingQueue.stream()
				.map(q -> q.toString())
				.collect(Collectors.joining("\n\n"));
	}
}
