package scratch.service.anime;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

import scratch.dao.inter.IAnimeDao;
import scratch.dao.inter.IAnimeEpisodeDao;
import scratch.model.Anime;
import scratch.model.AnimeEpisode;

@Service
public class AnimeMessageService {
	
	@Autowired
	private IAnimeDao animeDao;
	
	@Autowired
	private IAnimeEpisodeDao episodeDao;

	@Autowired
	private ConnectionFactory factory;
	
	private Connection connection;
	
	private final static String EXCHANGE_NAME = "scratch";
	
	private final static String EXCHANGE_TYPE = "direct";
	
	private final static String ROUTE_KEY = "save";
	
	@PostConstruct
	private void init() throws IOException, TimeoutException {
		connection = factory.newConnection();
		startListener();
	}
	
	private void startListener() throws IOException, TimeoutException {
		// create channel
		Channel channel = connection.createChannel();
		// declare exchange
		channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
		// exchange <-> queue
		String queue = channel.queueDeclare().getQueue();
		channel.queueBind(queue, EXCHANGE_NAME, ROUTE_KEY);
		// waiting for message
		channel.basicConsume(queue, true, new DefaultConsumer(channel) {
			
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				
				System.out.println("----------------接收数据----------------------------------");
				
				ObjectMapper mapper = new ObjectMapper();
				@SuppressWarnings("deprecation")
				JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, AnimeEpisode.class);
				List<AnimeEpisode> episodes = mapper.readValue(body, javaType);
				int saveCount = saveList(episodes);
				
				System.out.println("----------------保存数据成功[" + saveCount + "]----------------------------------");
			}
		});
	}
	
	
	public void push(List<AnimeEpisode> animeEpisodes) throws IOException, TimeoutException {
		ObjectMapper mapper = new ObjectMapper();
		// create channel
		Channel channel = connection.createChannel();
		// declare exchange
		channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
		// push message
		channel.basicPublish(EXCHANGE_NAME, ROUTE_KEY, null, mapper.writeValueAsBytes(animeEpisodes));
		channel.close();
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	private int saveList(List<AnimeEpisode> episodes) {
		int saveCount = 0;
		
		for(AnimeEpisode episode : episodes) {
			if(episodeDao.findByUrl(episode.getUrl()) != null) continue;
			
			//根据别名查找番剧对象，为了将番剧与具体集建立关系
			Anime anime = animeDao.findByAlias(episode.getAnime().getName(), false);
			if(anime == null) continue;
			episode.setAnime(anime);
			episode.setScratchTime(new Date());
			//保存数据
			episodeDao.save(episode);
			saveCount++;
		}
		
		return saveCount;
	}
}
