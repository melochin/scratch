package scratch.service.reader;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import scratch.model.Anime;
import scratch.model.AnimeEpisode;
import scratch.support.regex.RegexMatch;

@Service
public class AnimeUpdateReader extends ScratchReader<List<AnimeEpisode>> {

	{
		readerUrl = "http://www.dilidili.wang";
	}

	private final static String[] WEEKDAYS = {"#weekdiv1", "#weekdiv2", "#weekdiv3", 
			"#weekdiv4", "#weekdiv5", "#weekdiv6", "#weekdiv7"};
	
	@Override
	protected void read(String html, RegexMatch match, List<List<AnimeEpisode>> returnList) {
		Document document = Jsoup.parse(html);
		for(int i=0; i<WEEKDAYS.length; i++) {
			List<AnimeEpisode> animeEpisodes = readWeekDay(document, WEEKDAYS[i]);
			returnList.add(animeEpisodes);
		}
	}
	
	private List<AnimeEpisode> readWeekDay(Document document, String weekDay) {
		//过滤某星期日的数据
		Elements root = document.select(weekDay);
		Elements items = root.select(".week_item");
		List<AnimeEpisode> animeEpisodes = new ArrayList<AnimeEpisode>();
		for(Element item : items) {
			
			Elements itemRights = item.select(".week_item_right").select("a");
			if(itemRights.size() <= 0) continue;

			AnimeEpisode animeEpisode = new AnimeEpisode();
			//更新链接
			Element episode = itemRights.get(0);
			animeEpisode.setUrl(episode.attr("href"));
			//更新第几集
			String number = episode.text().replaceAll("[^1-9]", "");
			//会出现number不存在的情况
			if(StringUtils.isEmpty(number)) continue;
			animeEpisode.setNumber(Integer.valueOf(number));
			//Anime Name
			Elements itemLefts = item.select(".week_item_left").select("a");
			
			if(itemLefts.size() <= 0) continue;
			animeEpisode.setAnime(new Anime(itemLefts.get(0).text()));
			animeEpisodes.add(animeEpisode);
		}
		return animeEpisodes;
	}
	
}