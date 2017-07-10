package scratch.dao.inter;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageRowBounds;

import scratch.model.Anime;

public interface IAnimeDao {

	List<Anime> findAll();
	
	List<Anime> findWithAlias();
	
	Page<Anime> find(PageRowBounds page);
	
	List<Anime> findByFinished(@Param("finished") Boolean finished);
	
	List<Anime> findByType(@Param("type")String type);
	
	Anime findById(@Param("id") Long id);
	
	Anime findByAlias(@Param("alias") String alias, Boolean finished);
	
	void save(@Param("anime") Anime anime);
	
	void update(@Param("anime") Anime anime);
	
	void delete(@Param("id") Long id);

	
}
