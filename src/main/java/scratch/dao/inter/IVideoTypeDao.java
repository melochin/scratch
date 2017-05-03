package scratch.dao.inter;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import scratch.model.VideoType;

public interface IVideoTypeDao {
	
	/**
	 * 检索鄋类型，除了code为0的特殊情况
	 * @return
	 */
	List<VideoType> list();

	/**
	 * 根据ID检索关联的父类型或者子类型
	 * @param typeId
	 * @return
	 */
	List<VideoType> listById(@Param("id") Integer typeId);
	
	/**
	 * 保存对象，主键不会自动生成，需要手工指定
	 * @param type
	 */
	void save(VideoType type);
	
	/**
	 * 按主键更新
	 * @param type
	 */
	void modify(VideoType type);
	
	/**
	 * 按主键删除
	 * @param typeId
	 */
	void delete(@Param("id") Integer typeId);
}
