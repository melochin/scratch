package scratch.dao.inter;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageRowBounds;

import scratch.model.Video;

public interface IVideoDao {
	
	/**
	 * 视频检索
	 * 根据关键字、视频类型检索
	 * @param keyword
	 * @param type
	 * @param order
	 * @param page
	 * @return
	 */
	Page<Video> list(@Param("words") List<String> words, 
			@Param("types") List<Integer> types, 
			@Param("order") String order,
			@Param("page") PageRowBounds page);
	
	/**
	 * 保存
	 * @param video
	 */
	void save(@Param("video") Video video);
	
	/**
	 * 更新
	 * @param video
	 */
	void update(@Param("video") Video video);
	
	/**
	 * 根据ID获取对象
	 * @param id
	 * @return
	 */
	Video getById(@Param("id") Long id);
	
	/**
	 * 按视频类别，统计视频个数
	 * Map key: count	视频个数
	 *  		tid		视频类别
	 * @return
	 */
	List<Map<String, Object>> countsByType();
}
