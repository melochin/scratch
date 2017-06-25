package scratch.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConnection;
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
	
	private final static Logger log = Logger.getLogger(DictService.class);
	
	@SuppressWarnings("unchecked")
	public Map<Long, String> findByType(String type) {
		
		// redis连接的状态标志
		// 连接:数据将存储在缓存 	未连接:直接走DB
		boolean connected = false;
		
		// 判断redis是否连接
		try{
			RedisConnection connection = redis.getConnectionFactory().getConnection();
			connection.close();
			connected = true;
			log.debug("Redis连接，尝试从Redis读取数据");
		} catch (RedisConnectionFailureException e) {
			connected = false;
			log.debug("Redis尚未连接，直接从数据库读取数据");
		}
		
		// 尝试从redis中读取字典数据
		if(connected && redis.hasKey(REDIS_PREFIX + type)) {
			return  (Map<Long, String>) redis.opsForValue().get(REDIS_PREFIX + type);
		}
		
		// 直接从DB中读取字典数据
		List<Map<String, Object>> maps = dao.findByType(type);
		if(maps == null || maps.size() == 0) {
			return null;
		}
		Map<Long, String> dics = new LinkedHashMap<Long, String>();
		for(Map<String, Object> m : maps) {
			dics.put((Long)m.get("code"), (String)m.get("name"));
		}
		
		// 将数据缓存在redis中
		if(connected) {
			redis.opsForValue().set(REDIS_PREFIX + type, dics, 6, TimeUnit.HOURS);	
		}
		
		return dics;
	}
}

