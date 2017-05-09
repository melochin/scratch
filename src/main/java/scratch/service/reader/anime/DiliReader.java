package scratch.service.reader.anime;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import scratch.model.Anime;
import scratch.model.AnimeEpisode;
import scratch.service.reader.ScratchReader;

public class DiliReader extends ScratchReader<AnimeEpisode> {

	private final static Logger log = Logger.getLogger(DiliReader.class);
	
	public DiliReader() {
		readerUrl = "http://www.dilidili.wang";
	}

	private final static String[] WEEKDAYS = {"#weekdiv1", "#weekdiv2", "#weekdiv3", 
			"#weekdiv4", "#weekdiv5", "#weekdiv6", "#weekdiv7"};

	@Override
	protected void read(String html, List<AnimeEpisode> returnList) {
		Document document = Jsoup.parse(html);
		//解析7个DIV，读取7天的更新情况
		for(int i=0; i<WEEKDAYS.length; i++) {
			List<AnimeEpisode> animeEpisodes = readWeekDay(document, WEEKDAYS[i]);
			returnList.addAll(animeEpisodes);
		}
	}
	
	private List<AnimeEpisode> readWeekDay(Document document, String weekDay) {
		//过滤某星期日的数据
		Elements root = document.select(weekDay);
		if(root.size() == 0) {
			log.error("DOM元素[" + weekDay +"]丢失");
		}
		
		List<AnimeEpisode> animeEpisodes = new ArrayList<AnimeEpisode>();
		Elements items = root.select(".week_item");
		for(Element item : items) {
			//<a href="链接">集</a>
			Elements itemRights = item.select(".week_item_right").select("a");
			if(itemRights.size() <= 0) continue;

			AnimeEpisode animeEpisode = new AnimeEpisode();
			
			//获取链接
			Element episode = itemRights.get(0);
			animeEpisode.setUrl(episode.attr("href"));
			//获取集
			String number = episode.text().replaceAll("[^0-9]", "");
			Integer num = null;
			if(!StringUtils.isEmpty(number)) {
				try{
					num = Integer.valueOf(number);
				} catch (Exception e) {}
			}
			animeEpisode.setNumber(num);
			
			//获取名字
			Elements itemLefts = item.select(".week_item_left").select("a");
			if(itemLefts.size() <= 0) continue;
			animeEpisode.setAnime(new Anime(itemLefts.get(0).text()));
			
			animeEpisodes.add(animeEpisode);
		}
		return animeEpisodes;
	}
	
}