package scratch.test;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.model.User;
import scratch.service.MailTemplate;
import scratch.service.UserService;
import scratch.support.service.MailException;
import scratch.support.service.MailService;

public class MailServiceTest extends ContextTest{

	@Autowired
	private MailService service;

	@Autowired
	private UserService userSer;
	
	@Test
	public void sendMailTest() throws MailException, MessagingException {
		Map<String, Object> model = new HashMap<>();
		model.put("url", "www.baidu.com");
		service.sendMail(MailTemplate.REGISTER.toString(), "398299262@qq.com", model);
	}
	
	@Test
	public void sendRestMailTest() throws MailException, MessagingException {
		User u = new User();
		u.setEmail("398299262@qq.com");
		userSer.sendRestMail(u);
	}
}
