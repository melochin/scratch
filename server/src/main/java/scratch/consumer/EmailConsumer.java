package scratch.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import scratch.config.RabbitMQConfig;
import scratch.model.entity.AnimeEpisode;
import scratch.model.entity.AnimeFocus;
import scratch.service.EmailService;
import scratch.support.service.EmailSupport;
import scratch.support.service.MailContent;
import scratch.support.service.MailException;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Map;

@Service
public class EmailConsumer {

	private EmailSupport emailSupport;

	@Autowired
	public EmailConsumer(EmailSupport emailSupport) {
		System.out.println("初始化 email support");
		this.emailSupport = emailSupport;
	}

/*
	@RabbitListener(queues = RabbitMQConfig.QUEUE_EMAIL_NOTIFY)
	public void consume(@Payload Map<String, Object> message) {

		System.out.println("send email");

		try {
			String email = (String) message.get("email");
			List<AnimeEpisode> episodeList = (List<AnimeEpisode>) message.get("episodeList");
			List<AnimeFocus> focusList = (List<AnimeFocus>) message.get("focusList");

			emailService.sendAnimeInfo(episodeList, email);
			for (AnimeFocus focus : focusList) {
				focusDao.update(focus);
			}

		} catch (MailException | MessagingException e) {
			e.printStackTrace();
		}
	}
*/

	@RabbitListener(queues = RabbitMQConfig.QUEUE_EMAIL_ACTIVE)
	public void consume(MailContent mailContent) throws MailException, MessagingException {
		emailSupport.sendMail(mailContent);
	}

}
