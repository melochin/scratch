package scratch.dao.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.support.collections.DefaultRedisSet;
import org.springframework.stereotype.Repository;
import scratch.model.RedisKey;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class CacheLikeDao implements ILikeDao {

	@Autowired
	private RedisTemplate redisTemplate;

	private DefaultRedisSet<Long> userLikeSet(long userId) {
		return new DefaultRedisSet<Long>(RedisKey.likeSet(userId), redisTemplate);
	}

	@Override
	public long getCount(long episodeId) {
		Long count = (Long) redisTemplate.opsForValue().get(RedisKey.like(episodeId));
		return count == null ? 0 : count;
	}

	@Override
	public Set<Long> listEpisode(long userId) {
		return userLikeSet(userId).size() == 0 ?
				new HashSet<>() :
				userLikeSet(userId).stream().collect(Collectors.toSet());
	}

	@Override
	public long increase(Long episodeId) {
		ValueOperations ops = redisTemplate.opsForValue();
		Long val = (Long) ops.get(RedisKey.like(episodeId));

		val = val == null ? 1 : val + 1;
		ops.set(RedisKey.like(episodeId), new Long(val));
		return val;
	}

	@Override
	public long decrease(Long episodeId) {
		ValueOperations ops = redisTemplate.opsForValue();
		Long val = (Long) ops.get(RedisKey.like(episodeId));

		val = (val == null || val == 1) ? 0 : val - 1;
		ops.set(RedisKey.like(episodeId), val);
		return val;
	}

	@Override
	public boolean add(long userId, Long episodeId) {
		if (userLikeSet(userId).contains(episodeId)) return false;
		userLikeSet(userId).add(episodeId);
		return true;
	}

	public boolean remove(long userId, Long episodeId) {
		if (userLikeSet(userId).contains(episodeId) == false) return false;
		userLikeSet(userId).remove(episodeId);
		return true;
	}

}
