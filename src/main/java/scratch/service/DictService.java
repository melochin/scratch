package scratch.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import scratch.dao.inter.IDictDao;

@Service
public class DictService {
	
	@Autowired 
	private IDictDao dao;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redis;
	
	private final static String REDIS_PREFIX = "dic_";
	
	@SuppressWarnings("unchecked")
	public Map<Long, String> findByType(String type) {
		
		if(redis.hasKey(REDIS_PREFIX + type)) {
			return  (Map<Long, String>) redis.opsForValue().get(REDIS_PREFIX + type);
		}
		
		List<Map<String, Object>> maps = dao.findByType(type);
		if(maps == null || maps.size() == 0) {
			return null;
		}
		Map<Long, String> dics = new LinkedHashMap<Long, String>();
		for(Map<String, Object> m : maps) {
			dics.put((Long)m.get("code"), (String)m.get("name"));
		}
		
		redis.opsForValue().set(REDIS_PREFIX + type, dics, 6, TimeUnit.HOURS);
		
		return dics;
	}
}

