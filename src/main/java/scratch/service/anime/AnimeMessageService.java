package scratch.service.anime;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
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
import scratch.dao.inter.IAnimeEpisodeScratchDao;
import scratch.model.Anime;
import scratch.model.AnimeEpisode;
import scratch.model.AnimeEpisodeScratch;

@Service
public class AnimeMessageService {
	
	@Autowired
	private IAnimeDao animeDao;
	
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
		try{
			connection = factory.newConnection();
			startListener();	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@PreDestroy
	public void destroy() throws IOException {
		if(connection!= null && connection.isOpen()) {
			connection.close();	
		}
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
		
		saveCount = episodes.stream()
				.filter(episode -> episodeDao.findByUrl(episode.getUrl()) == null)
				.mapToInt(episode -> {
					Anime anime = animeDao.getByAlias(episode.getAnime().getName(), false);
					if(anime == null) return 0;
					episode.setAnime(anime);
					episode.setScratchTime(new Date());
					//保存数据
					AnimeEpisodeScratch episodeScratch = conversionService.convert(
							episode, AnimeEpisodeScratch.class);
					episodeDao.save(episodeScratch);
					return 1;
				})
				.sum();
		
		return saveCount;
	}
}
