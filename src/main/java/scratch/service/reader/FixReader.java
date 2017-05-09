package scratch.service.reader;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import scratch.model.Anime;
import scratch.model.AnimeEpisode;

@Service
public class FixReader extends ScratchReader<AnimeEpisode> {

	public FixReader() {
		readerUrl = "http://www.fixsub.com/%E6%88%91%E4%BB%AC%E7%9A%84%E4%BD%9C%E5%93%81";
	}
	
	@Override
	public void ConnectConfig(Map<String, String> configs) {
		configs.put("Connection", "close");
		configs.put("RReferer", "www.baidu.com");
		super.ConnectConfig(configs);
	}
	
	@Override
	protected void read(String html, List<AnimeEpisode> returnList) {
		System.out.println(html);
		
		Map<String, String> videos = new LinkedHashMap<String, String>();
		//解析HTML文档
		Document document = Jsoup.parse(html);
		//获取链接
		Elements eles = document.select(".pg-item");
		for(Element div : eles) {
			Elements as = div.select("a");
			if(as!= null && as.size() > 0) {
				Element a = as.get(0);
				videos.put(a.attr("title"), a.attr("href"));
			}
		}
		//获取名称和链接
		if(videos.size() == 0) return;
		for(Entry<String, String> entry : videos.entrySet()) {
			FixUpdateReader updateReader = new FixUpdateReader(entry.getValue());
			List<AnimeEpisode> episodes = updateReader.read();
			returnList.addAll(episodes);
		}
		System.out.println(returnList);
	}
}

class FixUpdateReader extends ScratchReader<AnimeEpisode> {

	public FixUpdateReader(String url) {
		this.readerUrl = url;
	}
	
	private final static String DOWNLOAD = "百度网盘";
	
	/**
	 * 返回的AnimeEpisode只填充了no和url
	 */
	@Override
	protected void read(String html, List<AnimeEpisode> returnList) {
		Document document = Jsoup.parse(html);
		Elements eles = document.select("#content-container");
		Elements hs = eles.select(".content-page-title");
		
		//获取标题，生成Anime对象
		if(hs == null || hs.size() == 0) return;
		String title = hs.get(0).text();
		if(StringUtils.isEmpty(title)) return;
		Anime anime = new Anime();
		anime.setName(title);
		//获取链接地址
		Elements urls = eles.select("a");
		for(Element u : urls) {
			if(u.text().equals(DOWNLOAD)) {
				AnimeEpisode episode = new AnimeEpisode();
				String number = u.parent().text().replaceAll("[^0-9]", "");
				if(StringUtils.isEmpty(number)) continue;
				String url = u.attr("href");
				episode.setAnime(anime);
				try{
					episode.setNumber(Integer.valueOf(number));
				} catch (Exception e) {
					continue;
				}
				episode.setUrl(url);
				returnList.add(episode);
			}
		}		
	}
}