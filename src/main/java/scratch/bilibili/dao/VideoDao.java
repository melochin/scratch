package scratch.bilibili.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import scratch.bilibili.model.Video;
import scratch.dao.BasicDao;

@Repository
public class VideoDao extends BasicDao<Video>{

	private static final String QUERY_COUNT_TYPE = "select type.code,count(*) from Video group by type.code";
	
	//private static final String QUERY_TITLE = "from Video where title like "
	
	@SuppressWarnings("unchecked")
	public Map<Long, Long> listCountByType() {
		Map<Long, Long> map = new HashMap<Long, Long>();
		List<Map> list = getCurrentSession().createQuery(QUERY_COUNT_TYPE)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
		for(Map m:list) {
			map.put((Long)m.get("0"), (Long)m.get("1"));
		}
		return map;
	}
	
	public List<Video> list(String keyword) {
		return null;
		
	}
	
	
}
