package scratch.dao.inter;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import scratch.model.entity.AnimeFocus;

public interface IAnimeFocusDao {

	List<AnimeFocus> findAll();
	
	List<AnimeFocus> findByUserId(@Param("userId") Long userId);

	void save(@Param("focus") AnimeFocus focus);
	
	void update(@Param("focus") AnimeFocus focus);
	
	void delete(@Param("id") Long id);
	
	void deleteByAnimeAndUser(@Param("animeId") Long animeId, @Param("userId") Long userId);
	
}
