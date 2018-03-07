package scratch.dao.inter;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;

public interface IAnimeEpisodeDao {

	/**
	 * 根据URL查找对象
	 * @param url
	 * @return
	 */
	AnimeEpisode findByUrl(@Param("url") String url);
	
	List<AnimeEpisode> listByAnimeId(@Param("animeId") Long animeId);
	
	/**
	 * 以Anime id 和抓取时间>time为条件，检索对象
	 * @param anime
	 * @param time
	 * @return
	 */
	List<AnimeEpisode> findByAnimeAndTime(@Param("anime") Anime anime, @Param("time") Date time);
	
	List<AnimeEpisode> findByTime(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);
	
	List<AnimeEpisode> list();
	
	AnimeEpisode getById(@Param("id") Long id);
	
	int save(@Param("episode") AnimeEpisode episode);
	
	/**
	 * 
	 * @param episode
	 * anime, hostId 不能变更
	 * @return
	 */
	int modify(@Param("episode") AnimeEpisode episode);
	
	int delete(@Param("id") Long id);
	
	int deleteByUrl(@Param("url") String url);


	
}
