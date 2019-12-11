package scratch.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import scratch.config.RabbitMQConfig;
import scratch.model.entity.AnimeEpisode;
import scratch.support.service.EmailSupport;
import scratch.support.service.MailContent;
import scratch.support.service.MailException;

@Service
public class EmailProducer {

	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	public void sendUserResetPassword(String resetUrl, String destEmail) throws MailException, MessagingException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("url", resetUrl);
		rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_EMAIL,
				RabbitMQConfig.QUEUE_EMAIL_ACTIVE,
				new MailContent("密码重置", "/mail/resetpwd", destEmail, map));

		//emailSupport.sendMail(new MailContent("密码重置", "/mail/resetpwd", destEmail , map));
	}
	
	public void sendUserActiveCode(String activeUrl, String destEmail) throws MailException, MessagingException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("url", activeUrl);

		rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_EMAIL,
				RabbitMQConfig.QUEUE_EMAIL_ACTIVE,
				new MailContent("用户激活", "/mail/register", destEmail, map));

//		emailSupport.sendMail(new MailContent("用户激活", "/mail/register", destEmail, map));
	}
	
	public void sendAnimeInfo(List<AnimeEpisode> episodes, String destEmail) throws MailException, MessagingException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("episodes", episodes);

		rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_EMAIL,
				RabbitMQConfig.QUEUE_EMAIL_ACTIVE,
				new MailContent("番剧推送", "/mail/resetpwd", destEmail, map));

		//emailSupport.sendMail(new MailContent("番剧推送", "/mail/animepsuh", destEmail, map));
	}
	
}
