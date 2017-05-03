package scratch.support.regex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegexUnity {
	
	private String text;
	
	private List<String> result;
	
	public RegexUnity(String text){
		this.text = text;
		this.result = new ArrayList<String>();
	}
	
	private RegexUnity(String text, List<String> result){
		this.text = text;
		this.result = result;
	}
	
	
	//��ȡ��ǩ
	// ex: <a>......................</a>
	// regex: \<a[^\>]*\>\</a\>
	public RegexUnity findTag(String tagName){
		//regex 
		//String regex = "\\<" +  tagName +"[^\\>]*\\>[^\\<]*\\</" + tagName + "\\>";
		String regex = "\\<" +  tagName +"[^\\>]*\\>";
		result.clear();
		result.addAll(SratchUnity.matcher(regex, text));
		return new RegexUnity(result.toString(), result);
		
	}
	
	//��ȡ�������ֵ
	// ex����src��"................"
	// regex: (?<=src\=")[^"]*(?=")
	public RegexUnity getAttribute(String attributeName){

		String regex = "(?<=" + attributeName + "\\=\")[^\"]*(?=\")";
		result.clear();
		result.addAll(SratchUnity.matcher(regex, text));
		return new RegexUnity(result.toString(), result);
	}
	
	//�Խ�������й���
	public RegexUnity filter(String keyword){
		if(result.size() == 0){
			return this;
		}
		for(int i=result.size()-1; i>=0; i--){
			if(!SratchUnity.isMatcher(keyword, result.get(i))){
				result.remove(i);
			}
		}
		
		return new RegexUnity(result.toString(), result);
	}
	
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getResult() {
		return result;
	}

	public void setResult(List<String> result) {
		this.result = result;
	}
	
	public static void main(String[] args) throws IOException {
		String url = "http://search.bilibili.com/all?keyword=hey+say+jump";
		String html = SratchUnity.getHtml("http://search.bilibili.com/all?keyword=hey+say+jump", null);
		System.out.println("�L��վ�c��" + url);
		System.out.println("ץȡvideo���}��:");
		List<String> result = new RegexUnity(html).
				findTag("a").
				filter("video").
				filter("se-linkid").
				getAttribute("title").
				getResult();
		
		for(String s:result){
			System.out.println(s);
		}
	}

}
