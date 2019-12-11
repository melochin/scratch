package scratch.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.listener.exception.ListenerExecutionFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@EnableRabbit
@Configuration
public class RabbitMQConfig {

	public final static String EXCHANGE_SCRATCH = "scratch";

	public final static String EXCHANGE_EMAIL = "email";

	public final static String QUEUE_SCRATCH_INSTANT = "scratch.instant";

	public final static String QUEUE_SCRATCH_TIMING = "scratch.timing";

	public final static String QUEUE_SCRATCH_DELAY = "scratch.timing.delay";

	public final static String QUEUE_SCRATCH_DLX = "scratch.timing.dlx";

	public static final String QUEUE_EMAIL_NOTIFY = "email.notify";

	public static final String QUEUE_EMAIL_ACTIVE = "email.active";

	@Value("${rabbitMQ.hostname}")
	public String hostname;

	@Bean
	public ConnectionFactory rabbitConnectionFactory() {
		return new CachingConnectionFactory(hostname);
	}

	@Bean
	public AmqpAdmin amqpAdmin() {
		return new RabbitAdmin(rabbitConnectionFactory());
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(rabbitConnectionFactory());
		rabbitTemplate.setReplyTimeout(60 * 1000L);
		return rabbitTemplate;
	}

	@Bean
	public DirectExchange scratchExchange() {
		return new DirectExchange(EXCHANGE_SCRATCH);
	}

	@Bean
	public DirectExchange emailExchange() {
		return new DirectExchange(EXCHANGE_EMAIL);
	}

	@Bean
	public Queue instantQueue() {
		return new Queue(QUEUE_SCRATCH_INSTANT);
	}

	@Bean
	public Queue timingQueue() {
		return new Queue(QUEUE_SCRATCH_TIMING);
	}

	@Bean
	public Queue delayQueue() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x-message-ttl", 30000);
		params.put("x-dead-letter-exchange", EXCHANGE_SCRATCH);
		params.put("x-dead-letter-routing-key", QUEUE_SCRATCH_DLX);
		return new Queue(QUEUE_SCRATCH_DELAY, true, false, false, params);
	}

	@Bean
	public Queue dlxQueue() {
		return new Queue(QUEUE_SCRATCH_DLX);
	}

	@Bean
	public Queue notifyQueue() {
		return new Queue(QUEUE_EMAIL_NOTIFY);
	}

	@Bean
	public Queue activeQueue() {
		return new Queue(QUEUE_EMAIL_ACTIVE);
	}

	@Bean
	public Binding instantBinding() {
		return BindingBuilder.bind(instantQueue()).to(scratchExchange()).with(QUEUE_SCRATCH_INSTANT);
	}

	@Bean
	public Binding timingBinding() {
		return BindingBuilder.bind(timingQueue()).to(scratchExchange()).with(QUEUE_SCRATCH_TIMING);
	}

	@Bean
	public Binding delayBinding() {
		return BindingBuilder.bind(delayQueue()).to(scratchExchange()).with(QUEUE_SCRATCH_DELAY);
	}

	@Bean
	public Binding dlxBinding() {
		return BindingBuilder.bind(dlxQueue()).to(scratchExchange()).with(QUEUE_SCRATCH_DLX);
	}

	@Bean
	public Binding activeBinding() {
		return BindingBuilder.bind(activeQueue()).to(emailExchange()).with(QUEUE_EMAIL_ACTIVE);
	}

	@Bean
	public Binding notifyBinding() {
		return BindingBuilder.bind(notifyQueue()).to(emailExchange()).with(QUEUE_EMAIL_NOTIFY);
	}

	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(rabbitConnectionFactory());
		return factory;
	}

	@Bean
	public RabbitListenerErrorHandler consumerErrorHandler() {
		return new RabbitListenerErrorHandler() {
			@Override
			public Object handleError(Message message, org.springframework.messaging.Message<?> message1, ListenerExecutionFailedException e) throws Exception {
				return null;
			}
		};
	}

}
