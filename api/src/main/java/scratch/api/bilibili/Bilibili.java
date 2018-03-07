package scratch.api.bilibili;

import java.util.List;
import java.util.Map;

import scratch.api.bilibili.Video;

public interface Bilibili {

	/**
	 * 根据关键字搜索视频
	 * @param keyword 关键字
	 * @return
	 */
	List<Video> search(String keyword);

	/**
	 * 根据关键字搜索视频
	 * @param keyword 关键字
	 * @param page 页号
	 * @return
	 */
	List<Video> search(String keyword, int page);

	/**
	 * 根据类型和页数搜索视频
	 * @param videoType
	 * @param page
	 * @return
	 */
	List<Video> getVideos(VideoType videoType, int page);

	/**
	 * 获取视频页数和总数
	 * @param videoType 视频类型
	 * @return
	 */
	Map<String, Integer> getVideoPageAndCount(VideoType videoType);

	/**
	 * 获取视频页数和总数
	 * @param videoType 视频类型(数值)
	 * @return
	 */
	Map<String, Integer> getVideoPageAndCount(int videoType);

	/**
	 * 判断用户是否登录
	 * @return
	 */
	boolean isLogin();
	
}
