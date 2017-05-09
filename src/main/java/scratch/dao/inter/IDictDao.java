package scratch.dao.inter;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import scratch.model.Dict;

public interface IDictDao {

	List<Dict> findAll();
	
	//List<Dict> findByType(@Param("type") String type);
	
	@MapKey("name")
	List<Map<String, Object>> findByType(@Param("type") String type);
	
}
