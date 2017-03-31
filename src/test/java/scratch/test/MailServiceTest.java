package scratch.test;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.exception.MailException;
import scratch.model.User;
import scratch.service.MailService;
import scratch.service.MailTemplate;
import scratch.service.UserService;

public class MailServiceTest extends ContextClass{

	@Autowired
	private MailService service;

	@Autowired
	private UserService userSer;
	
	@Test
	public void sendMailTest() throws MailException, MessagingException {
		Map<String, Object> model = new HashMap<>();
		model.put("url", "www.baidu.com");
		service.sendMail(MailTemplate.REGISTER, "398299262@qq.com", model);
	}
	
	@Test
	public void sendRestMailTest() throws MailException, MessagingException {
		User u = new User();
		u.setEmail("398299262@qq.com");
		userSer.sendRestMail(u);
	}
}
