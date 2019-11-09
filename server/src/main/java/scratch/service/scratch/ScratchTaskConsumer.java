package scratch.service.scratch;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scratch.api.exception.ParseException;
import scratch.config.RabbitMQConfig;
import scratch.dao.inter.IAnimeEpisodeScratchDao;
import scratch.dao.inter.IScratchLogDao;
import scratch.model.entity.*;
import scratch.service.anime.ScratchFactory;
import scratch.service.adpater.ScratchAdpater;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScratchTaskConsumer implements IScratchTaskConsumer {

	private IAnimeEpisodeScratchDao animeEpisodeScratchDao;

	private IScratchLogDao scratchLog;

	private RabbitTemplate rabbitTemplate;

	private final static Logger log = Logger.getLogger(ScratchTaskProducer.class);

	@Autowired
	public ScratchTaskConsumer(
			IAnimeEpisodeScratchDao animeEpisodeScratchDao,
			IScratchLogDao scratchLog,
			RabbitTemplate rabbitTemplate) {
		this.animeEpisodeScratchDao = animeEpisodeScratchDao;
		this.scratchLog = scratchLog;
		this.rabbitTemplate = rabbitTemplate;
	}

	@RabbitListener(queues = RabbitMQConfig.QUEUE_SCRATCH_INSTANT)
	public int consumeInstant(Message message, @Payload List<Anime> animes) {
		System.out.println("get message from queue");
		for(Anime anime : animes) {
			System.out.println("do task " + anime.getName());
			doTask(message, anime);
		}
		return 0;
	}

	@RabbitListener(queues = {
			RabbitMQConfig.QUEUE_SCRATCH_TIMING,
			RabbitMQConfig.QUEUE_SCRATCH_DLX}, errorHandler = "consumerErrorHandler")
	public void consumeTiming(Message message, @Payload Anime anime) {
			doTask(message, anime);
	}

	private boolean doRetry(Message message, Anime anime) {
		// get retry count
		Map<String, Object> headers = message.getMessageProperties().getHeaders();
		Integer retryCount = headers.get("retry-count") == null ? 0 :(Integer)headers.get("retry-count");

		log.debug(anime.getName() + " retry count " + retryCount);

		if (retryCount == 0) return false;
		rabbitTemplate.convertAndSend(
				RabbitMQConfig.EXCHANGE_SCRATCH,
				RabbitMQConfig.QUEUE_SCRATCH_DELAY,
				anime,
				message1 -> {
					message1.getMessageProperties().getHeaders().put("retry-count", retryCount - 1);
					message1.getMessageProperties().setRedelivered(true);
					return message1;
		}		);
		return true;
	}

	// TODO 目前只负责处理一个animeAlias, 多个以后版本改进
	public void doTask(Message message, Anime anime) {

		log.debug("handle task " + anime.getName());

		// 遍历别名
		if (anime.getAliases().size() == 0) return;
		AnimeAlias animeAlias = anime.getAliases().get(0);
		// 获取适配器
		try {
			// start scratch
			ScratchAdpater scratch = ScratchFactory.get(animeAlias.getHostId());
			List<AnimeEpisode> episodes = scratch.readAnimeEpidsodes(anime, animeAlias.getName());
			// set default value
			episodes.forEach(animeEpisode -> animeEpisode.setHostId(animeAlias.getHostId()));
			// save result
			int count = save(episodes);
			scratchLog.save(new ScratchLog(animeAlias, 0, String.valueOf(count)));
		} catch (ParseException e) {
			// 解析出错 说明网站结构发生变化, 不再重新入队
			// TODO FILL MESSAGE
			scratchLog.save(new ScratchLog(animeAlias, 1, e.getMessage()));
		} catch (Exception e) {
			// 其他异常 重新尝试入队
			String queue = message.getMessageProperties().getConsumerQueue();
			if (RabbitMQConfig.QUEUE_SCRATCH_TIMING.equals(queue)) {
				// send to delay
				rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_SCRATCH,
						RabbitMQConfig.QUEUE_SCRATCH_DELAY,
						anime, message1 ->{
							message1.getMessageProperties().setHeader("retry-count", 3);
							return message1;
						});
			}
			if (RabbitMQConfig.QUEUE_SCRATCH_DLX.equals(queue)) {
				if (doRetry(message, anime) == false) {
					log.debug("retry many times " + anime.getName());
					// TODO FILL MESSAGE
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					scratchLog.save(new ScratchLog(animeAlias, 1, sw.toString()));
					pw.close();
				}
			}
		}
		return;
	}

	@Transactional
	public int save(List<AnimeEpisode> animeEpisodeScratchList) {
		// 过滤重复地址
		List<AnimeEpisodeScratch> animeEpisodeScratches = animeEpisodeScratchList.stream()
				.filter(ep -> !animeEpisodeScratchDao.isExistUrl(ep.getUrl()))
				.map(AnimeEpisodeScratch::new)
				.collect(Collectors.toList());
		// 保存数据
		animeEpisodeScratches.forEach(ep -> animeEpisodeScratchDao.save(ep));
		return animeEpisodeScratches.size();
	}

}
