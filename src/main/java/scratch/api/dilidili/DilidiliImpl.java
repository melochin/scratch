package scratch.api.dilidili;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import scratch.model.AnimeAlias;
import scratch.model.AnimeEpisode;
import scratch.support.NumberUtils;
import scratch.support.web.HttpConnection;

public class DilidiliImpl implements Dilidili {
	
	private final static String detailUrl = "http://www.dilidili.wang/anime/{alias}";
	
	private final static Logger log = Logger.getLogger(DilidiliImpl.class);

	@Override
	public List<AnimeEpisode> getEpisodeList(String alias) {
		
		// get url
		String url = UriComponentsBuilder.fromUriString(detailUrl).build()
				.expand(alias).toUriString();
		
		if(log.isDebugEnabled()) {
			log.debug("get url:" + url);
		}
		
		// get html
		String html = new HttpConnection().connect(url);
		
		// parse html
		Document document = Jsoup.parse(html);
		
		Elements eles = document.select(".time_pic").select("ul").select("li");
		
		List<AnimeEpisode> episodes = eles.stream()
				.map(ele -> doEpisode(ele))
				.collect(Collectors.toList());
		
		return episodes;
	}
	
	
	private AnimeEpisode doEpisode(Element ele) {
		
		AnimeEpisode episode = new AnimeEpisode();
		
		String url = ele.select("a").attr("href");
		
		String no = ele.select("span").text();
		
		episode.setUrl(url);
		episode.setNumber(NumberUtils.toInteger(no).toString());
		
		return episode;
	}

}
