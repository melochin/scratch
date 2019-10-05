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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;

@EnableRabbit
@Configuration
public class RabbitMQConfig {

	public final static String INSTANT_QUEUE = "scratch.instant";
	public final static String TIMING_QUEUE = "scratch.timing";
	public final static String SCRATCH_EXCHANGE = "scratch";

	@Bean
	public ConnectionFactory rabbitConnectionFactory() {
		return new CachingConnectionFactory("localhost");
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
		return new DirectExchange(SCRATCH_EXCHANGE);
	}

	@Bean
	public Queue instantQueue() {
		return new Queue(INSTANT_QUEUE);
	}

	@Bean
	public Queue timingQueue() {
		return new Queue(TIMING_QUEUE);
	}

	@Bean
	public Binding instantBinding() {
		return BindingBuilder.bind(instantQueue()).to(scratchExchange()).with(INSTANT_QUEUE);
	}

	@Bean
	public Binding timingBinding() {
		return BindingBuilder.bind(timingQueue()).to(scratchExchange()).with(TIMING_QUEUE);
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
				System.out.println(e.getFailedMessage());
				return null;
			}
		};
	}

}
