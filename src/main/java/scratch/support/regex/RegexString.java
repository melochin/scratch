package scratch.support.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexString {
	
	//���ض��м�¼��
	public static List<String> matcher(String regex, String str) {
		List<String> list = new ArrayList<String>();

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		while (m.find()) {
			list.add(m.group());
		}

		return list;
	}
	
	//����һ�м�¼��
	public static String matcherOne(String regex, String str) {
		List<String> list = new ArrayList<String>();
		list = matcher(regex, str);
		if (list.size() <= 0)
			return "";
		else
			return list.get(0);
	}
	
	//ȡhref�ڲ���ֵ
	public static String matcherByNear(String pre, String next, String str){
		
		return null;
	}
	
	//ת�������ַ�
	//(?<=\<div\sid\="i1"\sclass\="sni".{1,50}\<h1\>)[^\<]*(?=\</h1)
	
}
