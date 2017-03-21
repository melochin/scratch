package com.rory.regex;

import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 设置URL参数
 * @author melochin
 *
 */
public class ParameterURL{

	private String templateUrl;
	
	private Map<String,String> parameters;

	public ParameterURL(String url){
		this.templateUrl = url;
		parameters = new HashMap<String,String>();
	}
	
	/**
	 * 设置参数
	 * @param parameter
	 * @param value
	 */
	public void setParameter(String parameter, String value){
		parameters.put(parameter, value);
	}
	
	public URL getURL(){
		URL u = null;
		StringBuilder url = new StringBuilder(templateUrl);
		try{
			int i=0;
			for(Entry<String, String> entry:parameters.entrySet()){
				String key = URLEncoder.encode(entry.getKey(),"utf-8");
				String value = URLEncoder.encode(entry.getValue(),"utf-8");
				if(i == 0) {
					url.append("?");
				} else {
					url.append("&");
				}
				url.append(key + "=" + value);
				i++;
			}
			u = new URL(url.toString());
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return u;
	}
	
}
