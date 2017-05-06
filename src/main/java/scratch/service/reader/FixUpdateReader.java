package scratch.service.reader;

import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import scratch.model.AnimeEpisode;
import scratch.support.regex.RegexMatch;

public class FixUpdateReader extends ScratchReader<List<AnimeEpisode>> {

	{
		readerUrl = "http://www.fixsub.com/portfolio/%E8%B6%8A%E7%8B%B1";
	}
	
	private final static String DOWNLOAD = "百度网盘";
	
	@Override
	public void ConnectConfig(Map<String, String> configs) {
		configs.put("Cookie", "__cfduid=df8d1f58f9ebab8a13a575d757d313b941493977319; Hm_lvt_af75f52bfdd411d166c23dc7aa879aa5=1493977370; Hm_lpvt_af75f52bfdd411d166c23dc7aa879aa5=1493977370");
		configs.put("User-agent", "  Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
	}
	
	
	@Override
	protected void read(String html, RegexMatch match, List<List<AnimeEpisode>> returnList) {
		Document document = Jsoup.parse(html);
		Elements eles = document.select("#content-container");
		Elements urls = eles.select("a");
		for(Element u : urls) {
			if(u.text().equals(DOWNLOAD)) {
				System.out.println(u.parent().text());
				System.out.println(u.attr("href"));
			}
		}
	}
	
	
}