package scratch.dao.inter;

import org.apache.ibatis.annotations.Param;
import scratch.model.entity.AnimeAlias;
import java.util.List;

public interface IAnimeAliasDao {

	/**
	 * 列出指定anime和host的别名
	 * @param animeId
	 * @param hostId
	 * @return
	 */
	AnimeAlias find(@Param("animeId") Long animeId,
					@Param("hostId") Long hostId);

	/**
	 * 列出指定anime的所有别名
	 * @param animeId
	 * @return
	 */
	List<AnimeAlias> list(@Param("animeId") Long animeId);


	// TODO 完成SQL
	List<AnimeAlias> listByHost(@Param("hostId") Long hostId);

	List<Long> listAnimeIdByHost(@Param("hostId") Long hostId);

	/**
	 * 保存别名
	 * @param animeAlias
	 */
	void save(@Param("alias") AnimeAlias animeAlias);

	/**
	 * 删除anime关联的所有别名
	 * @param animeId
	 */
	void delete(@Param("animeId") Long animeId);

}
