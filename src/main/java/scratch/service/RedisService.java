package scratch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

	@Autowired
	private RedisTemplate redisTemplate;

	private static final String SPLIT = ":";

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

}
