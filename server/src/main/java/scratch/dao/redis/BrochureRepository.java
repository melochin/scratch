package scratch.dao.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.collections.DefaultRedisMap;
import org.springframework.data.redis.support.collections.RedisMap;
import org.springframework.stereotype.Repository;
import scratch.model.RedisKey;
import scratch.model.entity.Brochure;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Repository
public class BrochureRepository implements IBrochureRepository {

	@Autowired
	private RedisTemplate redisTemplate;

	private RedisMap<String, Brochure> brochures() {
		return new DefaultRedisMap<String, Brochure>(RedisKey.BROCHURES, redisTemplate);
	}

	public Brochure find(String brochureId) {
		return brochures().get(brochureId);
	}

	public List<Brochure> list() {
		List<Brochure> brochures = brochures().values()
				.stream().collect(Collectors.toList());
		brochures.sort(Comparator.comparing(Brochure::getName));
		return brochures;
	}

	public Brochure save(Brochure brochure) {
		brochures().putIfAbsent(brochure.getId(), brochure);
		return brochures().get(brochure.getId());
	}

	public Brochure modify(Brochure brochure) {
		brochures().put(brochure.getId(), brochure);
		return brochures().get(brochure.getId());
	}

	public Brochure delete(String brochureId) {
		Brochure brochure = brochures().get(brochureId);
		if (brochure == null) return null;
		brochures().remove(brochureId);
		return brochure;
	}

}
