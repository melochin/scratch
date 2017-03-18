package com.rory.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexBase {
	
//	private static String[][] ESC = {{" "}, {"\\s"}};
	
	/**
	 * 
	 * @param regex
	 * @param str
	 * @return		是否匹配正则
	 */
	public boolean isMatcher(String regex, String str){
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		return m.find();
	}
	
	/**
	 * 
	 * @param regex 正则表达式
	 * @param str	检索的字符串
	 * @return		符合要求的字符串队列
	 */
	public List<String> matcher(String regex, String str) {
		List<String> list = new ArrayList<String>();

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		while (m.find()) {
			list.add(m.group());
		}

		return list;
	}

	/**
	 * 
	 * @param regex	正则表达式
	 * @param str	检索的字符串
	 * @return		符合要求的字符串
	 */
	public String matcherOne(String regex, String str) {
		List<String> list = new ArrayList<String>();
		list = matcher(regex, str);
		if (list.size() <= 0)
			return "";
		else
			return list.get(0);
	}
	
	//零宽断言
	public List<String> intercept(String prefix, String suffix, String midRegex, String str){
		String preRegex = "(?<=" + prefix + ")";
		String sufRegex = "(?=" + suffix + ")";
		String regex = preRegex + midRegex + sufRegex;
		return matcher(regex, str);
	}
	
	//处理转义字符
/*	private String dealWithESC(String regex){
		return "";
	}*/
}
