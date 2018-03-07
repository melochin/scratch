package scratch.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

	@Autowired
	private RedisTemplate redisTemplate;

	private final static Logger log = Logger.getLogger(RedisService.class);

	private static final String SPLIT = ":";


	/** 判断redis是否连接 */
	public boolean isConnected() {

		try{
			RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
			connection.close();
			log.debug("Redis连接，尝试从Redis读取数据");
			return true;
		} catch (RedisConnectionFailureException e) {
			log.debug("Redis尚未连接，直接从数据库读取数据");
		}

		return false;
	}


	public String getKey(String key1, String key2) {
		return key1 + SPLIT + key2;
	}

	public void delete(String key) {
		redisTemplate.delete(key);
	}

	public void set(String key, Object value) {
		redisTemplate.opsForValue().set(key, value);
	}

	public void set(String key1, String key2, Object value) {
		this.set(getKey(key1, key2), value);
	}

	public void set(String key, Object value, long time, TimeUnit unit) {
		redisTemplate.opsForValue().set(key, value, time, unit);
	}

	public void set(String key1, String key2, Object value, long time, TimeUnit unit) {
		set(getKey(key1, key2), value, time, unit);
	}

	public boolean equalsTo(String key, Object excepted) {
		Object value = get(key);
		if(value == null || !value.equals(excepted)) {
			return false;
		}
		return true;
	}

	public <T> T get(String key) {
		return (T)redisTemplate.opsForValue().get(key);
	}

	public <T> T get(String key1, String key2) {
		return get(getKey(key1, key2));
	}


	public void add(String key, Object value) {
		redisTemplate.opsForSet().add(key, value);
	}

	public Set list(String key) {
		return redisTemplate.opsForSet().members(key);
	}

	public void pop(String key, Object value) {
		redisTemplate.opsForSet().remove(key, value);
	}

	public Object hashGet(Object key, Object hashKey) {
		return redisTemplate.opsForHash().get(key, hashKey);
	}

	public void hashPut(Object key, Object hashKey, Object value) {
		redisTemplate.opsForHash().put(key, hashKey, value);
	}
}
