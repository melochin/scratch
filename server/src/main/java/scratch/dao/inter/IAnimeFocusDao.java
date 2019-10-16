package scratch.dao.inter;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import scratch.model.entity.AnimeFocus;

public interface IAnimeFocusDao {

	AnimeFocus findByAnimeAndUser(
			@Param("animeId") Long animeId, @Param("userId") Long userId);

	List<AnimeFocus> listByUserId(@Param("userId") Long userId);

	void save(@Param("focus") AnimeFocus focus);

	void update(@Param("focus") AnimeFocus focus);

	void delete(@Param("id") Long focusId);

	void deleteByAnimeAndUser(@Param("animeId") Long animeId, @Param("userId") Long userId);

	int count(@Param("animeId") Long animeId);

	List<Long> listMostFocused(@Param("limit") int limit);

}
