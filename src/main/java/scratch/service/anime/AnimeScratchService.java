package scratch.service.anime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

import scratch.api.dilidili.DilidiliImpl;
import scratch.api.fix.FixImpl;
import scratch.api.renren.RenrenImpl;
import scratch.dao.inter.IAnimeDao;
import scratch.dao.inter.IAnimeEpisodeDao;
import scratch.model.Anime;
import scratch.model.AnimeAlias;
import scratch.model.AnimeEpisode;
import scratch.service.reader.adpater.Adapter;
import scratch.service.reader.adpater.DilidiliAdapter;
import scratch.service.reader.adpater.FixAdapter;
import scratch.service.reader.adpater.RenrenAdapter;

@Service
public class AnimeScratchService {

	private final static Logger log = Logger.getLogger(AnimeScratchService.class);
	
	@Autowired
	private IAnimeDao animeDao;
	
	@Autowired
	private IAnimeEpisodeDao episodeDao;
	
	private static Boolean isScratchRun = false;
	
	@Autowired
	private ConnectionFactory factory;
	
	private final static String EXCHANGE_NAME = "scratch";
	
	private final static String EXCHANGE_TYPE = "direct";
	
	private static Boolean isListening = false;
	
	/**
	 * key: host code, value: anime alias list; 
	 * hsot code = 0, 认为anime没有维护任何host的alias 
	 */
	private Map<Long, List<Anime>> animeAliasMap = new LinkedHashMap<Long, List<Anime>>();
	
	/** key: host code , value: adapater */
	private Map<Long, Adapter> adpaterMap = new HashMap<Long, Adapter>();
	
	/** init adapterMap */
	{
		this.adpaterMap.put(new Long(1), new DilidiliAdapter(new DilidiliImpl()));
		this.adpaterMap.put(new Long(2), new FixAdapter(new FixImpl()));
		this.adpaterMap.put(new Long(3), new RenrenAdapter(new RenrenImpl()));
	}
	
	/** init AnimeAliasMap */
	private synchronized void init() {
		// 获取未完结的番剧，且含有别名
		List<Anime> animes = animeDao.findWithAlias();
		for(Anime anime : animes) {
			// 没有维护别名的，hostId全部默认为0，别名即为Anime name
			if(CollectionUtils.isEmpty(anime.getAliass())) {
				fillAnimeAliasMap(new Long(0), anime.getName(), anime);
			} 
			// 有别名的，存放对应hostId的AnimeList中
			else {
				
				for(AnimeAlias alias : anime.getAliass()) {
					fillAnimeAliasMap(alias.getHostId(), alias.getAlias(), anime);
				}
			}
		}
	}
	
	
	/** 填充AnimeAliasMap，为每个站点生成对应的Anime List  */
	private void fillAnimeAliasMap(Long hostId, String alias, Anime anime) {
		if(!animeAliasMap.containsKey(hostId)) {
			animeAliasMap.put(hostId, new ArrayList<Anime>());
		}
		anime.setAlias(alias);
		animeAliasMap.get(hostId).add(anime);
	}
	
	
	private synchronized List<Anime> getAnimeMap(Long hostId) {
		
		List<Anime> animes = animeAliasMap.get(hostId);
		if(animes == null) {
			animes = new ArrayList<Anime>();
		}
		
		return animes;
	}
	
	
	@Scheduled(fixedRate=60*60*1000)
	public void run() {
		
		// 运行状态判断
		synchronized (isScratchRun) {
			if(isScratchRun) {
				if(log.isDebugEnabled()) {
					log.debug("scratch service is running, waiting for next time");				
				}
				return;
			}
			isScratchRun = true;
		}
		
		// 初始化
		init();
		
		ExecutorService exec = Executors.newCachedThreadPool();
		CountDownLatch countDownLatch = new CountDownLatch(adpaterMap.size());
		ConcurrentMap<String, Integer> resultMap = new ConcurrentHashMap<String, Integer>();
		
		if(log.isInfoEnabled()) {
			log.info("start runing scratch service");
		}
		
		// 遍历所有适配器，执行数据抓取线程任务
		for(Entry<Long, Adapter> entry : adpaterMap.entrySet()) {
			
			// 获取HostId及对应的适配器
			Long hostId = entry.getKey();
			Adapter adapter = entry.getValue();
			// 加载对应的ANIME数据
			List<Anime> animes = getAnimeMap(hostId);
			
			// 网络访问任务可能超时，启用线程任务
			exec.execute(new Runnable() {
				
				@Override
				public void run() {
					int episodeCount = 0;
					// 遍历Anime，执行数据抓取
					for(Anime anime : animes) {
						List<AnimeEpisode> episodes = adapter.readAnimeEpidsode(anime);
						
						for(AnimeEpisode episode : episodes) {
							episode.setHostId(hostId);
						}
						
						try {
							pushMessage(episodes);
						} catch (IOException | TimeoutException e) {
							e.printStackTrace();
						}
						
						episodeCount += episodes.size();
					}
					// 保存抓取结果
					resultMap.put(adapter.getClass().getName(), episodeCount);
					countDownLatch.countDown();
				}
				
			});
		}
		// 结束线程，等待所有scratch任务完成
		// 1.输出运行结果 2.重置运行状态
		exec.execute(new Runnable() {

			@Override
			public void run() {
				// 等待所有scratch任务完成
				try {
					countDownLatch.await();
				} catch (InterruptedException e) {
				}
				// 输出运行结果
				if(log.isInfoEnabled()) {
					// 计算总共抓取的数据个数
					int count = 0;
					for(Entry<String, Integer> entry : resultMap.entrySet()) {
						count += entry.getValue();
					}
					log.info("end scratch service \n" + 
							"get " + count + " episode \n" +
							resultMap);
				}
				//重置运行状态
				synchronized(isScratchRun) {
					isScratchRun = false;	
				}
			}
			
		});
		
		exec.shutdown();
	}
	
	public void pushMessage(List<AnimeEpisode> animeEpisodes) throws IOException, TimeoutException {
		ObjectMapper mapper = new ObjectMapper();
		// create channel
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		// declare exchange
		channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
		// push message
		channel.basicPublish(EXCHANGE_NAME, "", null, mapper.writeValueAsBytes(animeEpisodes));
		channel.close();
		connection.close();
	}
	
	@PostConstruct
	public void startMessageListener() throws IOException, TimeoutException {
		
		synchronized (isListening) {
			if(isListening) return;
			isListening = true;
		}
		
		// create channel
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		// declare exchange
		channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
		// exchange <-> queue
		String queue = channel.queueDeclare().getQueue();
		channel.queueBind(queue, EXCHANGE_NAME, "");
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
				saveList(episodes);
				
				System.out.println("----------------保存数据成功[" + episodes.size() + "]----------------------------------");
			}
		});
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	private void saveList(List<AnimeEpisode> episodes) {
		
		for(AnimeEpisode episode : episodes) {
			if(episodeDao.findByUrl(episode.getUrl()) != null) continue;
			
			//根据别名查找番剧对象，为了将番剧与具体集建立关系
			Anime anime = animeDao.findByAlias(episode.getAnime().getName(), false);
			if(anime == null) continue;
			episode.setAnime(anime);
			episode.setScratchTime(new Date());
			//保存数据
			episodeDao.save(episode);
		}
		
		return;
	}
	
}
