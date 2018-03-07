package scratch.support.regex;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegexMatch extends RegexBase{
	
	private String sourceText;
	
	private List<String> resultList;
	
	private static Connection c = new Connection();
	
	//-----------------------���캯��---------------------------------
	public RegexMatch(String sourceText){
		this(sourceText, new ArrayList<String>());
	}
	
	public RegexMatch(URL url){
		this(url, null);
	}
	
	public RegexMatch(URL url, Map<String,String> cookie){
		this(c.toHtml(url, cookie));
	}
	
	public RegexMatch(URL url, String method, Map<String, String> param) {
		this(c.toHtml(url, method, param, null));
	}
	
	private RegexMatch(List<String> resultList){
		this(resultList.toString(), resultList);
	}
	
	private RegexMatch(String sourceText, List<String> resultList){
		this.sourceText = sourceText.replace("\n", "");
		this.resultList = resultList;
	}
	
	//-----------------------����get set---------------------------------
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
	
	//-----------------------ƥ�亯��---------------------------------

	//��ȡ��ǩ
	// ex: <a>......................</a>
	// regex: \<a[^\>]*\>\</a\>
	// ���˱�ǩcontent�Ĺ��ˣ���������ƥ��
	public RegexMatch findTag(String tagName){
		//regex 
		//String regex = "\\<" +  tagName +"[^\\>]*\\>[^\\<]*\\</" + tagName + "\\>";
		//��֧����
		String regex = "\\<" +  tagName +"[^\\>]*\\>";
		String regexWithEnd = "\\<" +  tagName +"[^\\>]*\\>.*?\\</" + tagName + "\\>";
		
		return new RegexMatch(matcher(regexWithEnd + '|' + regex , sourceText));
		
	}
	
	//��ȡ�������ֵ
	// ex����src��"................"
	// regex: (?<=src\=")[^"]*(?=")
	public RegexMatch getAttribute(String attributeName){

		String regex = "(?<=" + attributeName + "\\=\")[^\"]*(?=\")";
		return new RegexMatch(matcher(regex, sourceText));
	}
		
	//ץȡ��ǩҳ�е����ݣ�����޷�ץȡ���ؿհ�
	public RegexMatch getContent(){
		//regex 
		return new RegexMatch(intercept("\\>", "\\</", "[^\\<]*", sourceText));
	}
	
	public RegexMatch intercept(String prefix, String suffix, String midRegex){
		return new RegexMatch(intercept(prefix, suffix, midRegex, sourceText));
	}
	
	//�Խ�������й���
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
