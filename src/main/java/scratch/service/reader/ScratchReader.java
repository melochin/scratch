package scratch.service.reader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scratch.support.regex.Connection;

/**
 * 将HTML文本结构化的类
 * @author melochin
 * @since 2017.05.03
 * @param <T>
 */
 public abstract class ScratchReader<T> {
	
	 private Map<String,String> config = new HashMap<String, String>();
	 
	 private Connection connection;
	 
	 protected String readerUrl;
	 
	 public ScratchReader() {
		 connection = new Connection();
	 }
	 
	 public ScratchReader(String url) {
		 this();
		 readerUrl = url;
	 }
	 
	 public void ConnectConfig(Map<String, String> configs) {
		configs.put("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0");
		 return;
	 }
	 
	/**
	 * 将HTML文本转换成实体列表（由子类覆写）
	 * @param html			读取的HTML文本
	 * @param match			解析HTML文本的工具
	 * @param returnList	转换的实体（作为参数，写子类的时候就不会实例化该对象了，父类已经实例化）
	 */
	protected abstract void read(String html, List<T> returnList);
	
	/**
	 * 读取HTML，返回实体列表
	 * @param url
	 * @return
	 */
	public List<T> read(String url) {
		List<T> t = new ArrayList<T>();
		try {
			ConnectConfig(config);
			String html = connection.toHtml(new URL(url), config);
			read(html, t);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return t;
	}
	
	public List<T> read() {
		return read(readerUrl);
	}
	
}
