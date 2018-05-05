package scratch.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.collections.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import scratch.model.RedisKey;
import scratch.model.entity.Card;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class CardV2Repository implements ICardRepository {

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * key:brochure:{id}:cards value:zset
	 * @param brochureId
	 * @return
	 */
	private RedisZSet<String> cards(String brochureId) {
		return new DefaultRedisZSet<String>(RedisKey.cards(brochureId), redisTemplate);
	}

	private RedisMap<String, Card> cards() {
		return new DefaultRedisMap<String, Card>(RedisKey.CARDS, redisTemplate);
	}

	private RedisList<String> memoryCards(String brochureId) {
		return new DefaultRedisList<String>(RedisKey.memoryCards(brochureId), redisTemplate);
	}

	@Override
	public List<Card> list(String brochureId) {
		List<Card> cards = cards(brochureId)
				.stream().map(this::find)
				.collect(Collectors.toList());
		return cards;
	}

	@Override
	public List<Card> listMemory(String brochureId) {
		RedisList<String> memoryCards = memoryCards(brochureId);
		// 如果不存在，则拷贝一份队列
		if(memoryCards.size() == 0) {
			memoryCards = copyForMemory(brochureId);
		}
		// 如果存在，判断关联取到的列表是否存在
		List<Card> cards = memoryCardsToList(memoryCards);
		if(cards != null && cards.size() > 0) return  cards;
		return memoryCardsToList(copyForMemory(brochureId));
	}

	private RedisList<String> copyForMemory(String brochureId) {
		RedisList<String> memoryCards = memoryCards(brochureId);
		memoryCards.addAll(cards(brochureId).range(0, -1));
		redisTemplate.expire(RedisKey.memoryCards(brochureId), 1, TimeUnit.DAYS);
		return memoryCards;
	}

	private List<Card> memoryCardsToList(RedisList<String> memoryCards) {
		List<Card> cards = memoryCards.stream().map(this::find)
				.filter(card -> card != null)
				.collect(Collectors.toList());
		return cards;
	}

	@Override
	public void memory(String brochureId, String cardId) {
		memoryCards(brochureId).remove(cardId);
		if(memoryCards(brochureId).size() == 0) {
			redisTemplate.delete(RedisKey.memoryCards(brochureId));
		}
	}

	@Override
	public void save(String brochureId, Card card) {
		// 保存到map中
		cards().putIfAbsent(card.getId(), card);
		// 保存到对应的brochure中，建立联系
		cards(brochureId).add(card.getId(), cards(brochureId).size());
	}

	@Override
	public void modify(String brochureId, Card card) {
		cards().put(card.getId(), card);
	}

	@Override
	public void delete(String brochureId, Card card) {
		cards().remove(card.getId());
		cards(brochureId).remove(card.getId());
	}

	@Override
	public void swap(String brochureId, String firstCardId, String secondCardId) {
		if(firstCardId.equals(secondCardId)) return;

		RedisZSet<String> cards = cards(brochureId);
		Double firstScore = cards.score(firstCardId);
		Double secondScore = cards.score(secondCardId);
		if(firstScore == null || secondScore == null) return;

		if(firstScore.equals(secondScore)) {
			firstScore = firstScore - 0.001;
		}
		cards.add(firstCardId, secondScore);
		cards.add(secondCardId, firstScore);
	}

	@Override
	public void swapBefore(String brochureId, String firstCardId, String secondCardId) {
		if(firstCardId.equals(secondCardId)) return;
		RedisZSet<String> cards = cards(brochureId);


		cards.scan().forEachRemaining(id -> {
			System.out.println("id:" + id + ":" + cards.score(id));
		});

		Double secondScore = cards.score(secondCardId);
		if(secondScore == null) return;
		cards.add(firstCardId, secondScore - 0.001);

		System.out.println("排序后");
		cards.scan().forEachRemaining(id -> {
			System.out.println("id:" + cards.score(id));
		});
	}





	public Card find(String cardId) {
		return cards().get(cardId);
	}
}
