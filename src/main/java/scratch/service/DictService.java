package scratch.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import scratch.dao.inter.IDictDao;
import scratch.model.Dict;

@Service
public class DictService {
	
	@Autowired 
	private IDictDao dao;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redis;
	
	private final static String REDIS_PREFIX = "dic_";
	
	private final static Logger log = Logger.getLogger(DictService.class);
	
	@Transactional
	public int save(Dict dict) {
		return dao.save(dict);
	}
	
	@Transactional
	public int update(Dict dict) {
		return dao.update(dict);
	}
	
	@Transactional
	public int delete(Dict dict) {
		return dao.delete(dict);
	}
	
	public List<Dict> findAllDictionaries() {
		return dao.findByParentCode("-1", false);
	}
	
	public Dict findByCodeAndParentCode(String code, String parentCode) {
		return dao.findByCodeAndParentCode(code, parentCode);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Dict> findByType(String parentCode) {
		
		// redis连接的状态标志
		// 连接:数据将存储在缓存 	未连接:直接走DB
		boolean connected = isRedisConnected();
		
		// 尝试从redis中读取字典数据
		if(connected && redis.hasKey(REDIS_PREFIX + parentCode)) {
			return  (List<Dict>) redis.opsForValue().get(REDIS_PREFIX + parentCode);
		}
		
		// 直接从DB中读取字典数据
		List<Dict> dicts = dao.findByParentCode(parentCode, true);
		if(dicts == null || dicts.size() == 0) {
			return null;
		}
		// 将数据缓存在redis中
		if(connected) {
			redis.opsForValue().set(REDIS_PREFIX + parentCode, dicts, 6, TimeUnit.HOURS);	
		}
		
		return dicts;
	}
	
	/** 判断redis是否连接 */
	private boolean isRedisConnected() {
		
		boolean connected = false;
		
		try{
			RedisConnection connection = redis.getConnectionFactory().getConnection();
			connection.close();
			connected = true;
			log.debug("Redis连接，尝试从Redis读取数据");
		} catch (RedisConnectionFailureException e) {
			connected = false;
			log.debug("Redis尚未连接，直接从数据库读取数据");
		}
		
		return connected;
	}
}

