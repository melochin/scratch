package scratch.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;
import scratch.model.RedisKey;
import scratch.model.entity.Card;
import scratch.service.ListenerService;

import javax.annotation.Resource;
import java.util.LinkedHashSet;
import java.util.Set;

@Repository
public class CardRepository {

	@Resource(name="redisTemplate")
	private HashOperations<String, String, Set<Card>> operations;

	@Autowired
	private ListenerService listenerService;

	public Set<Card> list(String brochureId) {
		return operations.get(RedisKey.CARDS, brochureId);
	}

	public void save(String brochureId, Card card) {
		card.generateId();
		Set<Card> cards = operations.get(RedisKey.CARDS, brochureId);
		if(cards == null) {
			cards = new LinkedHashSet<>();
		}
		cards.add(card);
		operations.put(RedisKey.CARDS, brochureId, cards);
		listenerService.handle(cards);
		return;
	}


	public void delete(String brochureId, Card card) {
		Set<Card> cards = operations.get(RedisKey.CARDS, brochureId);
		cards.remove(card);
		operations.put(RedisKey.CARDS, brochureId, cards);
		listenerService.handle(cards);
		return;
	}

}
