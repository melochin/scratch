package scratch.dao.inter;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageRowBounds;

import org.springframework.security.access.method.P;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeAlias;

public interface IAnimeDao {
	
	/**---------------------------查询----------------------------------**/
	
	Anime getById(@Param("id") Long id);
	
	/**查询所有的anime**/
	List<Anime> list();

	/**条件查询:按类型和是否完结查询，如果参数为null,则不起条件限制作用**/
	List<Anime> listIf(@Param("type") String type, @Param("finished") Boolean finished);

	List<Anime> listByName(@Param("name") String name);

	/**关联查询：查询的anime带有alias数据**/
	List<Anime> listWithAlias();
	
	/**关联查询：查询的anime，按focus数量排序**/
	List<Anime> listMostFocused();

	/** 注意：由于排序中使用到了focus表，因此会进行表关联查询  **/
	List<Anime> listByTypeLeftJointFocus(@Param("type") String type);
	
	List<Anime> listByNameLeftJoinFocus(@Param("name") String name);
	
	/**分页查询：根据类型，查询anime**/
	Page<Anime> pageByType(@Param("type") String type, @Param("page") PageRowBounds page);
	
	/**--------------------------更新--------------------------------------**/
	
	void save(@Param("anime") Anime anime);
	
	void update(@Param("anime") Anime anime);
	
	void delete(@Param("id") Long id);

	Anime find(Long animeId);
}
