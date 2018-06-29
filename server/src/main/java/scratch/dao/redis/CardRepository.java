package scratch.dao.redis;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.collections.*;
import org.springframework.stereotype.Repository;
import scratch.model.RedisKey;
import scratch.model.entity.Card;
import scratch.model.entity.MemoryCardInfo;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class CardRepository implements ICardRepository {

	@Autowired
	private RedisTemplate redisTemplate;

	private final static Logger log = Logger.getLogger(CardRepository.class);

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
	public Card find(String cardId) {
		return cards().get(cardId);
	}

	@Override
	public MemoryCardInfo findMemoryCardInfo(String brochureId, String cardId) {
		return memoryCardsInfo(brochureId).get(cardId);
	}

	/**
	 * 列出指定卡册的所有卡片
	 * @param brochureId
	 * @return
	 */
	@Override
	public List<Card> list(String brochureId) {
		List<Card> cards = cards(brochureId)
				.stream().map(this::find)
				.collect(Collectors.toList());
		return cards;
	}

	@Override
	public List<Card> listByWord(String word) {
		return cards().values().stream().filter(card ->
					(card.getValue() != null && card.getValue().contains(word)) ||
							(card.getKey() != null && card.getKey().contains(word))
		).collect(Collectors.toList());
	}

	/**
	 * 当前记忆模式中，卡册中尚未记忆的卡片
	 * @param brochureId
	 * @return
	 */
	@Override
	public List<Card> listMemory(String brochureId) {
		RedisList<String> memoryCards = memoryCards(brochureId);
		// 如果第一次记忆/上次记忆完成，则需要记忆的卡片为空
		// 需要筛选出本次需要记忆的所有卡片
		if(memoryCards.size() == 0) {
			memoryCards = doListMemory(brochureId);
		}
		// String cardId -> Card card
		List<Card> cards = idStreamtoCardList(memoryCards.stream());
		if(cards != null && cards.size() > 0) return cards;
		return idStreamtoCardList(doListMemory(brochureId).stream());
	}

	/**
	 * 筛选需要记忆的卡片，将结果存在记忆列表
	 * 筛选条件：
	 * 1.卡片记忆信息不存在（说明初次在记忆中加载）
	 * 2.超过或等于7天，没有记忆
	 * 3.总记忆次数小于5
	 * 4.记忆正确比率小于80%
	 * 附加：若经过过滤后数量小于10, 则不过滤
	 * @param brochureId
	 * @return
	 */
	private RedisList<String> doListMemory(String brochureId) {
		// 卡册中正在记忆的卡片
		RedisList<String> memoryCards = memoryCards(brochureId);
		// 每张卡片的具体记忆信息
		RedisMap<String, MemoryCardInfo> memoryCardInfoMap =  memoryCardsInfo(brochureId);
		// 卡册中的所有卡片
		List<String> cardIds = cards(brochureId).range(0, -1)
				.stream().collect(Collectors.toList());
		Long now = new Date().getTime();

		// 过滤
		List<String> filterCardIds = cardIds.stream().filter(cardId -> {

			log.debug("[选取判断]carId:" + cardId);
			MemoryCardInfo memoryCardInfo = memoryCardInfoMap.get(cardId);

			// 条件1：卡片记忆信息不存在（说明初次在记忆中加载）
			if(memoryCardInfo == null) {
				log.debug("[选取]记忆卡片信息不存在");
				return true;
			}

			// 条件2：超过或等于7天，没有记忆
			Long availableTime = memoryCardInfo.getLastTime().getTime() + 7*24*60*60*1000;
			if(availableTime <= now) {
				log.debug("[选取]记忆卡片长久没有记忆 记忆时间:" + memoryCardInfo.getLastTime()
						+ " 有效时间:" + new Date(availableTime));
				return true;
			}

			// 条件3：总记忆次数小于5
			int count = memoryCardInfo.getRemeber() + memoryCardInfo.getForget();
			if(count <= 5) {
				log.debug("[选取]总记忆次数小于5次");
				return true;
			}

			// 条件4：记忆正确比率小于80%
			double percent = (double)memoryCardInfo.getRemeber() / (double) count;
			if(percent <= 0.8) {
				log.debug("[选取]总记住比例低于0.85 目前比例:" + percent + " 记住次数:" + memoryCardInfo.getRemeber());
				return true;
			}
			return false;
		}).collect(Collectors.toList());

		// 条件5:过滤后总数小于等于10，则显示全部
		if(filterCardIds.size() <= 10) {
			filterCardIds = cardIds;
		}

		// 存放入正在记忆列表
		memoryCards.addAll(filterCardIds);
		// 有效期1天
		redisTemplate.expire(RedisKey.memoryCards(brochureId), 1, TimeUnit.DAYS);
		return memoryCards;
	}

	private List<Card> idStreamtoCardList(Stream<String> stream) {
		List<Card> cards = stream.map(this::find)
				.filter(card -> card != null)
				.collect(Collectors.toList());
		return cards;

	}

	@Override
	public void save(String brochureId, Card card) {
		cards().putIfAbsent(card.getId(), card);
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
		// 获取两张卡片的score
		RedisZSet<String> cards = cards(brochureId);
		Double firstScore = cards.score(firstCardId);
		Double secondScore = cards.score(secondCardId);

		if(firstScore == null || secondScore == null) return;
		if(firstScore.equals(secondScore)) {
			firstScore = firstScore - 0.001;
		}

		//交换score
		cards.add(firstCardId, secondScore);
		cards.add(secondCardId, firstScore);
	}

	/**
	 *
	 * @param brochureId
	 * @param firstCardId	排序到secondCard前面的Card
	 * @param secondCardId  排序到firstCard后面的Card
	 */
	@Override
	public void swapBefore(String brochureId, String firstCardId, String secondCardId) {
		if(firstCardId.equals(secondCardId)) return;
		RedisZSet<String> cards = cards(brochureId);

		// 将firstCard移到secondCard前面
		Double secondScore = cards.score(secondCardId);
		if(secondScore == null) return;
		cards.add(firstCardId, secondScore - 0.001);

	}

	public void memoryRember(String brochureId, String cardId) {
		updateMemoryCardsInfo(brochureId, cardId, (memoryCardInfo -> memoryCardInfo.increRemeber()));
		removeMemoryCard(brochureId, cardId);
	}

	public void memoryForget(String brochureId, String cardId) {
		updateMemoryCardsInfo(brochureId, cardId, (memoryCardInfo -> memoryCardInfo.increForget()));
		removeMemoryCard(brochureId, cardId);
	}

	private void updateMemoryCardsInfo(String brochureId, String cardId, Consumer<MemoryCardInfo> consumer) {
		// 获取 MemoryCardInfo
		MemoryCardInfo memoryCardInfo = memoryCardsInfo(brochureId).get(cardId);
		if(memoryCardInfo == null) {
			memoryCardInfo = new MemoryCardInfo();
		}
		log.debug("记忆卡片信息："   + memoryCardInfo);

		// do something about MemoryCardInfo
		consumer.accept(memoryCardInfo);

		// 更新 MemoryCardInfo
		memoryCardsInfo(brochureId).put(cardId, memoryCardInfo);
		if (log.isDebugEnabled()) {
			log.debug("更新记忆卡片信息："   + memoryCardsInfo(brochureId).get(cardId));
		}
	}

	private void removeMemoryCard(String brochureId, String cardId) {
		memoryCards(brochureId).remove(cardId);
		if(memoryCards(brochureId).size() == 0) {
			redisTemplate.delete(RedisKey.memoryCards(brochureId));
		}
	}

}
