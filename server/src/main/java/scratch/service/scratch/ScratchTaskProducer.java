package scratch.service.scratch;

import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scratch.config.RabbitMQConfig;
import scratch.dao.inter.IAnimeAliasDao;
import scratch.dao.inter.IAnimeDao;
import scratch.model.entity.Anime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScratchTaskProducer implements IScratchTaskProducer {

	private IAnimeDao animeDao;

	private IAnimeAliasDao animeAliasDao;

	private final static Logger log = Logger.getLogger(ScratchTaskProducer.class);

	private Map<Long, String> queueMap;

	private RabbitTemplate rabbitTemplate;

	@Autowired
	public ScratchTaskProducer(IAnimeDao animeDao,
							   IAnimeAliasDao animeAliasDao,
							   RabbitTemplate rabbitTemplate) {
		this.animeDao = animeDao;
		this.animeAliasDao = animeAliasDao;
		this.rabbitTemplate = rabbitTemplate;
		this.queueMap = new HashMap<>();
		this.queueMap.put(new Long(3), "scrach.web.renren");
	}

	public void produce() {
		for (Map.Entry<Long, String> entry : queueMap.entrySet()) {
			Long hostId = entry.getKey();
			List<Anime> animes = list(hostId);

			for (Anime anime : animes) {
				rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_SCRATCH, RabbitMQConfig.QUEUE_SCRATCH_TIMING, anime, message -> {
					// 设置有效期让message失效？
					message.getMessageProperties().setHeader("retry-count", 0);
					return message;
				});
			}
		}
	}

	@Override
	public void produceAndWait() {
		List<Anime> animes = new ArrayList<>();
		for (Map.Entry<Long, String> entry : queueMap.entrySet()) {
			Long hostId = entry.getKey();
			animes.addAll(list(hostId));
		}
		System.out.println(animes);
		rabbitTemplate.convertSendAndReceive(RabbitMQConfig.EXCHANGE_SCRATCH,
				RabbitMQConfig.QUEUE_SCRATCH_INSTANT, animes);
	}

	public List<Anime> list(Long hostId) {
		// TODO TEST - SQL
		List<Long> animeIds = animeAliasDao.listAnimeIdByHost(hostId);
		return animeIds.stream()
				.map(animeId -> {
					// TODO 级联删除 Anime对应的别名
					Anime anime = animeDao.getById(animeId);
					if (anime == null) return null;

					anime.setAliass(animeAliasDao.list(animeId));
					return anime;
				})
				.filter(anime -> anime != null)
				.collect(Collectors.toList());
	}

}
