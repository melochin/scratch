package scratch.dao;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import scratch.model.RedisKey;
import scratch.model.entity.Brochure;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Repository
public class BrochureRepository {

	@Resource(name="redisTemplate")
	private HashOperations<String, String, Brochure> operations;

	@Resource(name="redisTemplate")
	private ValueOperations<String, Integer> valueOperations;

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

	public Integer findMemory(String brochureId) {
		return valueOperations.get(RedisKey.memory(brochureId));
	}

	public void modifyMemory(String brochureId, Integer remainSize) {
		if(remainSize == 0) {
			valueOperations.getOperations().delete(RedisKey.memory(brochureId));
			return;
		}
		valueOperations.set(RedisKey.memory(brochureId),remainSize, 1, TimeUnit.DAYS);
	}

	public void delete(String brochureId) {
		operations.delete(RedisKey.BROCHURES, brochureId);
	}

}
