package scratch.dao.inter;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import scratch.model.entity.Dict;
import scratch.model.ohter.DictList;

public interface IDictDao {

	List<Dict> findAll();
	
	//List<Dict> findByType(@Param("type") String type);
	
	@MapKey("name")
	List<Map<String, Object>> findByType(@Param("type") String type);
	
	/**
	 * @param parentCode
	 * @param mustBeUsed	true 不包含停止使用的 	false 包含停止使用的 
	 * @return
	 */
	DictList findByParentCode(
			@Param("parentCode") String parentCode, @Param("mustBeUsed") boolean mustBeUsed);
	
	
	int save(@Param("dict") Dict dict);
	
	int update(@Param("dict") Dict dict);
	
	int delete(@Param("dict") Dict dict);
	
	Dict findByCodeAndParentCode(@Param("code") String code, @Param("parentCode") String parentCode);
	
}
