package com.rory.regex;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import scratch.image.unity.SratchUnity;

public class RegexMatch extends RegexBase{
	
	private String sourceText;
	
	private List<String> resultList;
	
	private static Connection c = new Connection();
	
	//-----------------------构造函数---------------------------------
	public RegexMatch(String sourceText){
		this(sourceText, new ArrayList<String>());
	}
	
	public RegexMatch(URL url){
		this(url, null);
	}
	
	public RegexMatch(URL url, Map<String,String> cookie){
		this(c.toHtml(url, cookie));
	}
	
	private RegexMatch(List<String> resultList){
		this(resultList.toString(), resultList);
	}
	
	private RegexMatch(String sourceText, List<String> resultList){
		this.sourceText = sourceText.replace("\n", "");
		this.resultList = resultList;
	}
	
	//-----------------------基本get set---------------------------------
	public String getSourceText() {
		return sourceText;
	}

	public void setSourceText(String sourceText) {
		this.sourceText = sourceText;
	}
	
	public List<String> getResultList() {
		return resultList;
	}

	public void setResultList(List<String> resultList) {
		this.resultList = resultList;
	}
	
	public String getReuslt() throws Exception{
		if(resultList.size() > 1){
			throw new Exception("have more than one result");
		}
		
		if(resultList.size() == 0){
			return "";
		}else{
			return resultList.get(0);
		}
		
	}
	
	//-----------------------匹配函数---------------------------------

	//获取标签
	// ex: <a>......................</a>
	// regex: \<a[^\>]*\>\</a\>
	// 带了标签content的过滤，采用懒惰匹配
	public RegexMatch findTag(String tagName){
		//regex 
		//String regex = "\\<" +  tagName +"[^\\>]*\\>[^\\<]*\\</" + tagName + "\\>";
		//分支条件
		String regex = "\\<" +  tagName +"[^\\>]*\\>";
		String regexWithEnd = "\\<" +  tagName +"[^\\>]*\\>.*?\\</" + tagName + "\\>";
		
		return new RegexMatch(matcher(regexWithEnd + '|' + regex , sourceText));
		
	}
	
	//获取属性里的值
	// ex：　src＝"................"
	// regex: (?<=src\=")[^"]*(?=")
	public RegexMatch getAttribute(String attributeName){

		String regex = "(?<=" + attributeName + "\\=\")[^\"]*(?=\")";
		return new RegexMatch(matcher(regex, sourceText));
	}
		
	//抓取标签页中的内容，如果无法抓取返回空白
	public RegexMatch getContent(){
		//regex 
		return new RegexMatch(intercept("\\>", "\\</", "[^\\<]*", sourceText));
	}
	
	public RegexMatch intercept(String prefix, String suffix, String midRegex){
		return new RegexMatch(intercept(prefix, suffix, midRegex, sourceText));
	}
	
	//对结果集进行过滤
	public RegexMatch filter(String keyword){
		
		if(resultList.size() == 0){
			return this;
		}
		
		for(int i=resultList.size()-1; i>=0; i--){
			if(!SratchUnity.isMatcher(keyword, resultList.get(i))){
				resultList.remove(i);
			}
		}
		
		return new RegexMatch(resultList);
	}
	
	
}
