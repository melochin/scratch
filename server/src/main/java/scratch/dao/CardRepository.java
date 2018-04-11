package scratch.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.collections.DefaultRedisList;
import org.springframework.data.redis.support.collections.RedisList;
import org.springframework.stereotype.Repository;
import scratch.model.RedisKey;
import scratch.model.entity.Card;
import scratch.service.ListenerService;

import javax.annotation.Resource;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class CardRepository {

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private ListenerService listenerService;

	public RedisList<Card> cards(String brochureId) {
		return new DefaultRedisList<Card>(RedisKey.cards(brochureId), redisTemplate);
	}

	public int count(String brochureId) {
		return cards(brochureId).size();
	}

	public List<Card> list(String brochureId) {
		return cards(brochureId).stream().collect(Collectors.toList());
	}

	public List<Card> list(String brochureId, Integer size) {
		if(size < 0) size = 0;
		return cards(brochureId).range(0, size);
	}

	public void deleteAndSave(String brochureId, Card card) {
		cards(brochureId).remove(card);
		cards(brochureId).addLast(card);
	}

	public void save(String brochureId, Card card) {
		card.generateId();
		cards(brochureId).addLast(card);
		listenerService.handle(list(brochureId));
		return;
	}

	public void delete(String brochureId, Card card) {
		cards(brochureId).remove(card);
		listenerService.handle(list(brochureId));
		return;
	}

}
