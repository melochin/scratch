package scratch.dao.inter;

import org.apache.ibatis.annotations.Param;
import scratch.model.entity.AnimeAlias;

import java.util.List;

public interface IAnimeAliasDao {

	AnimeAlias find(@Param("animeId") Long animeId,
					@Param("hostId") Long hostId);

	List<AnimeAlias> list(@Param("animeId") Long animeId);

	void save(@Param("alias") AnimeAlias alias);

	void modify(@Param("alias") AnimeAlias alias);

	void delete(@Param("alias") AnimeAlias alias);

}
