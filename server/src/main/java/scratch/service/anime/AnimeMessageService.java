package scratch.service.anime;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;

import scratch.dao.inter.IAnimeEpisodeScratchDao;
import scratch.model.entity.AnimeEpisode;
import scratch.model.entity.AnimeEpisodeScratch;

@Service
public class AnimeMessageService {

	@Autowired
	private IAnimeEpisodeScratchDao episodeDao;

	@Autowired
	private ConversionService conversionService;

	@Autowired
	private ConnectionFactory factory;

	private Connection connection;

	private final static String EXCHANGE_NAME = "scratch";

	private final static String EXCHANGE_TYPE = "direct";

	private final static String ROUTE_KEY = "save";

	@PostConstruct
	private void init() throws IOException, TimeoutException {
		try {
			connection = factory.newConnection();
			startListener();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@PreDestroy
	public void destroy() throws IOException {
		if (connection != null && connection.isOpen()) {
			System.out.println("释放连接");
			connection.close();
		}
	}

	private void startListener() throws IOException, TimeoutException {
		// create channel
		Channel channel = connection.createChannel();
		// declare exchange
		channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE, true);
		// exchange <-> queue
		String queue = channel.queueDeclare().getQueue();
		channel.queueBind(queue, EXCHANGE_NAME, ROUTE_KEY);
		// waiting for message
		channel.basicConsume(queue, false, new DefaultConsumer(channel) {

			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				try {
					System.out.println("----------------接收数据----------------------------------");
					ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(body));
					List<AnimeEpisode> episodes = (List<AnimeEpisode>) in.readObject();
					int saveCount = saveList(episodes);
					LogMessage.add("[完成]" + episodes.get(0).getAnime().getName() + "保存数据" + saveCount + "条");
					System.out.println("----------------保存数据成功[" + saveCount + "]----------------------------------");
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void push(List<AnimeEpisode> animeEpisodes) throws IOException, TimeoutException {
		ObjectMapper mapper = new ObjectMapper();
		// create channel
		Channel channel = connection.createChannel();
		// declare exchange
		channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE, true);
		// push message
		System.out.println("----------------发送数据成功[" + animeEpisodes.size() + "]----------------------------------");
		ByteArrayOutputStream byoutput = new ByteArrayOutputStream();
		ObjectOutputStream output = new ObjectOutputStream(byoutput);
		output.writeObject(animeEpisodes);
		byte[] bytes = byoutput.toByteArray();
		channel.basicPublish(EXCHANGE_NAME, ROUTE_KEY, null, bytes);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private int saveList(List<AnimeEpisode> episodes) {
		int saveCount = 0;
		saveCount = episodes.stream()
				.filter(this::isNotExistInDB)
				.mapToInt(episode -> {
					AnimeEpisodeScratch episodeScratch = conversionService.convert(
							episode, AnimeEpisodeScratch.class);
					episodeDao.save(episodeScratch);
					return 1;
				})
				.sum();
		return saveCount;
	}

	private boolean isNotExistInDB(AnimeEpisode episode) {
		return episodeDao.findByUrl(episode.getUrl()) == null;
	}
}
