package scratch.dao.inter;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import org.springframework.security.access.method.P;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;
import scratch.model.entity.AnimeEpisodeScratch;

public interface IAnimeEpisodeScratchDao {
	
	AnimeEpisodeScratch getById(@Param("id") Long id);

	
	/**
	 * 根据URL查找对象
	 * @param url
	 * @return
	 */
	AnimeEpisodeScratch findByUrl(@Param("url") String url);

	/**
	 * 查询anime_episode_all视图，判断url是否存在
	 * @param url
	 * @return
	 */
	boolean isExistUrl(@Param("url") String url);

	/**
	 * 以Anime id 和抓取时间>time为条件，检索对象
	 * @param anime
	 * @param time
	 * @return
	 */
	List<AnimeEpisode> findByAnimeAndTime(@Param("anime") Anime anime, @Param("time") Date time);
	
	List<AnimeEpisodeScratch> listByStatus(@Param("status") Integer status);
	
	Integer countByStatus(@Param("status") Integer status);

	List<Map> listCountByStatus();

	/** 更新服务 **/
	
	void save(@Param("episode") AnimeEpisodeScratch episode);
	
	void modifyStatus(@Param("id") Long id, @Param("status") Integer status);

	void delete(@Param("id") Long scratchId);
}
