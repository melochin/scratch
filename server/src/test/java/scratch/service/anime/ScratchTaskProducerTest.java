package scratch.service.anime;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import scratch.dao.inter.IAnimeAliasDao;
import scratch.dao.inter.IAnimeDao;
import scratch.service.scratch.ScratchTaskProducer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ScratchTaskProducerTest {

	@Test
	public void send() throws Exception {
		RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
		IAnimeAliasDao animeAliasDao = mock(IAnimeAliasDao.class);
		IAnimeDao animeDao = mock(IAnimeDao.class);

		Connection connection = mock(Connection.class);
		Channel channel = mock(Channel.class);

		when(channel.exchangeDeclare(anyString(), anyString())).thenReturn(null);
		doNothing().when(channel).basicPublish(anyString(), anyString(), any(), any());

		ScratchTaskProducer scratchTaskProducer = new ScratchTaskProducer(animeDao, animeAliasDao, rabbitTemplate);
		scratchTaskProducer.produce();
	}

}