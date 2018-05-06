package scratch.dao;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.collections.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import scratch.model.RedisKey;
import scratch.model.entity.Card;
import scratch.model.entity.MemoryCardInfo;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Repository
public class CardV2Repository implements ICardRepository {

	@Autowired
	private RedisTemplate redisTemplate;

	private final static Logger log = Logger.getLogger(CardV2Repository.class);

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

	private RedisMap<String, MemoryCardInfo> memoryCardsInfo(String brochureId) {
		return new DefaultRedisMap<String, MemoryCardInfo>(RedisKey.memoryCardsInfo(brochureId) , redisTemplate);
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

		// 选取所有brochure关联的cardId
		// 再进行过滤
		// 过滤条件：
		// 1.记忆次数 = 记住次数 + 忘记次数 <= 5 或者 记住次数 / 记忆次数 <= 0.85
		// 2.上次记忆时间 距离目前 超过一礼拜
		// 附加：若经过过滤后数量小于10, 则不过滤

		RedisMap<String, MemoryCardInfo> memoryCardInfoMap =  memoryCardsInfo(brochureId);
		Long now = new Date().getTime();

		Set<String> cardIds = cards(brochureId).range(0, -1);

		Set<String> filterCardIds = cardIds.stream().filter(cardId -> {
			MemoryCardInfo memoryCardInfo = memoryCardInfoMap.get(cardId);
			log.debug("[选取判断]carId:" + cardId);
			if(memoryCardInfo == null) {
				log.debug("[选取]记忆卡片信息不存在");
				return true;
			}

			Long availableTime = memoryCardInfo.getLastTime().getTime() + 7*24*60*60*1000;

			if(availableTime <= now) {
				log.debug("[选取]记忆卡片长久没有记忆 记忆时间:" + memoryCardInfo.getLastTime()
						+ " 有效时间:" + new Date(availableTime));
				return true;
			}

			int count = memoryCardInfo.getRemeber() + memoryCardInfo.getForget();
			if(count <= 5) {
				log.debug("[选取]总记忆次数小于5次");
				return true;
			}

			double percent = (double)memoryCardInfo.getRemeber() / (double) count;
			if(percent <= 0.8) {
				log.debug("[选取]总记住比例低于0.85 目前比例:" + percent + " 记住次数:" + memoryCardInfo.getRemeber());
				return true;
			}
			return false;
		}).collect(Collectors.toSet());

		if(filterCardIds.size() <= 10) {
			filterCardIds = cardIds;
		}

		memoryCards.addAll(filterCardIds);
		redisTemplate.expire(RedisKey.memoryCards(brochureId), 1, TimeUnit.DAYS);
		return memoryCards;
	}

	private List<Card> memoryCardsToList(RedisList<String> memoryCards) {
		List<Card> cards = memoryCards.stream().map(this::find)
				.filter(card -> card != null)
				.collect(Collectors.toList());
		return cards;
	}

	private void removeFromMemoryList(String brochureId, String cardId) {
		memoryCards(brochureId).remove(cardId);
		if(memoryCards(brochureId).size() == 0) {
			redisTemplate.delete(RedisKey.memoryCards(brochureId));
		}
	}

	public void memoryRember(String brochureId, String cardId) {
		updateMemoryCardsInfo(brochureId, cardId, (memoryCardInfo -> memoryCardInfo.increRemeber()));
		removeFromMemoryList(brochureId, cardId);
	}

	public void memoryForget(String brochureId, String cardId) {
		updateMemoryCardsInfo(brochureId, cardId, (memoryCardInfo -> memoryCardInfo.increForget()));
		removeFromMemoryList(brochureId, cardId);
	}

	private void updateMemoryCardsInfo(String brochureId, String cardId, Consumer<MemoryCardInfo> consumer) {
		MemoryCardInfo memoryCardInfo = memoryCardsInfo(brochureId).get(cardId);
		if(memoryCardInfo == null) {
			memoryCardInfo = new MemoryCardInfo();
		}

		log.debug("记忆卡片信息："   + memoryCardInfo);

		consumer.accept(memoryCardInfo);

		memoryCardsInfo(brochureId).put(cardId, memoryCardInfo);

		if (log.isDebugEnabled()) {
			log.debug("更新记忆卡片信息："   + memoryCardsInfo(brochureId).get(cardId));
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
