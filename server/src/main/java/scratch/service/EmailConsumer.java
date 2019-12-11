package scratch.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scratch.config.RabbitMQConfig;
import scratch.support.service.EmailSupport;
import scratch.support.service.MailContent;
import scratch.support.service.MailException;

import javax.mail.MessagingException;

@Service
public class EmailConsumer {

	private EmailSupport emailSupport;

	@Autowired
	public EmailConsumer(EmailSupport emailSupport) {
		this.emailSupport = emailSupport;
	}

	@RabbitListener(queues = RabbitMQConfig.QUEUE_EMAIL_ACTIVE)
	public void consume(MailContent mailContent) throws MailException, MessagingException {
		emailSupport.sendMail(mailContent);
	}

}
