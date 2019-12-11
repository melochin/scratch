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

	/**
	 * 同步
	 * @param message
	 * @param animes
	 * @return
	 */
	@RabbitListener(queues = RabbitMQConfig.QUEUE_SCRATCH_INSTANT)
	public int consumeInstant(Message message, @Payload List<Anime> animes) {
		System.out.println("get message from queue");
		for (Anime anime : animes) {
			System.out.println("do task " + anime.getName());
			doTask(message, anime);
		}
		return 0;
	}

	/**
	 * 异步
	 * @param message
	 * @param anime
	 */
	@RabbitListener(queues = {
			RabbitMQConfig.QUEUE_SCRATCH_TIMING,
			RabbitMQConfig.QUEUE_SCRATCH_DLX}, errorHandler = "consumerErrorHandler")
	public void consumeTiming(Message message, @Payload Anime anime) {
		doTask(message, anime);
	}

	private boolean doRetry(Message message, Anime anime) {
		// get retry count
		Map<String, Object> headers = message.getMessageProperties().getHeaders();
		Integer retryCount = headers.get("retry-count") == null ? 0 : (Integer) headers.get("retry-count");

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
				});
		return true;
	}

	public void doTask(Message message, Anime anime) {

		log.debug("handle task " + anime.getName());

		if (anime.getAliases().size() == 0) return;

		// TODO 目前只负责处理一个animeAlias, 多个以后版本改进
		AnimeAlias animeAlias = anime.getAliases().get(0);

		try {
			// 获取适配器
			ScratchAdpater scratch = ScratchFactory.get(animeAlias.getHostId());

			// start scratch
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
			/**
			 * 其他异常：1. 网络延迟，导致请求timeout
			 * 		   2. 请求频繁，ip被暂时阻隔
			 * 对于以上异常，考虑将消息移入延迟队列，通过等待一定时间，重试任务。
			 * 重试3次，仍没有成功，则不再重试。
			 */
			String queue = message.getMessageProperties().getConsumerQueue();

			// 第一次出现问题，加入到延迟队列，请求头加入可重试3次的信息
			if (RabbitMQConfig.QUEUE_SCRATCH_TIMING.equals(queue)) {
				// send to delay
				rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_SCRATCH,
						RabbitMQConfig.QUEUE_SCRATCH_DELAY,
						anime, message1 -> {
							message1.getMessageProperties().setHeader("retry-count", 3);
							return message1;
						});
			}

			// 多次出现问题，更新请求头重试信息
			if (RabbitMQConfig.QUEUE_SCRATCH_DLX.equals(queue) &&
					doRetry(message, anime) == false) {
				log.debug("retry many times " + anime.getName());
				// TODO FILL MESSAGE
				// 打印错误信息
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				// 保存日志信息
				scratchLog.save(new ScratchLog(animeAlias, 1, sw.toString()));
				pw.close();
			}
		}
		return;
	}

	@Transactional
	public int save(List<AnimeEpisode> scratchList) {
		// 过滤重复地址
		List<AnimeEpisodeScratch> episodeScratches = scratchList.stream()
				.filter(ep -> !animeEpisodeScratchDao.isExistUrl(ep.getUrl()))
				.map(AnimeEpisodeScratch::new)
				.collect(Collectors.toList());
		// 保存数据
		episodeScratches.forEach(ep -> animeEpisodeScratchDao.save(ep));
		return episodeScratches.size();
	}

}
