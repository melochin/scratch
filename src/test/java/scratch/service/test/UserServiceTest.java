package scratch.service.test;

import javax.mail.MessagingException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import scratch.model.User;
import scratch.service.UserService;
import scratch.support.service.MailException;
import scratch.test.ContextTest;

public class UserServiceTest extends ContextTest{

	@Autowired
	private UserService userService;
	
	/**
	 * �û�ע�����
	 * 1.���������Ƿ�ᵼ���û�ע��ع���
	 * 2.���û�ע��������checked�쳣��ʹ�ʼ�����ʧ�ܣ�Ҳ���ᵼ��ע���û�����ع�
	 * @throws MailException 
	 */
	@Rollback
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Test
	public void registerTest() throws MailException {
		User user = new User("hejianok11", "hejianok");
		user.setEmail("398299262@qq.com");
		userService.add(user);
	}
	
	@Test
	public void sendResetMailTest() throws MailException, MessagingException {
		User user = new User("consequent", "consequqent");
		user.setEmail("398299262@qq.com");
		user.setUserId(new Long(111));
		userService.sendRestMail(user);
	}
	
}
