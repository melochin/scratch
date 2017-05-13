package scratch.service.reader.anime;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import scratch.model.Anime;
import scratch.model.AnimeEpisode;
import scratch.service.anime.AnimeScratchService;
import scratch.service.reader.ScratchReader;

public class FixReader extends ScratchReader<AnimeEpisode> {

	private static final  Logger log = Logger.getLogger(FixReader.class);

	private static final String HREF = "href";

	private static final String PG_ITEM = ".pg-item";
	
	private static final String A = "a";
	
	private static final String TITLE = "title";
	
	private static final String URL = "http://www.fixsub.com/%E6%88%91%E4%BB%AC%E7%9A%84%E4%BD%9C%E5%93%81?set=";
	
	private AnimeScratchService serivce;
	
	/**
	 * 读取FIX作品栏的第page页
	 * @param page
	 */
	public FixReader(int page, AnimeScratchService serivce ) {
		this.readerUrl = URL + page;
		this.serivce = serivce;
	}
	
	@Override
	public void ConnectConfig(Map<String, String> configs) {
		configs.put("Connection", "close");
		super.ConnectConfig(configs);
	}
	
	/**
	 * 解析作品一览界面，并从具体作品页面中获取更新链接
	 */
	@Override
	protected void read(String html, List<AnimeEpisode> returnList) {
		Map<String, String> videos = new LinkedHashMap<String, String>();
		Document document = Jsoup.parse(html);
		
		Elements eles = document.select(PG_ITEM);
		if(eles.size() == 0) {
			log.error("cant find DOM Elements [" + PG_ITEM +"]");
		}
		
		//获取名称和具体页面链接
		for(Element div : eles) {
			Elements as = div.select(A);
			if(as.size() == 0) continue;
			Element a = as.get(0);
			//key:name, value:url
			videos.put(a.attr(TITLE), a.attr(HREF));
		}
		
		if(log.isDebugEnabled()) {
			log.debug("get videos: " + videos.size());
		}
		
		//解析具体页面链接
		if(videos.size() == 0) return;
		for(Entry<String, String> entry : videos.entrySet()) {
			
			//判断数据库中的anime是否存在，不存在直接下一个
			Anime anime = serivce.getAnime(entry.getKey());
			if(anime == null) continue;
			
			//读取anime对应的episode数据
			FixUpdateReader updateReader = new FixUpdateReader(entry.getValue());
			List<AnimeEpisode> episodes = updateReader.read();
			
			//设置episode的anime
			for(AnimeEpisode episode : episodes) {
				episode.setAnime(anime);
			}
			
			returnList.addAll(episodes);
		}
	}
}

class FixUpdateReader extends ScratchReader<AnimeEpisode> {

	private static final String A = "a";
	
	private static final String HREF = "href";
	
	private static final String CONTENT_PAGE_TITLE = ".content-page-title";
	
	private static final String CONTENT_CONTAINER = "#content-container";
	
	private static final  String DOWNLOAD = "百度网盘";

	public FixUpdateReader(String url) {
		this.readerUrl = url;
	}
	
	/**
	 * 解析页面，转换成Episode对象
	 */
	@Override
	protected void read(String html, List<AnimeEpisode> returnList) {
		Document document = Jsoup.parse(html);
		Elements eles = document.select(CONTENT_CONTAINER);
		Elements hs = eles.select(CONTENT_PAGE_TITLE);
		if(hs == null || hs.size() == 0) return;

		//获取标题，生成Anime对象
		String title = hs.get(0).text();
		if(StringUtils.isEmpty(title)) return;
		Anime anime = new Anime();
		anime.setName(title);
		
		//获取链接地址和集
		Elements urls = eles.select(A);
		for(Element u : urls) {
			if(u.text().equals(DOWNLOAD)) {
				AnimeEpisode episode = new AnimeEpisode();
				//防止numbe转换出错
				String number = u.parent().text().replaceAll("[^0-9]", "");
				Integer num = null;
				if(!StringUtils.isEmpty(number)) {
					try{
						num = Integer.valueOf(number);
					} catch (Exception e) {}
				}
				//episode数据填充
				episode.setUrl(u.attr(HREF));
				episode.setNumber(num);
				episode.setAnime(anime);
				
				returnList.add(episode);
			}
		}		
	}
}