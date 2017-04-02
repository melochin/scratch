package com.rory.regex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * 获取HTML文本
 * @author Rory
 *
 */
public class Connection {

	public static final String POST = "post";
	
	public static final String GET = "get";

	public String toHtml(String url){
		String html = null;
		try {
			html = toHtml(new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "";
		}
		
		return html;
	}
	
	public String toHtml(URL url){
		return toHtml(url, null);
	}
	
	public String toHtml(URL url, String method, Map<String, String> params, Map<String, String> cookie) {
		URLConnection uc = null;
		InputStreamReader input = null;
		String temp = null;
		StringBuffer html = null;
		try {
			if(method != null && method.equals(POST)) {
				uc = connectPost(url, params, cookie);
			} else {
				uc = connect(url, cookie);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Delay Internet connection, get html text failed");
			return "";
		}
		
		try {
			input = new InputStreamReader(uc.getInputStream(),"UTF-8");
			BufferedReader reader = new BufferedReader(input);
			html = new StringBuffer();
			
			while((temp = reader.readLine()) != null ){
				html.append(temp);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return html.toString();
	}
	
	public String toHtml(URL url, Map<String,String> cookie){
		return toHtml(url, GET, null, cookie);
	}
	
	public URLConnection connect(String url) throws IOException{
		return connect(url,null);
	}
	
	public URLConnection connect(String url, Map<String,String> cookie) throws IOException{
		URL u = new URL(url);
		return connect(u, cookie);
	}
	
	public URLConnection connect(URL url) throws IOException{
		return connect(url, null);
	}
	
	public URLConnection connect(URL url, Map<String,String> cookie) throws IOException{
		URLConnection uc = url.openConnection();
		if(cookie == null){
			return uc;
		}
		
		for(String key:cookie.keySet()){
			uc.setRequestProperty(key, cookie.get(key));
		}
		return uc;	
	}
	
	public URLConnection connectPost(URL url, Map<String,String> params, Map<String,String> cookie) throws IOException {
		URLConnection uc = connect(url, cookie);
		uc.setDoOutput(true);  
		uc.setDoInput(true); 
		PrintWriter  out = new PrintWriter(uc.getOutputStream());  
		String param = "";
		for(Entry<String, String> entry : params.entrySet()) {
			param += (param.isEmpty() ? "" : "&") + entry.getKey() + "=" + entry.getValue();
		}
		param = URLEncoder.encode(param, "UTF-8");
		System.out.println(param);
        out.print(param);  
        out.flush();  
		return uc;
	}

	
}

