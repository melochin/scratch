package scratch.support.regex;

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
	 * @return		�Ƿ�ƥ������
	 */
	public boolean isMatcher(String regex, String str){
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		return m.find();
	}
	
	/**
	 * 
	 * @param regex ������ʽ
	 * @param str	�������ַ���
	 * @return		����Ҫ����ַ�������
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
	 * @param regex	������ʽ
	 * @param str	�������ַ���
	 * @return		����Ҫ����ַ���
	 */
	public String matcherOne(String regex, String str) {
		List<String> list = new ArrayList<String>();
		list = matcher(regex, str);
		if (list.size() <= 0)
			return "";
		else
			return list.get(0);
	}
	
	//������
	public List<String> intercept(String prefix, String suffix, String midRegex, String str){
		String preRegex = "(?<=" + prefix + ")";
		String sufRegex = "(?=" + suffix + ")";
		String regex = preRegex + midRegex + sufRegex;
		return matcher(regex, str);
	}
	
	//����ת���ַ�
/*	private String dealWithESC(String regex){
		return "";
	}*/
}
