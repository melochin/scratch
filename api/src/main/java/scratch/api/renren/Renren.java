package scratch.api.renren;

import java.util.List;

public interface Renren {
	
	/**
	 * 根据关键字搜索信息
	 * @param keyword 关键字
	 * @return
	 */
	List<Video> search(String keyword);

	/**
	 * 获取下载链接
	 * @param downloadUrl 放出下载链接的页面
	 * @return
	 */
	List<VideoEpisode> getEpisodeList(String downloadUrl);
	
}
