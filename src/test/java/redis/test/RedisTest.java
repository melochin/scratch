package redis.test;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import scratch.test.ContextTest;

public class RedisTest extends ContextTest {

	@Autowired
	private StringRedisTemplate template;
	
	/**
	 * 测试Redis存取功能
	 */
	@Test
	public void setAndDeleteTest() {
		String key = "hi";
		
		template.opsForValue().set(key, "Hello");
		Assert.assertTrue(template.hasKey(key));
		
		template.delete(key);
		Assert.assertFalse(template.hasKey(key));
	}
	
}
