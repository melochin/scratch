package scratch.service.specific;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rory.regex.RegexMatch;

import scratch.model.SearchInfo;

public class ScratchBiliVedio implements ScratchReader<SearchInfo>{

	@Override
	public List<SearchInfo> read(URL url) {
		List<SearchInfo> searchInfos = new ArrayList<SearchInfo>();
		try{
			RegexMatch regexMatch = new RegexMatch(url);
			String html = regexMatch.getSourceText();
			html = html.substring(9, html.length()-2);
			System.out.println(html);
			ObjectMapper m = new ObjectMapper();
			JsonNode js = m.readTree(html);
			JsonNode list = js.findValue("data")
							.findValue("archives");
			
			String urlTemplate = "http://www.bilibili.com/video/av";
			for(JsonNode n : list) {
				SearchInfo info = new SearchInfo();
				String title = n.findValue("title").asText();
				String pic = n.findValue("pic").asText();
				String vUrl = urlTemplate + n.findValue("aid").asText();
				info.setTitle(title);
				info.setPic(pic);
				info.setUrl(vUrl);
				searchInfos.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return searchInfos;
	}
	
	
	public static void main(String[] args) throws MalformedURLException {
		ScratchBiliVedio s = new ScratchBiliVedio();
		s.read(new URL("http://api.bilibili.com/archive_rank/getarchiverankbypartion?callback=callback&type=jsonp&tid=82&pn=1&_=1490537389048"));
	}

}
