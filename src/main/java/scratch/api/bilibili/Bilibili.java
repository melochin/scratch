package scratch.api.bilibili;

import java.util.List;
import java.util.Map;

import scratch.model.Video;

public interface Bilibili {
	
	/** 通过B站的视频类型ID和页数，查找视频 */
	List<Video> getVideos(VideoType videoType, int page);
	
	/** 获取某类视频的总个数和总页数 */
	Map<String, Integer> getVideoPageAndCount(VideoType videoType);
	
	Map<String, Integer> getVideoPageAndCount(int videoType);

	boolean isLogin();
	
}
