package scratch.dao;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.support.collections.RedisList;
import org.springframework.stereotype.Repository;
import scratch.model.RedisKey;
import scratch.model.entity.Brochure;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Repository
public class BrochureRepository {

	@Resource(name="redisTemplate")
	private HashOperations<String, String, Brochure> operations;


	public List<Brochure> list() {
		List<Brochure> brochures = operations.entries(RedisKey.BROCHURES)
				.entrySet().stream()
				.map(Map.Entry::getValue)
				.collect(Collectors.toList());
		brochures.sort(Comparator.comparing(Brochure::getName));
		return brochures;
	}

	public Brochure find(String brochureId) {
		return operations.get(RedisKey.BROCHURES, brochureId);
	}


	public void save(Brochure brochure) {
		operations.putIfAbsent(RedisKey.BROCHURES,
				brochure.getId(), brochure);
	}

	public void modify(Brochure brochure) {
		operations.put(RedisKey.BROCHURES,
				brochure.getId(), brochure);
	}

	public void delete(String brochureId) {
		operations.delete(RedisKey.BROCHURES, brochureId);
	}

}
