package scratch.support.service;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.test.ContextTest;

public class MailServiceTest extends ContextTest {

	@Autowired
	private EmailSupport emailSupport;
	
	@Test
	public void test() throws MailException, MessagingException {
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("url", "https://www.baidu.com/");
		emailSupport.sendMail(new MailContent("用户注册", "/mail/resetpwd.html"),  "398299262@qq.com", hashMap);
	}

}
