package scratch.service.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import scratch.exception.MailException;
import scratch.model.User;
import scratch.service.UserSerivce;
import scratch.test.ContextClass;

public class UserServiceTest extends ContextClass{

	@Autowired
	private UserSerivce userSerivce;
	
	/**
	 * 用户注册测试
	 * 1.邮箱事务是否会导致用户注册回滚？
	 * 2.在用户注册中引入checked异常，使邮件发送失败，也不会导致注册用户事务回滚
	 * @throws MailException 
	 */
	@Rollback
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Test
	public void registerTest() throws MailException {
		User user = new User("hejianok", "hejianok");
		user.setEmail("398299262@qq.com");
		userSerivce.add(user);
	}
	
}
