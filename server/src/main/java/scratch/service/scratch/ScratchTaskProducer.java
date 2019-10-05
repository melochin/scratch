package scratch.service.anime;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scratch.dao.inter.IAnimeAliasDao;
import scratch.dao.inter.IAnimeDao;
import scratch.model.entity.Anime;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
public class ScratchTaskProducer {

	private ConnectionFactory connectionFactory;

	private IAnimeDao animeDao;

	private IAnimeAliasDao animeAliasDao;

	private final static String EXCHANGE_NAME = "scratch.web";

	private final static String EXCHANGE_TYPE = "direct";

	private final static Logger log = Logger.getLogger(ScratchTaskProducer.class);

	private Map<Long, String> queueMap;

	@Autowired
	public ScratchTaskProducer(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
		this.animeDao = animeDao;
		this.animeAliasDao = animeAliasDao;
		this.queueMap = new HashMap<>();
		this.queueMap.put(new Long(2), "scrach.web.renren");
	}

	public void send() throws IOException, TimeoutException {
		System.out.println("produce task");
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);

		for (Map.Entry<Long, String> entry : queueMap.entrySet()) {
			Long hostId = entry.getKey();
			List<Anime> animes = list(hostId);
			for(Anime anime : animes) {
				channel.basicPublish(EXCHANGE_NAME, entry.getValue(), null,
						new Gson().toJson(anime).getBytes());
			}
		}
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
