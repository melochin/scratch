package scratch.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import scratch.model.entity.AnimeEpisode;
import scratch.support.service.EmailSupport;
import scratch.support.service.MailContent;
import scratch.support.service.MailException;

@Service
public class EmailService {

	@Autowired
	private EmailSupport emailSupport;
	
	public void sendUserResetPassword(String resetUrl, String destEmail) throws MailException, MessagingException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("url", resetUrl);
		emailSupport.sendMail(new MailContent("密码重置", "/mail/resetpwd"), destEmail , map);
	}
	
	public void sendUserActiveCode(String activeUrl, String destEmail) throws MailException, MessagingException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("url", activeUrl);
		emailSupport.sendMail(new MailContent("用户激活", "/mail/register"), destEmail, map);
	}
	
	public void sendAnimeInfo(List<AnimeEpisode> episodes, String destEmail) throws MailException, MessagingException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("episodes", episodes);
		emailSupport.sendMail(new MailContent("番剧推送", "/mail/animepsuh"), destEmail, map);
	}
	
}
