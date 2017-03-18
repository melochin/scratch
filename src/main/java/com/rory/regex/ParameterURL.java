package com.rory.regex;

import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 含有参数的URL
 * @author Admin
 *
 */
public class ParameterURL{

	private String templateUrl;
	
	private Map<String,String> parameters;

	public ParameterURL(String url){
		this.templateUrl = url;
		parameters = new HashMap<String,String>();
	}
	
	public void setParameter(String parameter, String value){
		
		if(templateUrl.indexOf(":"+parameter) == -1){
			try {
				throw new Exception(parameter + "参数不存在");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		parameters.put(parameter, value);
		
	}
	
	public URL getURL(){
		URL u = null;
		String url = templateUrl;
		
		try{
			for(String key:parameters.keySet()){
				url = url.replace(":"+key, URLEncoder.encode(parameters.get(key),"utf-8"));
			}
			
			if(url.indexOf("=:") > 0){
				try {
					throw new Exception("仍有参数未绑定");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			u = new URL(url);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return u;
	}
	
}
