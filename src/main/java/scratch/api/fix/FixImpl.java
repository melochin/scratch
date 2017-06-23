package scratch.api.fix;
 
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import scratch.model.Anime;
import scratch.model.AnimeEpisode;
import scratch.support.web.HttpConnection;

public class FixImpl implements Fix {

	private final static String detailUrl = "http://www.fixsub.com/portfolio/{videoName}";
	
	private final static Logger log = Logger.getLogger(FixImpl.class);
	
	// 解析页面需要的元素名称

	private final static String A = "a";
	
	private final static String HREF = "href";
	
	private final static String CONTENT_PAGE_TITLE = ".content-page-title";
	
	private final static String CONTENT_CONTAINER = "#content-container";
	
	private final static String DOWNLOAD = "百度网盘";
	
	@Override
	public List<AnimeEpisode> getDownloadList(String videoName) {
		
		String url = UriComponentsBuilder.fromUriString(detailUrl).build()
				.expand(videoName).encode().toUriString();
		
		if(log.isDebugEnabled()) {
			log.debug("访问地址：" + url);
		}
		
		String html = new HttpConnection().connect(url);

		// parse html
		Document document = Jsoup.parse(html);
		Elements eles = document.select(CONTENT_CONTAINER);
		
		String title = getTitle(eles.select(CONTENT_PAGE_TITLE));
		
		Anime anime = new Anime();
		anime.setName(title);
		
		// 获取所有链接
		Elements urlElement = eles.select(A);
		Elements downloadElement = new Elements();
		// 保存百度网盘的下载链接
		for(Element u : urlElement) {
			if(u.text().equals(DOWNLOAD)) {
				downloadElement.add(u);
			}
		}
		
		// 获取元素数据
		List<AnimeEpisode> epsiodes = downloadElement.stream()
					.map(ele -> doDownload(ele))
					.collect(Collectors.toList());
		
		for(AnimeEpisode e : epsiodes) {
			e.setAnime(anime);
		}
		
		return epsiodes;
	}
	
	/**
	 * {@link #getDownloadList(String)}
	 * @param titleElement
	 * @return
	 */
	private String getTitle(Elements titleElement) {
		
		if(titleElement == null || titleElement.size() <= 0) {
			return null;
		}
		
		String title = titleElement.get(0).text();
		
		if(StringUtils.isEmpty(title)) {
			return null;
		}
		
		return title;
	}
	
	/**
	 * {@link #getDownloadList(String)}
	 * @param ele
	 * @return
	 */
	private AnimeEpisode doDownload(Element ele) {
		AnimeEpisode episode = new AnimeEpisode();
		//防止numbe转换出错
		String number = ele.parent().text().replaceAll("[^0-9]", "");
		Integer num = null;
		if(!StringUtils.isEmpty(number)) {
			try{
				num = Integer.valueOf(number);
			} catch (Exception e) {}
		}
		//episode数据填充
		episode.setUrl(ele.attr(HREF));
		episode.setNumber(num);
		return episode;
	}
	
}