package scratch.service.scratch;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import scratch.config.RabbitMQConfig;
import scratch.dao.inter.IAnimeFocusDao;
import scratch.model.entity.AnimeEpisode;
import scratch.model.entity.AnimeFocus;
import scratch.support.service.EmailSupport;
import scratch.support.service.MailContent;
import scratch.support.service.MailException;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotifyTaskConsumer {

	@Autowired
	private IAnimeFocusDao focusDao;

	@Autowired
	private EmailSupport emailSupport;

	@RabbitListener(queues = RabbitMQConfig.QUEUE_EMAIL_NOTIFY)
	public void consume(@Payload Map<String, Object> message) {

		System.out.println("send email");

		try {
			String email = (String) message.get("email");
			List<AnimeEpisode> episodeList = (List<AnimeEpisode>) message.get("episodeList");
			List<AnimeFocus> focusList = (List<AnimeFocus>) message.get("focusList");

			sendMail(episodeList, email);
			for (AnimeFocus focus : focusList) {
				focusDao.update(focus);
			}

		} catch (MailException | MessagingException e) {
			e.printStackTrace();
		}
	}

	private void sendMail(List<AnimeEpisode> episodes, String mail) throws MailException, MessagingException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("episodes", episodes);
		emailSupport.sendMail(new MailContent("番剧推送",
				"/mail/animepsuh", mail, map));
	}
}
